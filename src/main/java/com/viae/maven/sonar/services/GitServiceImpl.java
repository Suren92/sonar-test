/*
 * Copyright (c) 2016 by VIAE (http///viae-it.com)
 */

package com.viae.maven.sonar.services;

import com.viae.maven.sonar.exceptions.GitException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.maven.plugin.logging.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Implementation class of {@link GitService}
 * <p>
 * Created by Vandeperre Maarten on 05/05/2016.
 */
public class GitServiceImpl implements GitService {

    private final Log log;

    /**
     * @param log, the logging service, can't be null.
     */
    public GitServiceImpl(final Log log) {
        Validate.notNull(log, "log can't be null");
        this.log = log;
    }

    @Override
    public String getBranchName(final Runtime runtime) throws GitException {
        try {
            String sonarBranchName = "";
            final Process p = runtime.exec("git rev-parse --abbrev-ref HEAD");
            p.waitFor();

            final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            if (reader != null) {
                final String tempName = reader.readLine();
                if (StringUtils.isNotEmpty(tempName)) {
                    sonarBranchName = tempName.trim();
                }
            }
            log.info(String.format("set sonar.branch [%s]", sonarBranchName));
            return sonarBranchName;
        } catch (IOException | InterruptedException e) {
            throw new GitException(e);
        }
    }
}
