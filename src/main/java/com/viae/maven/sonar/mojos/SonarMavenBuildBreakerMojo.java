/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.mojos;

import com.viae.maven.sonar.config.SonarStrings;
import com.viae.maven.sonar.services.SonarQualityGateService;
import com.viae.maven.sonar.services.SonarQualityGateServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.sonar.wsclient.SonarClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * MOJO to validate a project against a given quality gate.
 * <p>
 * Created by Vandeperre Maarten on 29/04/2016.
 */
@Mojo(name = SonarStrings.MOJO_NAME_VALIDATE_QUALITY_GATE, aggregator = true)
public class SonarMavenBuildBreakerMojo extends AbstractMojo {
	public static final int FIVE_MINUTES_IN_SECONDS = 500;
	private final SonarQualityGateService qualityGateService = new SonarQualityGateServiceImpl( getLog() );
	@Parameter(property = SonarStrings.SERVER, required = true)
	protected String sonarServer;
	@Parameter(property = SonarStrings.PROJECT_KEY)
	protected String sonarKey;
	@Parameter(property = SonarStrings.BRANCH)
	protected String branchName;
	@Parameter(property = SonarStrings.LOGIN, required = true)
	protected String sonarUser;
	@Parameter(property = SonarStrings.PASSWORD, required = true)
	protected String sonarPassword;
	@Parameter(property = SonarStrings.EXECUTION_START)
	protected String sonarExecutionStart;
	@Parameter(property = SonarStrings.QUALITY_GATE, required = true)
	protected String qualityGateName;
	@Component
	protected MavenProject project;

	/**
	 * Validate a project against a given quality gate.
	 *
	 * @throws MojoExecutionException will not be thrown.
	 * @throws MojoFailureException   whll be thrown when the quality gate is not met by the given project.
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info( String.format( "%s start execution of '%s'", SonarStrings.LOG_PREFIX, SonarStrings.MOJO_NAME_VALIDATE_QUALITY_GATE ) );
		getLog().info( String.format( "%s use sonar server '%s' and log in with user '%s'", SonarStrings.LOG_PREFIX, sonarServer, sonarUser ) );

		try {
			final SonarClient client = SonarClient.builder()
			                                      .url( sonarServer )
			                                      .login( sonarUser )
			                                      .password( sonarPassword )
			                                      .build();

			getLog().info( String.format( "validate quality gate for %s[%s] and branch [%s]", SonarStrings.PROJECT_KEY, sonarKey, branchName ) );
			final String computedProjectKey = qualityGateService.composeSonarProjectKey( project, sonarKey, branchName );
			getLog().info( String.format( "%s property '%s': %s", SonarStrings.LOG_PREFIX, SonarStrings.PROJECT_KEY, sonarKey ) );
			getLog().info( String.format( "%s property '%s': %s", SonarStrings.LOG_PREFIX, SonarStrings.BRANCH, branchName ) );
			getLog().info( String.format( "%s computed project key: %s", SonarStrings.LOG_PREFIX, computedProjectKey ) );
			if ( !StringUtils.isBlank( sonarExecutionStart ) ) {
				final LocalDateTime executionStart = LocalDateTime.parse( sonarExecutionStart, DateTimeFormatter.ISO_DATE_TIME );
				qualityGateService.validateQualityGate( client, computedProjectKey, qualityGateName, executionStart, FIVE_MINUTES_IN_SECONDS );
			}
			else {
				qualityGateService.validateQualityGate( client, computedProjectKey, qualityGateName );
			}
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
