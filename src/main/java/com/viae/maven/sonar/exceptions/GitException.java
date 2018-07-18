/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.exceptions;

/**
 * Exception to be thrown when something goes wrong when handling git processes.
 *
 * Created by Vandeperre Maarten on 05/05/2016.
 */
public class GitException  extends Exception {
    private static final String GIT_ERROR_MESSAGE = "Something went wrong while executing GIT command, verify that git is installed properly on the system";

    public GitException(final Exception cause){
        super(GIT_ERROR_MESSAGE, cause);
    }
}
