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
 * Mojo to set the sonar.execution.start property to the last run timestamp.
 * <p>
 * Created by Vandeperre Maarten on 29/04/2016.
 */
@Mojo(name = SonarStrings.MOJO_NAME_SET_EXECUTION_START, aggregator = true)
public class SonarMavenInitSonarExecutionStartMojo extends AbstractMojo {

	private final SonarQualityGateService qualityGateService = new SonarQualityGateServiceImpl( getLog() );
	@Parameter(property = SonarStrings.SERVER, required = true)
	protected String sonarServer;
	@Parameter(property = SonarStrings.PROJECT_KEY)
	protected String sonarKey;
	@Parameter(property = SonarStrings.LOGIN, required = true)
	protected String sonarUser;
	@Parameter(property = SonarStrings.PASSWORD, required = true)
	protected String sonarPassword;
	@Parameter(property = SonarStrings.BRANCH)
	protected String branchName;
	@Parameter(property = SonarStrings.QUALITY_GATE, required = true)
	protected String qualityGateName;
	@Component
	protected MavenProject project;

	/**
	 * Set the sonar.execution.start property to the last run timestamp (if the property is not defined).
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info( String.format( "%s start execution of '%s'", SonarStrings.LOG_PREFIX, SonarStrings.MOJO_NAME_SET_EXECUTION_START ) );
		getLog().info( String.format( "%s use sonar server '%s' and log in with user '%s'", SonarStrings.LOG_PREFIX, sonarServer, sonarUser ) );
		final String existingExecutionStart = project.getProperties().getProperty( SonarStrings.EXECUTION_START );

		getLog().info( String.format( "%s existing %s: '%s'", SonarStrings.LOG_PREFIX, SonarStrings.EXECUTION_START, existingExecutionStart ) );
		if ( StringUtils.isBlank( existingExecutionStart ) ) {
			try {
				final SonarClient client = SonarClient.builder()
				                                      .url( sonarServer )
				                                      .login( sonarUser )
				                                      .password( sonarPassword )
				                                      .build();
				final LocalDateTime lastRunTimeStamp =
						qualityGateService.getLastRunTimeStamp( client, qualityGateService.composeSonarProjectKey( project, sonarKey, branchName ), qualityGateName );

				getLog().info( String.format( "%s last run timestamp (i.e. from sonar): '%s'", SonarStrings.LOG_PREFIX, lastRunTimeStamp ) );
				final LocalDateTime executionStart = lastRunTimeStamp != null ? lastRunTimeStamp : LocalDateTime.now();
				final String executionStartValue = String.valueOf( DateTimeFormatter.ISO_DATE_TIME.format( executionStart ) );
				getLog().info( String.format( "%s set property '%s' to '%s'", SonarStrings.LOG_PREFIX, SonarStrings.EXECUTION_START, executionStartValue ) );
				project.getProperties().setProperty( SonarStrings.EXECUTION_START, executionStartValue );
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
}
