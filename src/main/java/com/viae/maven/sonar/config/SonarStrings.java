package com.viae.maven.sonar.config;

/**
 * Class to store and reuse SONAR property names
 * <p>
 * Created by Maarten on 28/05/2016.
 */
public class SonarStrings {

	public static final String EXECUTION_START = "sonar.execution.start";
	public static final String SERVER = "sonar.host.url";
	public static final String PROJECT_KEY = "sonar.projectKey"; // TODO compose out of maven group and artifact id
	public static final String BRANCH = "sonar.branch";
	public static final String LOGIN = "sonar.login";
	public static final String PASSWORD = "sonar.password";
	public static final String REPO_LOGIN = "repo.login";
	public static final String REPO_PASSWORD = "repo.password";
	public static final String QUALITY_GATE = "sonar.qualitygate";
	public static final String REPO_CONFIGURATION = "sonar.repo-configuration";
	public static final String LOG_PREFIX = "VIAE log:";
	public static final String MOJO_NAME_SET_GIT_BRANCH = "set-git-branch";
	public static final String MOJO_NAME_SYNC_GIT_REPO = "sync-git-branches";
	public static final String MOJO_NAME_SET_EXECUTION_START = "set-sonar-execution-start";
	public static final String MOJO_NAME_LINK_QUALITY_GATE = "link-project-to-qualitygate";
	public static final String MOJO_NAME_VALIDATE_QUALITY_GATE = "validate-qualitygate";
	private SonarStrings() {
	}
}
