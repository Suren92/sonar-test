/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.services;

import com.viae.maven.sonar.exceptions.GitException;

/**
 * Interface to interact with the GIT system.
 *
 * Created by Vandeperre Maarten on 05/05/2016.
 */
public interface GitService {

    /**
     * Retrieve the git branch name from the branch of the current workspace.
     *
     * @param runtime, the active runtime.
     * @return the git branch name or an empty string.
     * @throws GitException will be thrown when something goes wrong while retrieving GIT information
     */
    String getBranchName(Runtime runtime) throws GitException;
}
