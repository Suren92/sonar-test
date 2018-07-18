/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.services;

import com.viae.maven.sonar.exceptions.SonarQualityException;
import org.apache.maven.project.MavenProject;
import org.sonar.wsclient.SonarClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service to validate (a) SONAR quality gate(s).
 * <p>
 * Created by Vandeperre Maarten on 30/04/2016.
 */
public interface SonarQualityGateService {
	DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern( "yyyy-MM-dd'T'HH:mm:ssZZ" );

	/**
	 * Validate if the quality gate linked to the given project is passed.
	 *
	 * @param client,          the SONAR configuration.
	 * @param projectKey,      the identifier of the project (e.g. groupId:ArtifactId:branchId).
	 * @param qualityGateName, the name of the quality gate.
	 * @throws SonarQualityException will be thrown when the given project doesn't pass the linked quality gate.
	 */
	default void validateQualityGate( final SonarClient client, final String projectKey, String qualityGateName ) throws SonarQualityException {
		validateQualityGate( client, projectKey, qualityGateName, null, -1 );
	}

	/**
	 * Validate if the quality gate linked to the given project is passed.
	 *
	 * @param client,          the SONAR configuration.
	 * @param projectKey,      the identifier of the project (e.g. groupId:ArtifactId:branchId).
	 * @param qualityGateName, the name of the quality gate.
	 * @param executionStart,  a timestamp before the sonar validation run started (e.g. the last run timestamp).
	 * @param secondsToWait,   the interval that you will wait before going in a timeout (default set tot DEFAULT_WAIT_INTERVAL).
	 *                         (this is the time that you will wait until the last run timestamp of the given sonar run is after the executionStart.
	 *                         It will only be taken into account when the executionStart argument is passed and ignored when it's {@code null}.
	 * @throws SonarQualityException will be thrown when the given project doesn't pass the linked quality gate.
	 */
	void validateQualityGate( SonarClient client,
	                          String projectKey,
	                          String qualityGateName,
	                          LocalDateTime executionStart,
	                          int secondsToWait ) throws SonarQualityException;

	/**
	 * Link the given the given quality gate (i.e. via the qualityGateName) to the given project (i.e. via the projectKey).
	 *
	 * @param client,          the SONAR configuration.
	 * @param projectKey,      the identifier of the project (e.g. groupId:ArtifactId:branchId).
	 * @param qualityGateName, the name of the quality gate to link
	 * @throws SonarQualityException will be thrown when something goes wrong while linking the quality gate to the project.
	 */
	void linkQualityGateToProject( SonarClient client, String projectKey, String qualityGateName ) throws SonarQualityException;

	/**
	 * Get the timestamp of the last sonar run.
	 *
	 * @param client,          the SONAR configuration.
	 * @param projectKey,      the identifier of the project (e.g. groupId:ArtifactId:branchId).
	 * @param qualityGateName, the name of the quality gate.
	 * @throws SonarQualityException will be thrown when something goes wrong while fetching the last run timestamp.
	 */
	LocalDateTime getLastRunTimeStamp( SonarClient client, String projectKey, String qualityGateName ) throws SonarQualityException;

	/**
	 * Compose the project key used by SONAR based on the project key and an optional branch name.
	 * If projectKey is blank, the projectKey will be composed as groupId:artifactId like put in the project configuration.
	 *
	 * @param projectKey, the sonar project.
	 * @param projectKey, the key of the project (i.e. groupId:artifactId).
	 * @param branchName, the branchName (optional)
	 * @return the project key known to SONAR
	 */
	String composeSonarProjectKey( MavenProject project, String projectKey, String branchName );
}
