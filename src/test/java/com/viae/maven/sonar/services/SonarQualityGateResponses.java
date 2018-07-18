/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */
package com.viae.maven.sonar.services;

/**
 * Created by Vandeperre Maarten on 02/05/2016.
 */
public class SonarQualityGateResponses {
	public static final String OK = "{\"level\":\"OK\",\"conditions\":[]}";

	public static final String ERROR_WITHOUT_CONDITIONS = "[{\"msr\":{\"data\":" +
			"{\"level\":\"ERROR\"," +
			"}" +
			"}}]";

	public static final String ERROR_WITH_CONDITIONS_AS_NON_ARRAY = "[{\"msr\":{\"data\":" +
			"{\"level\":\"ERROR\"," +
			"\"conditions\":" +
			"{  " +
			"\"metric\":\"critical_violations\"," +
			"\"op\":\"LT\"," +
			"\"period\":3," +
			"\"warning\":\"1\"," +
			"\"error\":\"1\"," +
			"\"actual\":\"0.0\"," +
			"\"level\":\"ERROR\"" +
			"}" +
			"}" +
			"}}]";

	public static final String CRITICAL_VIOLATIONS_TOO_HIGH = "[{\"msr\":{\"data\":" +
			"{\"level\":\"ERROR\"," +
			"\"conditions\":[" +
			"{  " +
			"\"metric\":\"critical_violations\"," +
			"\"op\":\"LT\"," +
			"\"period\":3," +
			"\"warning\":\"1\"," +
			"\"error\":\"1\"," +
			"\"actual\":\"0.0\"," +
			"\"level\":\"ERROR\"" +
			"}" +
			"]" +
			"}" +
			"}}]";
	
	public static final String PROJECT_DETAIL = "{\"id\":22295,\"key\":\"test.package:sample-project:master\",\"name\":\"test.package:sample-project master\",\"scope\":\"PRJ\",\"qualifier\":\"TRK\",\"date\":\"2016-05-03T14:04:45+0200\",\"creationDate\":\"2016-05-02T16:32:23+0200\",\"lname\":\"test.package:sample-project master\",\"version\":\"0.0.1-SNAPSHOT\",\"branch\":\"master\",\"description\":\"\"}";

	public static final String PROJECT_DETAIL_AS_LIST = "[{\"id\":22295,\"key\":\"test.package:sample-project:master\",\"name\":\"test.package:sample-project master\",\"scope\":\"PRJ\",\"qualifier\":\"TRK\",\"date\":\"2016-05-03T14:04:45+0200\",\"creationDate\":\"2016-05-02T16:32:23+0200\",\"lname\":\"test.package:sample-project master\",\"version\":\"0.0.1-SNAPSHOT\",\"branch\":\"master\",\"description\":\"\"}]";

	public static final String QUALITY_GATE_DETAIL = "{\"id\":2,\"name\":\"SampleQualityGate\",\"conditions\":[{\"id\":9,\"metric\":\"new_coverage\",\"op\":\"LT\",\"warning\":\"\",\"error\":\"90\",\"period\":3}]}";

	public static final String QUALITY_GATE_DETAIL_AS_LIST = "[{\"id\":2,\"name\":\"SampleQualityGate\",\"conditions\":[{\"id\":9,\"metric\":\"new_coverage\",\"op\":\"LT\",\"warning\":\"\",\"error\":\"90\",\"period\":3}]}]";

	public static final String QUALITY_GATE_REPONSE = "" +
				                      "[" +
				                      "	{" +
				                      "		\"id\": 20035," +
				                      "		\"key\": \"be.realinvestor:investor-modules:feature/SONAR-TEST-2\"," +
				                      "		\"name\": \"investor-modules feature/SONAR-TEST-2\"," +
				                      "		\"scope\": \"PRJ\"," +
				                      "		\"qualifier\": \"TRK\"," +
				                      "		\"date\": \"2016-04-29T08:09:22+0200\"," +
				                      "		\"creationDate\": \"2016-04-29T08:09:22+0200\"," +
				                      "		\"lname\": \"investor-modules feature/SONAR-TEST-2\"," +
				                      "		\"version\": \"1.0-SNAPSHOT\"," +
				                      "		\"branch\": \"feature/SONAR-TEST-2\"," +
				                      "		\"description\": \"\"," +
				                      "		\"msr\": [" +
				                      "			{" +
				                      "				\"key\": \"quality_gate_details\"," +
				                      "				\"data\": {\"level\":\"OK\",\"conditions\":[]}" +
				                      "			}" +
				                      "		]" +
				                      "	}" +
				                      "]" +
				                      "";
}