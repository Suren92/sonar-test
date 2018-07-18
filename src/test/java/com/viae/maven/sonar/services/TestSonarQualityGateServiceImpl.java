/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.services;

import com.viae.maven.sonar.exceptions.SonarQualityException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.sonar.wsclient.SonarClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import static com.viae.maven.sonar.services.SonarQualityGateResponses.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Vandeperre Maarten on 01/05/2016.
 */
public class TestSonarQualityGateServiceImpl {
	public static final String QUALITY_GATE_QUERY_URL = "/api/resources/index?metrics=quality_gate_details&format=json&resource=%s";
	private static final String DUMMY_PROJECT_KEY = "DUMMY_PROJECT_KEY";
	private static final String DUMMY_BRANCH_NAME = "DUMMY_BRANCH_NAME";
	private final ArgumentCaptor<Map> MAP_CAPTOR = ArgumentCaptor.forClass( Map.class );

	private final SonarQualityGateService qualityGateService = spy( new SonarQualityGateServiceImpl( mock( Log.class ) ) );
	private final SonarClient client = mock( SonarClient.class );

	@Before
	public void setup() {
		reset( client );
	}

	@Test
	public void realLifeScenarioWithReturnAsList() throws Throwable {
		final String result = "";
	}

	@Test
	public void tooManyViolations() throws Throwable {
		doReturn( SonarQualityGateResponses.CRITICAL_VIOLATIONS_TOO_HIGH ).when( client ).get( String.format( QUALITY_GATE_QUERY_URL, DUMMY_PROJECT_KEY ) );
		try {
			qualityGateService.validateQualityGate( client, DUMMY_PROJECT_KEY, null );
			fail( "no error" );
		}
		catch ( final SonarQualityException e ) {
			System.out.print( e.getLocalizedMessage() );
			assertTrue( e.getLocalizedMessage().contains( "quality gate not met" ) );
			assertTrue( e.getLocalizedMessage().contains( "critical_violations" ) );
			assertTrue( e.getLocalizedMessage().contains( "\"op\":\"LT\"" ) );
			assertTrue( e.getLocalizedMessage().contains( "\"period\":3" ) );
			assertTrue( e.getLocalizedMessage().contains( "\"error\":\"1\"" ) );
		}
	}

	@Test
	public void noConditionsInServerResponseOnError() throws Throwable {
		doReturn( SonarQualityGateResponses.ERROR_WITHOUT_CONDITIONS ).when( client ).get( String.format( QUALITY_GATE_QUERY_URL, DUMMY_PROJECT_KEY ) );
		try {
			qualityGateService.validateQualityGate( client, DUMMY_PROJECT_KEY, null );
			fail( "no error" );
		}
		catch ( final SonarQualityException e ) {
			System.out.print( e.getLocalizedMessage() );
			assertTrue( e.getLocalizedMessage().contains( "quality gate not met" ) );
			assertTrue( e.getLocalizedMessage().toLowerCase().contains( "conditions".toLowerCase() ) );
		}
	}

	@Test
	public void noConditionsArrayInServerResponseOnError() throws Throwable {
		doReturn( SonarQualityGateResponses.ERROR_WITH_CONDITIONS_AS_NON_ARRAY ).when( client ).get(
				String.format( QUALITY_GATE_QUERY_URL, DUMMY_PROJECT_KEY ) );
		try {
			qualityGateService.validateQualityGate( client, DUMMY_PROJECT_KEY, null );
			fail( "no error" );
		}
		catch ( final SonarQualityException e ) {
			System.out.print( e.getLocalizedMessage() );
			assertTrue( e.getLocalizedMessage().contains( "quality gate not met" ) );
			assertTrue( e.getLocalizedMessage().toLowerCase().contains( "conditions".toLowerCase() ) );
		}
	}

	@Test
	public void invalidJson() throws Throwable {
		doReturn( "{tsetdit = invalid json}" ).when( client ).get( String.format( QUALITY_GATE_QUERY_URL, DUMMY_PROJECT_KEY ) );
		try {
			qualityGateService.validateQualityGate( client, DUMMY_PROJECT_KEY, null );
			fail( "no error" );
		}
		catch ( final SonarQualityException e ) {
			assertTrue( e.getLocalizedMessage().contains( "Unexpected character" ) );
		}
	}

	@Test
	public void composeProjectKey() throws Throwable {
		final MavenProject project = new MavenProject();
		project.setGroupId( "groupId" );
		project.setArtifactId( "artifactId" );
		assertThat( qualityGateService.composeSonarProjectKey( project, null, DUMMY_BRANCH_NAME ), equalTo( "groupId:artifactId:" + DUMMY_BRANCH_NAME ) );
		assertThat( qualityGateService.composeSonarProjectKey( project, null, null ), equalTo( "groupId:artifactId" ) );
		assertThat( qualityGateService.composeSonarProjectKey( project, null, "" ), equalTo( "groupId:artifactId" ) );
		assertThat( qualityGateService.composeSonarProjectKey( project, DUMMY_PROJECT_KEY, null ), equalTo( DUMMY_PROJECT_KEY ) );
		assertThat( qualityGateService.composeSonarProjectKey( project, DUMMY_PROJECT_KEY, DUMMY_BRANCH_NAME ),
		            equalTo( DUMMY_PROJECT_KEY + ":" + DUMMY_BRANCH_NAME ) );
	}

	@Test
	public void linkQualityGateToProjectWithNullSonarClient() throws Throwable {
		try {
			qualityGateService.linkQualityGateToProject( null, RandomStringUtils.randomAlphabetic( 5 ), RandomStringUtils.randomAlphabetic( 5 ) );
			fail( "no error" );
		}
		catch ( final NullPointerException e ) {
			assertTrue( e.getLocalizedMessage(), e.getLocalizedMessage().contains( "Sonar client" ) );
		}
	}

	@Test
	public void linkQualityGateToProjectWithNullProjectKey() throws Throwable {
		try {
			qualityGateService.linkQualityGateToProject( client, null, RandomStringUtils.randomAlphabetic( 5 ) );
			fail( "no error" );
		}
		catch ( final NullPointerException e ) {
			assertTrue( e.getLocalizedMessage(), e.getLocalizedMessage().contains( "project key" ) );
		}
	}

	@Test
	public void linkQualityGateToProjectWithEmptyProjectKey() throws Throwable {
		try {
			qualityGateService.linkQualityGateToProject( client, "", RandomStringUtils.randomAlphabetic( 5 ) );
			fail( "no error" );
		}
		catch ( final IllegalArgumentException e ) {
			assertTrue( e.getLocalizedMessage(), e.getLocalizedMessage().contains( "project key" ) );
		}
	}

	@Test
	public void linkQualityGateToProjectWithNullQualityGateName() throws Throwable {
		try {
			qualityGateService.linkQualityGateToProject( client, RandomStringUtils.randomAlphabetic( 5 ), null );
			fail( "no error" );
		}
		catch ( final NullPointerException e ) {
			assertTrue( e.getLocalizedMessage(), e.getLocalizedMessage().contains( "quality gate name" ) );
		}
	}

	@Test
	public void linkQualityGateToProjectWithEmptyQualityGateName() throws Throwable {
		try {
			qualityGateService.linkQualityGateToProject( client, RandomStringUtils.randomAlphabetic( 5 ), "" );
			fail( "no error" );
		}
		catch ( final IllegalArgumentException e ) {
			assertTrue( e.getLocalizedMessage(), e.getLocalizedMessage().contains( "quality gate name" ) );
		}
	}

	@Test
	public void linkQualityGateToProjectWithDetailsAsList() throws Throwable {
		final String qualityGateName = "qualityGateName";
		final String projectKey = "projectKey";

		doReturn( PROJECT_DETAIL_AS_LIST ).when( client ).get( "/api/resources?format=json&resource=projectKey" );
		doReturn( QUALITY_GATE_DETAIL_AS_LIST ).when( client ).get( "/api/qualitygates/show?name=qualityGateName" );

		qualityGateService.linkQualityGateToProject( client, projectKey, qualityGateName );

		verify( client, times( 1 ) ).get( "/api/resources?format=json&resource=projectKey" );
		verify( client, times( 1 ) ).get( "/api/qualitygates/show?name=qualityGateName" );
		verify( client, times( 1 ) ).post( eq( "/api/qualitygates/select" ), MAP_CAPTOR.capture() );

		final Map postedMap = MAP_CAPTOR.getValue();
		assertThat( postedMap.get( "gateId" ), equalTo( "2" ) );
		assertThat( postedMap.get( "projectId" ), equalTo( 22295 ) );
	}

	@Test
	public void linkQualityGateToProject() throws Throwable {
		final String qualityGateName = "qualityGateName";
		final String projectKey = "projectKey";

		doReturn( PROJECT_DETAIL ).when( client ).get( "/api/resources?format=json&resource=projectKey" );
		doReturn( QUALITY_GATE_DETAIL ).when( client ).get( "/api/qualitygates/show?name=qualityGateName" );

		qualityGateService.linkQualityGateToProject( client, projectKey, qualityGateName );

		verify( client, times( 1 ) ).get( "/api/resources?format=json&resource=projectKey" );
		verify( client, times( 1 ) ).get( "/api/qualitygates/show?name=qualityGateName" );
		verify( client, times( 1 ) ).post( eq( "/api/qualitygates/select" ), MAP_CAPTOR.capture() );

		final Map postedMap = MAP_CAPTOR.getValue();
		assertThat( postedMap.get( "gateId" ), equalTo( "2" ) );
		assertThat( postedMap.get( "projectId" ), equalTo( 22295 ) );
	}

	@Test
	public void waitForNewPublishingOfSonarResultsWithoutWaiting() throws Throwable {
		final String projectKey = DUMMY_PROJECT_KEY;
		final LocalDateTime executionStart = null;
		final int secondsToWait = -1;

		final LocalDateTime start = LocalDateTime.now();
		qualityGateService().waitForNewPublishingOfSonarResults( client, projectKey, null, executionStart, secondsToWait );
		final LocalDateTime end = LocalDateTime.now();
		final long duration = Duration.between( start, end ).getSeconds();

		assertTrue( String.valueOf( duration ), duration == 0 );
	}

	@Test
	public void waitForNewPublishingOfSonarResultsWithWaitingAndIntervalExpires() throws Throwable {
		final String projectKey = DUMMY_PROJECT_KEY;
		final LocalDateTime executionStart = LocalDateTime.now();
		final int secondsToWait = 2;

		doReturn( executionStart ).when( qualityGateService() ).getLastRunTimeStamp( client, projectKey, null );

		final LocalDateTime start = LocalDateTime.now();
		try {
			qualityGateService().waitForNewPublishingOfSonarResults( client, projectKey, null, executionStart, secondsToWait );
			fail( "no error" );
		}
		catch ( final SonarQualityException e ) {
			// 3 : duration is rounded
			assertEquals( "We waited for 3 seconds, but no update on last run (i.e. date field) occurred.", e.getLocalizedMessage() );
		}
		final LocalDateTime end = LocalDateTime.now();
		final long duration = Duration.between( start, end ).getSeconds();

		assertTrue( String.valueOf( duration ), duration == 3 );
	}

	@Test
	public void getLastRunTimeStampWithNullSonarClient() throws Throwable {
		try {
			qualityGateService.getLastRunTimeStamp( null, RandomStringUtils.randomAlphabetic( 5 ), null );
			fail( "no error" );
		}
		catch ( final NullPointerException e ) {
			assertTrue( e.getLocalizedMessage(), e.getLocalizedMessage().contains( "Sonar client" ) );
		}
	}

	@Test
	public void getLastRunTimeStampWithNullProjectKey() throws Throwable {
		try {
			qualityGateService.getLastRunTimeStamp( client, null, null );
			fail( "no error" );
		}
		catch ( final NullPointerException e ) {
			assertTrue( e.getLocalizedMessage(), e.getLocalizedMessage().contains( "project key" ) );
		}
	}

	@Test
	public void getLastRunTimeStampWithBlankResponse() throws Throwable {
		doReturn( null ).when( client ).get( "/api/resources?format=json&resource=DUMMY_PROJECT_KEY" );
		assertThat( qualityGateService.getLastRunTimeStamp( client, DUMMY_PROJECT_KEY, null ), equalTo( null ) );
		doReturn( "" ).when( client ).get( "/api/resources?format=json&resource=DUMMY_PROJECT_KEY" );
		assertThat( qualityGateService.getLastRunTimeStamp( client, DUMMY_PROJECT_KEY, null ), equalTo( null ) );
	}

	@Test
	public void getLastRunTimeStamp() throws Throwable {
		doReturn( QUALITY_GATE_REPONSE ).when( client ).get( "/api/resources?format=json&resource=DUMMY_PROJECT_KEY" );
		final LocalDateTime lastRunTimeStamp = qualityGateService.getLastRunTimeStamp( client, DUMMY_PROJECT_KEY, null );
		assertThat( lastRunTimeStamp, equalTo( LocalDateTime.of( 2016, 4, 29, 8, 9, 22, 0 ) ) );
	}

	@Test
	public void testResponseProjectDataParsingWithArrayResult() throws Throwable {
		String json = "" +
				"[" +
				"{" +
				"\"id\": \"3421\"," +
				"\"k\": \"com.viae-test:my-test:branch\"," +
				"\"nm\": \"com.viae-test:my-test\"," +
				"\"sc\": \"PRJ\"," +
				"\"qu\": \"TRK\"" +
				"}" +
				"]" +
				"";
		assertThat( qualityGateService().getProjectId( json ), equalTo( 3421 ) );
	}

	@Test
	public void testResponseProjectDataParsing() throws Throwable {
		String json = "" +
				"{" +
				"\"id\": \"3421\"," +
				"\"k\": \"com.viae-test:my-test:branch\"," +
				"\"nm\": \"com.viae-test:my-test\"," +
				"\"sc\": \"PRJ\"," +
				"\"qu\": \"TRK\"" +
				"}" +
				"";
		assertThat( qualityGateService().getProjectId( json ), equalTo( 3421 ) );
	}

	@Test
	public void testResponseProjectDataParsingWithException() throws Throwable {
		String json = "" +
				"{" +
				"\"idd\": \"3421\"," +
				"\"k\": \"com.viae-test:my-test:branch\"," +
				"\"nm\": \"com.viae-test:my-test\"," +
				"\"sc\": \"PRJ\"," +
				"\"qu\": \"TRK\"" +
				"}" +
				"";
		try {
			qualityGateService().getProjectId( json );
			fail( "no error" );
		}
		catch ( SonarQualityException e ) {
			assertThat( e.getLocalizedMessage(), e.getLocalizedMessage(), containsString( "idd" ) );
			assertThat( e.getCause(), instanceOf( NumberFormatException.class ) );
		}
	}

	private SonarQualityGateServiceImpl qualityGateService() {
		return (SonarQualityGateServiceImpl) qualityGateService;
	}
}