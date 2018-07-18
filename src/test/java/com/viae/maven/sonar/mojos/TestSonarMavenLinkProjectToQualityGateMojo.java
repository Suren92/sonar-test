package com.viae.maven.sonar.mojos;

import com.viae.maven.sonar.exceptions.SonarQualityException;
import com.viae.maven.sonar.services.SonarQualityGateService;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.sonar.wsclient.SonarClient;

import java.lang.reflect.Field;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by Maarten on 13/05/2016.
 */
public class TestSonarMavenLinkProjectToQualityGateMojo {

	private final SonarMavenLinkProjectToQualityGateMojo mojo = new SonarMavenLinkProjectToQualityGateMojo();

	private final SonarQualityGateService service = mock( SonarQualityGateService.class );

	private final ArgumentCaptor<String> sonarKeyCaptor = ArgumentCaptor.forClass( String.class );
	private final ArgumentCaptor<String> qualityGateCaptor = ArgumentCaptor.forClass( String.class );

	@Before
	public void setupFreshFixture() {
		reset( service );
	}

	@Test
	public void link() throws Throwable {
		mojo.sonarServer = "sonarServer";
		mojo.sonarUser = "sonarUser";
		mojo.sonarKey = "sonarKey";
		mojo.sonarPassword = "sonarPassword";
		mojo.qualityGateName = "qualityGateName";

		final Field field = mojo.getClass().getDeclaredField( "qualityGateService" );
		field.setAccessible( true );
		field.set( mojo, service );

		doNothing().when( service ).linkQualityGateToProject( any( SonarClient.class ), anyString(), anyString() );
		doReturn( mojo.sonarKey ).when( service ).composeSonarProjectKey( null, mojo.sonarKey, null );
		mojo.execute();
		verify( service ).linkQualityGateToProject( any( SonarClient.class ), sonarKeyCaptor.capture(), qualityGateCaptor.capture() );
		assertThat( sonarKeyCaptor.getValue(), equalTo( "sonarKey" ) );
		assertThat( qualityGateCaptor.getValue(), equalTo( "qualityGateName" ) );
	}

	@Test
	public void errorOnLink() throws Throwable {
		mojo.sonarServer = "sonarServer";
		mojo.sonarUser = "sonarUser";
		mojo.sonarKey = "sonarKey";
		mojo.sonarPassword = "sonarPassword";
		mojo.qualityGateName = "qualityGateName";

		final Field field = mojo.getClass().getDeclaredField( "qualityGateService" );
		field.setAccessible( true );
		field.set( mojo, service );

		doThrow( new SonarQualityException( "", new Exception( "sample-exception" ) ) ).when( service ).linkQualityGateToProject( any( SonarClient.class ),
		                                                                                                                      anyString(), anyString() );
		try {
			mojo.execute();
			fail( "no error" );
		}
		catch ( final MojoFailureException e ) {
			assertThat( e.getLocalizedMessage(), containsString( "sample-exception" ) );
			assertThat( e.getCause().getCause().getLocalizedMessage(), containsString( "sample-exception" ) );
		}
	}
}
