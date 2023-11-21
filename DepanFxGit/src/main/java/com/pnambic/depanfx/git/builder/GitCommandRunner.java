/*
 * Copyright 2023 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pnambic.depanfx.git.builder;

import com.pnambic.depanfx.git.tooldata.DepanFxGitRepoData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GitCommandRunner {

  public static final String DEFAULT_GIT_EXE = "git.exe";

  @SuppressWarnings("serial")
  public static class GitRunnerException extends RuntimeException {

    private final List<String> command;

    public GitRunnerException(List<String> command) {
      this.command = command;
    }

    protected StringBuilder initMessage() {
      StringBuilder result = new StringBuilder();
      result.append("Git command");
      command.stream().forEach(c -> result.append(" " + c));
      return result;
    }
  }

  @SuppressWarnings("serial")
  public static class GitCommandException extends GitRunnerException {

    private final int exitCode;

    public GitCommandException(List<String> command, int exitCode) {
      super(command);
      this.exitCode = exitCode;
    }

    @Override
    public String getMessage() {
      StringBuilder result = initMessage();
      result.append("failed with exit code ");
      result.append(exitCode);
      return result.toString();
    }
  }

  @SuppressWarnings("serial")
  public class GitSystemException extends GitRunnerException {

    public GitSystemException(List<String> command, Exception errAny) {
      super(command);
      this.initCause(errAny);
    }

    @Override
    public String getMessage() {
      StringBuilder result = initMessage();
      result.append(" failed with exception ");
      result.append(getCause().getClass().getName());
      return result.toString();
    }
  }

  private static final Logger LOG =
      LoggerFactory.getLogger(GitCommandRunner.class);

  private final DepanFxGitRepoData repoData;

  public GitCommandRunner(DepanFxGitRepoData repoData) {
    this.repoData = repoData;
  }

  public String getGitRepoName() {
    return repoData.getGitRepoName();
  }

  public String getGitRepoPath() {
    return repoData.getGitRepoPath();
  }

  public List<String> runGitCommand(String command) {
    return runGitCommand(Collections.singletonList(command));
  }

  public List<String> runGitCommand(List<String> command) {
    List<String> runCommand = buildRunCommand(command);
    ProcessBuilder processBuilder = new ProcessBuilder(runCommand);
    processBuilder.directory(new java.io.File(getGitRepoPath()));

    try {
      Process process = processBuilder.start();

      try (BufferedReader reader =
          new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        List<String> result = reader.lines().collect(Collectors.toList());

        if (!process.waitFor(10, TimeUnit.SECONDS)) {
          process.destroy();
          LOG.error("Git command timed out");
        }

        int exitCode = process.exitValue();
        if (exitCode != 0) {
          LOG.error("Git command {} failed with error {}",
              runCommand, exitCode);
          throw new GitCommandException(command, exitCode);
        }
        return result;
      } catch (InterruptedException errBreak) {
        throw new GitSystemException(command, errBreak);
      }
    } catch (IOException errIo) {
      throw new GitSystemException(command, errIo);
    }
  }

  private List<String> buildRunCommand(List<String> command) {
    List<String> result = new ArrayList<>(command.size() + 1);
    result.add(repoData.getGitExe());
    result.addAll(command);
    return result;
  }
}
