/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.mojos;

import com.viae.maven.sonar.config.SonarStrings;
import com.viae.maven.sonar.exceptions.SonarQualityException;
import com.viae.maven.sonar.services.SonarQualityGateService;
import com.viae.maven.sonar.services.SonarQualityGateServiceImpl;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.sonar.wsclient.SonarClient;

/**
 * Created by Vandeperre Maarten on 03/05/2016.
 */
@Mojo(name = SonarStrings.MOJO_NAME_LINK_QUALITY_GATE, aggregator = true)
public class SonarMavenLinkProjectToQualityGateMojo extends AbstractMojo {
	@Parameter(property = SonarStrings.SERVER, required = true)
	protected String sonarServer;
	@Parameter(property = SonarStrings.PROJECT_KEY)
	protected String sonarKey;
	@Parameter(property = SonarStrings.LOGIN, required = true)
	protected String sonarUser;
	@Parameter(property = SonarStrings.PASSWORD, required = true)
	protected String sonarPassword;
	@Parameter(property = SonarStrings.QUALITY_GATE, required = true)
	protected String qualityGateName;
	@Parameter(property = SonarStrings.BRANCH)
	protected String branchName;

	@Component
	protected MavenProject project;

	private final SonarQualityGateService qualityGateService = new SonarQualityGateServiceImpl(getLog());

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info( String.format( "%s start execution of '%s'", SonarStrings.LOG_PREFIX, SonarStrings.MOJO_NAME_LINK_QUALITY_GATE ) );
		getLog().info( String.format( "%s use sonar server '%s' and log in with user '%s'", SonarStrings.LOG_PREFIX, sonarServer, sonarUser ) );
		try {
			final SonarClient client = SonarClient.builder()
			                                      .url( sonarServer )
			                                      .login( sonarUser )
			                                      .password( sonarPassword )
			                                      .build();

			final String projectKey = qualityGateService.composeSonarProjectKey( project, sonarKey, branchName );
			getLog().info( String.format( "%s property '%s': %s", SonarStrings.LOG_PREFIX, SonarStrings.LOGIN, sonarUser ) );
			final int passwordLength = sonarPassword != null ? sonarPassword.length() : 0;
			getLog().info( String.format( "%s property length '%s': %s", SonarStrings.LOG_PREFIX, SonarStrings.PASSWORD, passwordLength ) );
			getLog().info( String.format( "%s property '%s': %s", SonarStrings.LOG_PREFIX, SonarStrings.PROJECT_KEY, sonarKey ) );
			getLog().info( String.format( "%s property '%s': %s", SonarStrings.LOG_PREFIX, SonarStrings.BRANCH, branchName ) );
			getLog().info( String.format( "%s computed project key: %s", SonarStrings.LOG_PREFIX, projectKey ) );
			getLog().info( String.format( "%s property '%s': %s", SonarStrings.LOG_PREFIX, SonarStrings.QUALITY_GATE, qualityGateName ) );
			getLog().info( String.format( "%s property '%s': %s", SonarStrings.LOG_PREFIX, SonarStrings.PROJECT_KEY, projectKey ) );
			getLog().info( String.format( "%s link project '%s' to quality gate %s", SonarStrings.LOG_PREFIX, projectKey, qualityGateName ) );
			qualityGateService.linkQualityGateToProject( client, projectKey, qualityGateName );
		}
		catch ( final Exception e ) {
			getLog().error( String.format( "%s %s", SonarStrings.LOG_PREFIX, e.getLocalizedMessage() ) );
			throw new MojoFailureException( String.format( "%s %s\ncause:\n%s",
			                                               SonarStrings.LOG_PREFIX,
			                                               e.getLocalizedMessage(),
			                                               ExceptionUtils.getStackTrace( e ) )
					, e );
		}
	}
}
