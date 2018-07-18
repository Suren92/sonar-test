/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.exceptions;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests for {@link com.viae.maven.sonar.exceptions.GitException}
 *
 * Created by Vandeperre Maarten on 08/05/2016.
 */
public class TestGitException {

    @Test
    public void exceptionWithCause() throws Throwable {
        final Exception cause = new Exception("test");
        try {
            throw new GitException(cause);
        } catch (GitException e){
            assertThat(e.getLocalizedMessage(), containsString("Something went wrong while executing GIT command, verify that git is installed properly on the system"));
            assertThat(e.getCause(), equalTo(cause));
            assertThat(e.getCause().getLocalizedMessage(), equalTo("test"));
        }
    }
}
