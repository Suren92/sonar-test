package com.viae.maven.sonar.mojos;

import com.viae.maven.sonar.exceptions.SonarQualityException;
import com.viae.maven.sonar.services.SonarQualityGateService;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.sonar.wsclient.SonarClient;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

/**
 * Tests for {@link SonarMavenInitSonarExecutionStartMojo}
 * <p>
 * Created by Maarten on 23/05/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestSonarMavenInitSonarExecutionStartMojo {

	private static final String KEY = "sonar.execution.start";

	@Mock
	private MavenProject project;
	@Mock
	private SonarQualityGateService service;
	private Properties properties;

	private final SonarMavenInitSonarExecutionStartMojo mojo = new SonarMavenInitSonarExecutionStartMojo();

	@Before
	public void setupFreshFixture() throws Throwable {
		mojo.project = project;
		mojo.sonarServer = "sonarServer";
		mojo.sonarUser = "user";
		mojo.sonarPassword = "password";
		mojo.sonarKey = KEY;
		properties = new Properties();
		doReturn( properties ).when( project ).getProperties();
		final Field serviceField = SonarMavenInitSonarExecutionStartMojo.class.getDeclaredField( "qualityGateService" );
		serviceField.setAccessible( true );
		serviceField.set(mojo, service);
	}

	@Test
	public void setTimeStampWithoutLastRunTimestamp() throws Throwable {
		assertEquals( "guard assertion", null, project.getProperties().get( KEY ) );
		final LocalDateTime before = LocalDateTime.now().minusSeconds( 1 );
		doReturn( null ).when( service ).getLastRunTimeStamp( any( SonarClient.class), anyString(), anyString() );
		mojo.execute();
		final LocalDateTime after = LocalDateTime.now().plusSeconds( 1 );
		final String startTimeString = (String) project.getProperties().get( KEY );
		assertTrue( before + " <= " + startTimeString, before.isBefore( LocalDateTime.parse( startTimeString, DateTimeFormatter.ISO_DATE_TIME ) ) );
		assertTrue( after + " >= " + startTimeString, after.isAfter( LocalDateTime.parse( startTimeString, DateTimeFormatter.ISO_DATE_TIME ) ) );
	}

	@Test
	public void setTimeStampWithLastRunTimestamp() throws Throwable {
		assertEquals( "guard assertion", null, project.getProperties().get( KEY ) );
		final LocalDateTime first = LocalDateTime.MIN;
		doReturn( first ).when( service ).getLastRunTimeStamp( any( SonarClient.class), anyString(), anyString() );
		mojo.execute();
		final String startTimeString = (String) project.getProperties().get( KEY );
		assertEquals( DateTimeFormatter.ISO_DATE_TIME.format( first ), startTimeString );
	}

	@Test
	public void keepTimeStamp() throws Throwable {
		final LocalDateTime original = LocalDateTime.now();
		project.getProperties().setProperty( "sonar.execution.start", String.valueOf( original ) );
		assertEquals( "guard assertion", String.valueOf( original ), project.getProperties().get( KEY ) );
		mojo.execute();
		final String startTimeString = (String) project.getProperties().get( KEY );
		assertEquals( DateTimeFormatter.ISO_DATE_TIME.format( original ), startTimeString );
	}

	@Test
	public void setTimeStampWithException() throws Throwable {
		doThrow( new SonarQualityException( "error" ) ).when( service ).getLastRunTimeStamp( any( SonarClient.class ), anyString(), anyString() );
		try {
			mojo.execute();
			fail( "no error" );
		}
		catch ( final MojoFailureException e ) {
			assertTrue( e.getLocalizedMessage(), e.getLocalizedMessage().contains( "error" ) );
		}
	}
}
