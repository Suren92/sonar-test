/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.mojos;

import com.viae.maven.sonar.config.SonarStrings;
import com.viae.maven.sonar.services.GitService;
import com.viae.maven.sonar.services.GitServiceImpl;
import com.viae.maven.sonar.utils.SpecialCharacterUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

/**
 * Mojo to set the sonar.branch property to the git branch name (if the property is not defined).
 * <p>
 * Created by Vandeperre Maarten on 29/04/2016.
 */
@Mojo(name = SonarStrings.MOJO_NAME_SET_GIT_BRANCH, aggregator = true)
public class SonarMavenSetGitBranchMojo extends AbstractMojo {

	private final GitService gitService = new GitServiceImpl( getLog() );
	@Component
	protected MavenProject project;

	/**
	 * Set the sonar.branch property to the git branch name (if the property is not defined).
	 *
	 * @throws MojoExecutionException will be thrown when something goes wrong while retrieving the git branch name.
	 * @throws MojoFailureException   will not be thrown
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		String resultingBranchName;
		getLog().info( String.format( "%s start execution of '%s'", SonarStrings.LOG_PREFIX, SonarStrings.MOJO_NAME_SET_GIT_BRANCH ) );
		try {
			final String existingBranchValue = project.getProperties().getProperty( SonarStrings.BRANCH );
			getLog().info( String.format( "%s existing %s: '%s'", SonarStrings.LOG_PREFIX, SonarStrings.BRANCH, existingBranchValue ) );
			if ( StringUtils.isBlank( existingBranchValue ) ) {
				final String sonarBranchName = gitService.getBranchName( Runtime.getRuntime() );
				getLog().info( String.format( "%s set property '%s' to '%s'", SonarStrings.LOG_PREFIX, SonarStrings.BRANCH, sonarBranchName ) );
				resultingBranchName = sonarBranchName;
			}
			else {
				resultingBranchName = existingBranchValue;
			}
			project.getProperties().setProperty( SonarStrings.BRANCH, SpecialCharacterUtil.makeStringFreeOfSpecialCharacters( resultingBranchName ) );
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
