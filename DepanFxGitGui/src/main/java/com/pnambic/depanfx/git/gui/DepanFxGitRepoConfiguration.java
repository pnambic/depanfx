/*
 * Copyright 2025 The Depan Project Authors
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
package com.pnambic.depanfx.git.gui;

import com.pnambic.depanfx.git.tooldata.DepanFxGitRepoData;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourcePathMenuContribution;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceRegistry;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.Map;

import javafx.scene.control.Cell;

@Configuration
public class DepanFxGitRepoConfiguration {

  public static final String GIT_REPO_LABEL = "git Repo";

  public static final String GIT_REPO_KEY = "git Repo";

  public static final String EDIT_GIT_REPO = "Edit git Repo Data...";

  private static final String NEW_GIT_REPO = "New git Repo Data...";

  private static Logger LOG =
      LoggerFactory.getLogger(DepanFxGitRepoConfiguration.class);

  @Bean
  public DepanFxResourceRegistry.Contribution
      gitRepoFileOpenContribution() {

    return new GitRepoDataFileOpenContribution();
  }

  @Bean
  public DepanFxResourcePathMenuContribution gitRepoPathMenu() {
    return new GitRepoDataPathMenuContribution();
  }

  private static class GitRepoDataFileOpenContribution
      extends DepanFxResourceRegistry.Principal<DepanFxGitRepoData> {

    public GitRepoDataFileOpenContribution() {
      super(
          GIT_REPO_LABEL,
          DepanFxGitRepoData.class,
          DepanFxGitRepoData.GIT_REPO_TOOL_EXT,
          GIT_REPO_KEY);
    }

    @Override
    protected void runDialog(
        DepanFxDialogRunner dialogRunner,
        Map<?, ?> loadContext,
        DepanFxWorkspaceResource<DepanFxGitRepoData> wkspRsrc) {
      DepanFxGitRepoToolDialog.runEditDialog(wkspRsrc, dialogRunner);
    }
  }

  private static class GitRepoDataPathMenuContribution
      implements DepanFxResourcePathMenuContribution {

    @Override
    public boolean acceptsPath(Path rsrcPath) {
      return DepanFxGitRepoData.GIT_REPOS_TOOL_PATH.equals(rsrcPath);
    }

    @Override
    public void prepareCell(
        DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner,
        Cell<DepanFxWorkspaceMember> cell,
        DepanFxProjectMember member, DepanFxContextMenuBuilder builder) {

      builder.appendActionItem(
          NEW_GIT_REPO,
          e -> runNewGitRepoAction(workspace, dialogRunner));
    }

    private void runNewGitRepoAction(
        DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
      try {
        DepanFxGitRepoData repoData =
            DepanFxGitRepoToolDialogs.buildInitialGitRepoData();
        DepanFxWorkspaceResource<DepanFxGitRepoData> repoRsrc =
            workspace.addScratchResource(repoData);
        DepanFxGitRepoToolDialog.runCreateDialog(repoRsrc, dialogRunner);
      } catch (RuntimeException errCaught) {
        LOG.error("Unable to create git repo data", errCaught);
      }
    }

    @Override
    public String getOrderKey() {
      return GIT_REPO_KEY;
    }
  }
}
