/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.utils;

import com.viae.maven.sonar.exceptions.SonarQualityException;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Vandeperre Maarten on 03/05/2016.
 */
public class TestJsonUtil {
    private static final String INVALID_JSON = "{test = not a valid json";
    private static final String MEANINGLESS_JSON = "{\"name\" : \"Vandeperre\", \"lastName\" : \"Maarten\", \"company\" : \"VIAE\"}";

    @Test
    public void getIdOnMainLevel() throws Throwable {
        final String happyPath = "{\"id\":22295,\"key\":\"group.id:project.id:master\",\"name\":\"group.id:project.id master\",\"scope\":\"PRJ\",\"qualifier\":\"TRK\",\"date\":\"2016-05-03T14:04:45+0200\",\"creationDate\":\"2016-05-02T16:32:23+0200\",\"lname\":\"group.id:project.id master\",\"version\":\"0.0.1-SNAPSHOT\",\"branch\":\"master\",\"description\":\"\"}";
        assertThat(JsonUtil.getIdOnMainLevel(happyPath), equalTo("22295"));
        assertThat(JsonUtil.getIdOnMainLevel(null), nullValue());
        assertThat(JsonUtil.getIdOnMainLevel(MEANINGLESS_JSON), nullValue());
        try {
            JsonUtil.getIdOnMainLevel(INVALID_JSON);
            fail("no error");
        } catch (SonarQualityException e) {
            assertThat(e.getLocalizedMessage(), e.getLocalizedMessage().contains("Unexpected character"), is(true));
        }
    }

    @Test
    public void getIdOnMainLevelForJsonArray() throws Throwable {
        final String happyPath = "[{\"id\":22295,\"key\":\"group.id:project.id:master\",\"name\":\"group.id:project.id master\",\"scope\":\"PRJ\",\"qualifier\":\"TRK\",\"date\":\"2016-05-03T14:04:45+0200\",\"creationDate\":\"2016-05-02T16:32:23+0200\",\"lname\":\"group.id:project.id master\",\"version\":\"0.0.1-SNAPSHOT\",\"branch\":\"master\",\"description\":\"\"}]";
        assertThat(JsonUtil.getIdOnMainLevel(happyPath), equalTo("22295"));
        assertThat(JsonUtil.getIdOnMainLevel(null), nullValue());
        assertThat(JsonUtil.getIdOnMainLevel(MEANINGLESS_JSON), nullValue());
        try {
            JsonUtil.getIdOnMainLevel(INVALID_JSON);
            fail("no error");
        } catch (SonarQualityException e) {
            assertThat(e.getLocalizedMessage(), e.getLocalizedMessage().contains("Unexpected character"), is(true));
        }
    }

    @Test
    public void getOnMainLevel() throws Throwable {
        final String happyPath = "{\"id\":22295,\"key\":\"group.id:project.id:master\",\"name\":\"group.id:project.id master\",\"scope\":\"PRJ\",\"qualifier\":\"TRK\",\"date\":\"2016-05-03T14:04:45+0200\",\"creationDate\":\"2016-05-02T16:32:23+0200\",\"lname\":\"group.id:project.id master\",\"version\":\"0.0.1-SNAPSHOT\",\"branch\":\"master\",\"description\":\"\"}";
        assertThat(JsonUtil.getOnMainLevel(happyPath, "id"), equalTo("22295"));
        assertThat(JsonUtil.getOnMainLevel(happyPath, "scope"), equalTo("PRJ"));
        assertThat(JsonUtil.getOnMainLevel(happyPath, "key"), equalTo("group.id:project.id:master"));
        assertThat(JsonUtil.getOnMainLevel(null, "id"), nullValue());
        assertThat(JsonUtil.getOnMainLevel(MEANINGLESS_JSON, "id"), nullValue());
        try {
            JsonUtil.getOnMainLevel(INVALID_JSON, "id");
            fail("no error");
        } catch (SonarQualityException e) {
            assertThat(e.getLocalizedMessage(), e.getLocalizedMessage().contains("Unexpected character"), is(true));
        }
    }

    @Test
    public void getOnMainLevelForJsonArray() throws Throwable {
        final String happyPath = "[{\"id\":22295,\"key\":\"group.id:project.id:master\",\"name\":\"group.id:project.id master\",\"scope\":\"PRJ\",\"qualifier\":\"TRK\",\"date\":\"2016-05-03T14:04:45+0200\",\"creationDate\":\"2016-05-02T16:32:23+0200\",\"lname\":\"group.id:project.id master\",\"version\":\"0.0.1-SNAPSHOT\",\"branch\":\"master\",\"description\":\"\"}]";
        assertThat(JsonUtil.getOnMainLevel(happyPath, "id"), equalTo("22295"));
        assertThat(JsonUtil.getOnMainLevel(happyPath, "scope"), equalTo("PRJ"));
        assertThat(JsonUtil.getOnMainLevel(happyPath, "key"), equalTo("group.id:project.id:master"));
        assertThat(JsonUtil.getOnMainLevel(null, "id"), nullValue());
        assertThat(JsonUtil.getOnMainLevel(MEANINGLESS_JSON, "id"), nullValue());
        try {
            JsonUtil.getOnMainLevel(INVALID_JSON, "id");
            fail("no error");
        } catch (SonarQualityException e) {
            assertThat(e.getLocalizedMessage(), e.getLocalizedMessage().contains("Unexpected character"), is(true));
        }
    }

    @Test
    public void testConstructorIsPrivate() throws Throwable {
        Constructor<JsonUtil> constructor = JsonUtil.class.getDeclaredConstructor();
        assertTrue( Modifier.isPrivate( constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

}
