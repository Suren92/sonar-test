/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.exceptions;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests for {@link SonarQualityException}
 *
 * Created by Vandeperre Maarten on 08/05/2016.
 */
public class TestSonarQualityException {

    @Test
    public void exceptionWithCause() throws Throwable {
        final Exception cause = new Exception("test");
        try {
            throw new SonarQualityException("", cause);
        } catch (SonarQualityException e){
            assertThat(e.getCause(), equalTo(cause));
            assertThat(e.getCause().getLocalizedMessage(), equalTo("test"));
        }
    }

    @Test
    public void exceptionWithMessage() throws Throwable {
        try {
            throw new SonarQualityException("test");
        } catch (SonarQualityException e){
            assertThat(e.getLocalizedMessage(), equalTo("test"));
        }
    }
}
