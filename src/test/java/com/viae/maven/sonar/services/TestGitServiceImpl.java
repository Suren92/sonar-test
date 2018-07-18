/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.services;

import com.viae.maven.sonar.GlobalSettings;
import com.viae.maven.sonar.exceptions.GitException;
import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link GitServiceImpl}
 *
 * Created by Vandeperre Maarten on 05/05/2016.
 */
public class TestGitServiceImpl {
    private static final String GIT_GET_BRANCH_NAME_COMMAND = "git rev-parse --abbrev-ref HEAD";

    private Runtime runtime = mock(Runtime.class);
    private Log log = mock(Log.class);
    private Process process = mock(Process.class);
    private final GitService gitService = new GitServiceImpl(log);

    @Before
    public void setupFreshFixture() throws Throwable {
        reset(runtime);
        reset(log);
        reset(process);
        doReturn(process).when(runtime).exec(eq(GIT_GET_BRANCH_NAME_COMMAND));
    }

    @Test
    public void constructor(){
        constructorTest((noArg) -> new GitServiceImpl(null), "log");
    }

    @Test
    public void getGitBranchForGitNotExisting() throws Throwable {
        doThrow(new IOException("exception on runtime")).when(runtime).exec(anyString());
        try {
            gitService.getBranchName(runtime);
            fail("no error");
        } catch (GitException e) {
            String gitErrorMessage = "Something went wrong while executing GIT command, verify that git is installed properly on the system";
            assertTrue(e.getLocalizedMessage(), e.getLocalizedMessage().contains(gitErrorMessage));
        }
    }

    @Test
    public void getGitBranchForEmptyProcessResponse() throws Throwable {
        InputStream processOutput = new ByteArrayInputStream(" ".getBytes());
        doReturn(processOutput).when(process).getInputStream();
        assertThat(gitService.getBranchName(runtime), equalTo(""));
        verify(process, times(1)).waitFor();
    }

    @Test
    public void getGitBranchFornullProcessResponse() throws Throwable {
        InputStream is = mock(InputStream.class);
        doReturn(-1).when(is).read(any(byte[].class), anyInt(), anyInt());
        doReturn(is).when(process).getInputStream();
        assertThat(gitService.getBranchName(runtime), equalTo(""));
        verify(process, times(1)).waitFor();
    }

    @Test
    public void getGitBranchForProcessResponse() throws Throwable {
        InputStream processOutput = new ByteArrayInputStream("test".getBytes());
        doReturn(processOutput).when(process).getInputStream();
        assertThat(gitService.getBranchName(runtime), equalTo("test"));
        verify(process, times(1)).waitFor();
    }

    @Test
    public void realRuntime() throws Throwable {
        GitServiceImpl service = new GitServiceImpl(log);
        String branchName = service.getBranchName(Runtime.getRuntime());
        assertThat(branchName, equalTo(GlobalSettings.BRANCH_NAME));
        verify(log, times(1)).info("set sonar.branch [" + GlobalSettings.BRANCH_NAME + "]");
    }

    private void constructorTest(final Consumer<Void> constructorCall, final String errorMessage) {
        try {
            constructorCall.accept(null);
            fail("no error");
        } catch (NullPointerException e) {
            assertTrue(e.getLocalizedMessage(), e.getLocalizedMessage().contains(errorMessage));
        }
    }
}
