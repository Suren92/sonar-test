package com.viae.maven.sonar.mojos;

import com.viae.maven.sonar.exceptions.SonarQualityException;
import com.viae.maven.sonar.services.SonarQualityGateService;
import com.viae.maven.sonar.services.SonarQualityGateServiceImpl;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.sonar.wsclient.SonarClient;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Created by Maarten on 13/05/2016.
 */
public class TestSonarMavenBuildBreakerMojo {

	private final SonarMavenBuildBreakerMojo mojo = new SonarMavenBuildBreakerMojo();

	private final SonarQualityGateService service = spy( new SonarQualityGateServiceImpl( mock( Log.class ) ) );
	private final ArgumentCaptor<String> projectKeyCaptor = ArgumentCaptor.forClass( String.class );
	private final MavenProject project = new MavenProject();

	@Before
	public void setupFreshFixture() {
		reset( service );
		project.setGroupId( "groupId" );
		project.setArtifactId( "artifactId" );
	}

	@Test
	public void breakBuild() throws Throwable {

		mojo.sonarServer = "sonarServer";
		mojo.sonarUser = "sonarUser";
		mojo.sonarKey = "sonarKey";
		mojo.sonarPassword = "sonarPassword";
		mojo.branchName = "branchName";
		mojo.project = project;

		final Field field = mojo.getClass().getDeclaredField( "qualityGateService" );
		field.setAccessible( true );
		field.set( mojo, service );

		doThrow( new SonarQualityException( "quality gate not met" ) ).when( service ).validateQualityGate( org.mockito.Mockito.any( SonarClient.class ),
		                                                                                                    projectKeyCaptor.capture(),
		                                                                                                    eq( null ) );
		try {
			mojo.execute();
			fail( "no error" );
		}
		catch ( final MojoFailureException e ) {
			assertThat( e.getLocalizedMessage(), containsString( "quality gate not met" ) );
		}
		assertThat( projectKeyCaptor.getValue(), equalTo( "sonarKey:branchName" ) );
	}

	@Test
	public void doNotbreakBuild() throws Throwable {
		mojo.sonarServer = "sonarServer";
		mojo.sonarUser = "sonarUser";
		mojo.sonarKey = "sonarKey";
		mojo.sonarPassword = "sonarPassword";
		mojo.branchName = "branchName";
		mojo.project = project;

		final Field field = mojo.getClass().getDeclaredField( "qualityGateService" );
		field.setAccessible( true );
		field.set( mojo, service );

		doNothing().when( service ).validateQualityGate( any( SonarClient.class ), projectKeyCaptor.capture(), eq( null ) );
		mojo.execute();

		verify( service ).validateQualityGate( any( SonarClient.class ), anyString(), anyString() );
	}

	@Test
	public void doNotbreakBuildForWaitingTime() throws Throwable {
		final LocalDateTime now = LocalDateTime.now();

		mojo.sonarServer = "http://sonarServer";
		mojo.sonarUser = "sonarUser";
		mojo.sonarKey = "sonarKey";
		mojo.sonarPassword = "sonarPassword";
		mojo.branchName = "branchName";
		mojo.sonarExecutionStart = DateTimeFormatter.ISO_DATE_TIME.format( now );
		mojo.project = project;

		final Field field = mojo.getClass().getDeclaredField( "qualityGateService" );
		field.setAccessible( true );
		field.set( mojo, service );

		doReturn( now )
				.doReturn( now )
				.doReturn( now )
				.doReturn( now.plusHours( 1 ) )
				.when( service ).getLastRunTimeStamp( any( SonarClient.class ), anyString(), anyString() );

		doNothing().when( (SonarQualityGateServiceImpl) service ).handleQualityGateState( any( SonarClient.class ), projectKeyCaptor.capture() );
		doReturn(true).when( (SonarQualityGateServiceImpl) service ).qualityGateDetailsExists( any( SonarClient.class ), projectKeyCaptor.capture() );
		mojo.execute();

		verify( service, times( 1 ) ).validateQualityGate( any( SonarClient.class ), anyString(), anyString(), any( LocalDateTime.class ), anyInt() );
		verify( service, times( 4 ) ).getLastRunTimeStamp( any( SonarClient.class ), anyString(), anyString() );
	}

}
