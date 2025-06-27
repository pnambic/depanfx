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
package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxMenuItemFactory;
import com.pnambic.depanfx.scene.plugins.DepanFxNewResourceContribution;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javafx.scene.control.MenuItem;

@Configuration
public class DepanFxNodeViewsMergeConfiguration {

  public static final String MERGE_NODE_VIEWS = "Merge Node Views";

  private static final String MERGE_NODE_VIEWS_LABEL =
      MERGE_NODE_VIEWS;

  private static final String LINK_MATCHER_SEQUENCE_TOOL_DESCR =
      "Merge node views.";

  @Bean
  public DepanFxNewResourceContribution nodeViewsMergeNewMenu(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    return new NewContribution(workspace, dialogRunner);
  }

  private static class NewContribution
    implements DepanFxNewResourceContribution {

    private final DepanFxWorkspace workspace;

    private final DepanFxDialogRunner dialogRunner;

    public NewContribution(
        DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
      this.workspace = workspace;
      this.dialogRunner = dialogRunner;
    }

    @Override
    public MenuItem createNewResourceMenuItem() {
      return DepanFxMenuItemFactory.createActionItem(
          MERGE_NODE_VIEWS_LABEL, e -> runCreateDialog(workspace, dialogRunner));
    }
  }

  private static void runCreateDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    DepanFxNodeViewsMergeDialog.runMergeDialog(workspace, dialogRunner);
  }
}
