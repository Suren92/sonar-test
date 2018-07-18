# sonar-maven-plugin
This plugin is created to validate sonar quality gates and set sonar configuration through maven.

# Table of Contents
1. [Components overview](https://github.com/VandeperreMaarten/sonar-maven-plugin#components-overview)
2. [set-git-branch](https://github.com/VandeperreMaarten/sonar-maven-plugin#set-git-branch)
3. [set-sonar-execution-start](https://github.com/VandeperreMaarten/sonar-maven-plugin#set-sonar-execution-start)
4. [link-project-to-qualitygate](https://github.com/VandeperreMaarten/sonar-maven-plugin#link-project-to-qualitygate)
5. [validate-qualitygate](https://github.com/VandeperreMaarten/sonar-maven-plugin#validate-qualitygate)
6. [Maven example](https://github.com/VandeperreMaarten/sonar-maven-plugin#maven-example)

## **Components overview**

* **set-git-branch**

  * Sets the sonar.branch property to the current git branch (when it's not yet set).
  When set already, the property will not be overridden.*
* **set-sonar-execution-start**

  * Looks at the project in sonar and stores the last run timestamp in the property 'sonar.execution.start' (as an ISO formatted string).
  When the timestamp is not found, the current time will be stored in the property.
  When set already, the property will not be overridden.*
* **link-project-to-qualitygate**

  * Links the project to the quality gate defined in the property 'sonar.qualitygate'*
* **validate-qualitygate**

  * Checks if the project did pass the quality gate (i.e. property 'sonar.qualitygate') after last sonar run.
  If the quality gate is not met, the maven build will break.*

## set-git-branch
* Sets the sonar.branch property to the current git branch (when it's not yet set).
When set already, the property will not be overridden. When the branchname (set by the user / GIT branch name) contains speciale characters (e.g. '/') then those will be replaced by '-'. This is because sonar can't threat that character in its key name.*

##### Required properties
none

##### Example usage
mvn com.viae-it.maven:sonar-maven-plugin:set-git-branch

## set-sonar-execution-start
* This will call sonar via the API, look for the last run timestamp and store it in the 'sonar.execution.start' property.
When there was no last run configuration found, the timestamp will be set to now.
When the 'sonar.execution.start' property is set, the [validate-qualitygate](https://github.com/VandeperreMaarten/sonar-maven-plugin#validate-qualitygate) will use it to verify that the new run has ended.

##### Required properties
1. **sonar.host.url** : the root url of the sonar server.
2. **sonar.login** : the user to login with (!! make sure this user has sufficient rights).
3. **sonar.password** : the password linked to that user.

##### Optional properties
1. **sonar.branch** : the sonar branch for which you want to have the last run timestamp.
2. **sonar.projectKey** : most of the time this is ${project.groupId}:${project.artifactId}. If not set, this property will be set to "${project.groupId}:${project.artifactId}"

##### Example usage
mvn com.viae-it.maven:sonar-maven-plugin:com.viae-it.maven:sonar-maven-plugin:set-sonar-execution-start

## link-project-to-qualitygate
* This will take the quality gate from the 'sonar.qualitygate' property and will link the corresponding project to it
(i.e. the project for which this configuration is set up).*

**Important note**: most of the time you have to make sure that is user is allowed to change the quality gate configurations.
*This can be configured in sonar (logged in as admin) and go to 'Settings' > 'Security' > 'Global Permissions' and make sure that the configured user has
sufficient rights on 'Administer Quality Profiles and Gates' permission.*

##### Required properties
1. **sonar.host.url** : the root url of the sonar server.
2. **sonar.login** : the user to login with (!! make sure this user has sufficient rights).
3. **sonar.password** : the password linked to that user.
4. **sonar.qualitygate** : the name of the quality gate you want to link.

##### Optional properties
1. **sonar.branch** : the sonar branch for which you want to have the last run timestamp.
2. **sonar.projectKey** : most of the time this is ${project.groupId}:${project.artifactId}. If not set, this property will be set to "${project.groupId}:${project.artifactId}"

##### Example usage
mvn com.viae-it.maven:sonar-maven-plugin:link-project-to-qualitygate

## validate-qualitygate
* Checks if the project did pass the quality gate (i.e. property 'sonar.qualitygate') after last sonar run.
If the quality gate is not met, the maven build will break and will show the condition states (also from the ones that were passed).
If the 'sonar.execution.start' property is set, the validation run will wait until the timestamp of the last run is after this value
(can be set via [set-sonar-execution-start](https://github.com/VandeperreMaarten/sonar-maven-plugin#set-sonar-execution-start)). If nothing changed for 5 minutes, an exception will be thrown.

##### Required properties
1. **sonar.host.url** : the root url of the sonar server.
2. **sonar.login** : the user to login with (!! make sure this user has sufficient rights).
3. **sonar.password** : the password linked to that user.
4. **sonar.qualitygate** : the name of the quality gate you want to validate.

##### Optional properties
1. **sonar.branch** : the sonar branch for which you want to have the last run timestamp.
2. **sonar.sonarExecutionStart** : the name of the quality gate you want to link.
3. **sonar.projectKey** : most of the time this is ${project.groupId}:${project.artifactId}. If not set, this property will be set to "${project.groupId}:${project.artifactId}"

##### Example usage
mvn com.viae-it.maven:sonar-maven-plugin:validate-qualitygate

## Maven example
*This is an example in how to configure all the possible goals and how to call is.
All the output is prefixed by 'VIAE log', so you can filter on this to see what's going on.*

##### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId></groupId>
	<artifactId></artifactId>
	<version></version>
	<packaging>pom</packaging>

	<name></name>

	<properties>
		....

		<jacoco-maven-plugin.version>0.7.6.201602180812</jacoco-maven-plugin.version>
		<sonar.host.url>http://localhos:9134/</sonar.host.url>
		<sonar.login>...</sonar.login>
		<sonar.password>...</sonar.password>
		<sonar.skippedModules/>
		<sonar.exclusions/>
		<sonar.coverage.exclusions>**/*Properties.java</sonar.coverage.exclusions>
		<sonar.java.coveragePlugin>jacoco</sonar.java.coveragePlugin>
		<sonar.jacoco.reportPath>${project.basedir}/../target/jacoco.exec</sonar.jacoco.reportPath>
		<sonar.language>java</sonar.language>
		<sonar.jdbc.url>.....</sonar.jdbc.url>
		<sonar.jdbc.driverClassName>....</sonar.jdbc.driverClassName>
		<sonar.jdbc.username>....</sonar.jdbc.username>
		<sonar.jdbc.password>....</sonar.jdbc.password>
		<sonar.qualitygate>....</sonar.qualitygate>
	</properties>

	....

	<build>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<version>3.1</version>
				<configuration>
					<source>1.8</source>
					<target>1.8</target>
				</configuration>
			</plugin>
			<plugin>
				<groupId>org.codehaus.mojo</groupId>
				<artifactId>sonar-maven-plugin</artifactId>
				<version>2.7.1</version>
			</plugin>
			<plugin>
				<groupId>com.viae-it.maven</groupId>
				<artifactId>sonar-maven-plugin</artifactId>
				<version>LATEST</version>
			</plugin>
			<plugin>
				<groupId>org.jacoco</groupId>
				<artifactId>jacoco-maven-plugin</artifactId>
				<version>${jacoco-maven-plugin.version}</version>
				<configuration>
					<destFile>${sonar.jacoco.reportPath}</destFile>
					<append>true</append>
				</configuration>
				<executions>
					<execution>
						<id>agent</id>
						<goals>
							<goal>prepare-agent</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-surefire-plugin</artifactId>
				<version>2.18.1</version>
				<configuration>
					<includes>
						<include>**/*Test.java</include>
					</includes>
					<failIfNoTests>false</failIfNoTests>
				</configuration>
			</plugin>
		</plugins>
	</build>


</project>

```

##### How to execute

```{r, engine='bash', count_lines}
mvn
com.viae-it.maven:sonar-maven-plugin:set-git-branch
com.viae-it.maven:sonar-maven-plugin:set-sonar-execution-start
com.viae-it.maven:sonar-maven-plugin:link-project-to-qualitygate
sonar:sonar
com.viae-it.maven:sonar-maven-plugin:validate-qualitygate
| grep "VIAE log\|ERROR"
```
