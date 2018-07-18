/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.mojos;

import com.viae.maven.sonar.GlobalSettings;
import com.viae.maven.sonar.exceptions.GitException;
import com.viae.maven.sonar.services.GitService;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Created by Vandeperre Maarten on 08/05/2016.
 */
public class TestSonarMavenGitBranchMojo {

	private SonarMavenSetGitBranchMojo mojo;
	private final MavenProject project = mock( MavenProject.class );
	private final GitService gitService = mock( GitService.class );
	private Properties properties;

	@Before
	public void setupFreshFixture() {
		mojo = new SonarMavenSetGitBranchMojo();
		reset( project );
		mojo.project = project;
		properties = new Properties();
		doReturn( properties ).when( project ).getProperties();
	}

	@Test
	public void happyPath() throws Throwable {
		mojo.execute();
		assertThat( properties.getProperty( GlobalSettings.SONAR_BRANCH_PROPERTY_NAME ), equalTo( GlobalSettings.BRANCH_NAME ) );
	}

	@Test
	public void happyPathWithSpecialCharacter() throws Throwable {
		final GitService gitService = mock( GitService.class );
		doReturn( "feature/test-test" ).when( gitService ).getBranchName( any( Runtime.class ) );

		Field f = mojo.getClass().getDeclaredField( "gitService" );
		f.setAccessible( true );
		f.set( mojo, gitService );

		mojo.execute();
		assertThat( properties.getProperty( GlobalSettings.SONAR_BRANCH_PROPERTY_NAME ), equalTo( "feature-test-test" ) );
	}

	@Test
	public void doNotOverrideSonarBranchProperty() throws Throwable {
		properties.setProperty( GlobalSettings.SONAR_BRANCH_PROPERTY_NAME, "test-property" );
		mojo.execute();
		assertThat( properties.getProperty( GlobalSettings.SONAR_BRANCH_PROPERTY_NAME ), equalTo( "test-property" ) );
	}

	@Test
	public void doNotOverrideSonarBranchPropertyWithSpecialCharacter() throws Throwable {
		properties.setProperty( GlobalSettings.SONAR_BRANCH_PROPERTY_NAME, "feature/test-property" );
		mojo.execute();
		assertThat( properties.getProperty( GlobalSettings.SONAR_BRANCH_PROPERTY_NAME ), equalTo( "feature-test-property" ) );
	}

	@Test
	public void gitExceptionHandling() throws Throwable {
		final Field field = mojo.getClass().getDeclaredField( "gitService" );
		field.setAccessible( true );
		field.set( mojo, gitService );
		doThrow( new GitException( new Exception( "sample-exception" ) ) ).when( this.gitService ).getBranchName( any( Runtime.class ) );
		try {
			mojo.execute();
			fail( "no error" );
		}
		catch ( final MojoFailureException e ) {
			assertThat( e.getLocalizedMessage(), containsString( "Something went wrong while executing GIT command" ) );
			assertThat( e.getCause().getCause().getLocalizedMessage(), containsString( "sample-exception" ) );
		}
	}
}
