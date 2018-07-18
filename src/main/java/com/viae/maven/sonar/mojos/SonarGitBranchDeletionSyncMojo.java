package com.viae.maven.sonar.mojos;

import com.viae.maven.sonar.config.SonarStrings;
import com.viae.maven.sonar.exceptions.SonarQualityException;
import com.viae.maven.sonar.utils.JsonUtil;
import com.viae.maven.sonar.utils.SpecialCharacterUtil;
import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.sonar.wsclient.SonarClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MOJO to check whether a given git branch still exists: if not, the corresponding SONAR project will be deleted.
 * <p>
 * TODO: clean up code + tests
 *
 * @author by Maarten on 25/09/2016.
 */
@Mojo(name = SonarStrings.MOJO_NAME_SYNC_GIT_REPO, aggregator = true)
public class SonarGitBranchDeletionSyncMojo extends AbstractMojo {
	public static final String DELETE_PROJECT_URL = "/api/projects/";
	@Parameter(property = SonarStrings.SERVER, required = true)
	protected String sonarServer;
	@Parameter(property = SonarStrings.LOGIN, required = true)
	protected String sonarUser;
	@Parameter(property = SonarStrings.PASSWORD, required = true)
	protected String sonarPassword;
	@Parameter(property = SonarStrings.REPO_LOGIN, required = true)
	protected String repoUser;
	@Parameter(property = SonarStrings.REPO_PASSWORD, required = true)
	protected String repoPassword;
	@Parameter(property = SonarStrings.REPO_CONFIGURATION, required = true)
	protected Map<String, String> repoConfigurations;

	/**
	 * Validate a project against a given quality gate.
	 *
	 * @throws MojoExecutionException will not be thrown.
	 * @throws MojoFailureException   whll be thrown when the quality gate is not met by the given project.
	 */
	@Override

	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			repoConfigurations.forEach( this::deleteNotExistingBranches );
		}
		catch ( Exception e ) {
			getLog().error( String.format( "%s %s", SonarStrings.LOG_PREFIX, e.getLocalizedMessage() ) );
			throw new RuntimeException( String.format( "%s %s\ncause:\n%s",
			                                           SonarStrings.LOG_PREFIX,
			                                           e.getLocalizedMessage(),
			                                           ExceptionUtils.getStackTrace( e ) )
					, e );
		}
	}

	private void deleteNotExistingBranches( final String projectName, final String repoRootUrl ) {
		try {
			final SonarClient client = SonarClient.builder()
			                                      .url( sonarServer )
			                                      .login( sonarUser )
			                                      .password( sonarPassword )
			                                      .build();
			final Set<String> existingRepoBranches = getExistingBranches( repoRootUrl );
			final Map<String, BranchMetaData> sonarBranches = getSonarBranches( projectName, client );
			List<String> toDelete =
					sonarBranches.keySet().stream().filter( sonarBranchName -> !existingRepoBranches.contains( sonarBranchName ) ).collect( Collectors.toList() );
			getLog().info( String.format( "branches to delete for: %s:\n%s", projectName, toDelete ) );
			for ( String branchName : toDelete ) {
				String id = sonarBranches.get( branchName ).getId();
				String serverOutput = doDelete( String.format( "%s%s%s", "http://sonar.projects.foreach.be", DELETE_PROJECT_URL, id ), "admin", "tmp1022!" );
				getLog().info( "Got server output: \n" + serverOutput );
			}
		}
		catch ( final SonarQualityException e ) {
			throw new RuntimeException( e );
		}
	}

	private Map<String, BranchMetaData> getSonarBranches( final String projectName, final SonarClient client ) throws SonarQualityException {
		final Map<String, BranchMetaData> sonarBranches = new ConcurrentHashMap();
		String sonarResult = client.get( "/api/projects?format=json" );
		JSONArray sonarResultArray = JsonUtil.parseArray( sonarResult );
		sonarResultArray.stream().forEach( project -> {
			String id = ( (JSONObject) project ).get( "id" ).toString();
			String projectKey = ( (JSONObject) project ).get( "k" ).toString();
			if ( projectKey.startsWith( projectName ) ) {
				String branchName = Optional.ofNullable( projectKey ).orElse( "" ).replace( String.format( "%s:", projectName ), "" );
				sonarBranches.put( branchName, new BranchMetaData( id, projectKey ) );
			}
		} );
		return sonarBranches;
	}

	private Set<String> getExistingBranches( final String repoRootUrl ) throws SonarQualityException {
		final Set<String> existingBranches = new HashSet<>();
		String json = doGet( repoRootUrl, repoUser, repoPassword );
		JSONObject jsonObject = JsonUtil.parse( json );
		JSONArray jsonArray = (JSONArray) jsonObject.get( "values" );
		jsonArray.forEach(
				branch -> {
					String id = ( (JSONObject) branch ).get( "id" ).toString();
					String displayId = ( (JSONObject) branch ).get( "displayId" ).toString();
					existingBranches.add( displayId );
					String escapedValue = SpecialCharacterUtil.makeStringFreeOfSpecialCharacters( displayId );
					existingBranches.add( escapedValue );
				} );
		return existingBranches;
	}

	private String doGet( final String path, final String username, final String password ) throws SonarQualityException {
		getLog().info( String.format( "try to get data for: %s", path ) );
		try {
			StringJoiner joiner = new StringJoiner( "" );

			URL url = new URL( path );
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod( "GET" );
			conn.setRequestProperty( "Accept", "application/json" );
			String userpass = String.format( "%s:%s", username, password );
			String basicAuth = "Basic " + new String( new Base64().encode( userpass.getBytes() ) );
			conn.setRequestProperty( "Authorization", basicAuth );

			if ( conn.getResponseCode() != 200 ) {
				throw new RuntimeException( "Failed : HTTP error code : " + conn.getResponseCode() );
			}

			BufferedReader br = new BufferedReader( new InputStreamReader( ( conn.getInputStream() ) ) );

			String output;
			while ( ( output = br.readLine() ) != null ) {
				joiner.add( output );
			}

			conn.disconnect();
			return joiner.toString();
		}
		catch ( Exception e ) {
			throw new SonarQualityException( "failed to load repo data", e );
		}
	}

	private String doDelete( final String path, final String username, final String password ) throws SonarQualityException {
		try {
			URL url = new URL( path );
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput( true );
			conn.setRequestMethod( "DELETE" );
			conn.setRequestProperty( "Content-Type", "application/json" );
			String userpass = String.format( "%s:%s", username, password );
			String basicAuth = "Basic " + new String( new Base64().encode( userpass.getBytes() ) );
			conn.setRequestProperty( "Authorization", basicAuth );

			OutputStream os = conn.getOutputStream();
			os.flush();

			int responseCode = conn.getResponseCode();
			getLog().info( "got response code for delete: " + responseCode );
			if ( responseCode != HttpURLConnection.HTTP_OK ) {
				BufferedReader br = new BufferedReader( new InputStreamReader( ( conn.getErrorStream() ) ) );

				String output;
				StringJoiner serverOutput = new StringJoiner( "\n" );
				while ( ( output = br.readLine() ) != null ) {
					serverOutput.add( output );
				}
				throw new RuntimeException( "Failed : HTTP error code : " + conn.getResponseCode() + " ;\n" + serverOutput.toString() );
			}

			BufferedReader br = new BufferedReader( new InputStreamReader( ( conn.getInputStream() ) ) );

			String output;
			StringJoiner serverOutput = new StringJoiner( "\n" );
			while ( ( output = br.readLine() ) != null ) {
				serverOutput.add( output );
			}

			conn.disconnect();

			return serverOutput.toString();
		}
		catch ( Exception e ) {
			throw new SonarQualityException( "failed to delete project", e );
		}
	}

	private class BranchMetaData {
		private final String id;
		private final String name;

		public BranchMetaData( final String id, final String name ) {
			this.id = id;
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}
	}
}
