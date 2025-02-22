/*
 * Copyright 2024 The Depan Project Authors
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
package com.pnambic.depanfx.nodefilters.gui;

import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxNodeFilterSequenceData;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourcePathMenuContribution;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceRegistry;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.plugins.DepanFxNewResourceContribution;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.Collections;

import javafx.scene.control.Cell;
import javafx.scene.control.MenuItem;

@Configuration
public class DepanFxNodeFilterSequenceConfiguration {

  private static final String NODE_FILTER_SEQUENCE = "Node Filter Sequence";

  public static final String EDIT_NODE_FILTER_SEQUENCE_FILTER =
      "Edit Node Filter Sequence...";

  public static final String NEW_NODE_FILTER_SEQUENCE_FILTER =
      "New Node Filter Sequence...";

  private static final String NODE_FILTER_SEQUENCE_LABEL =
      NODE_FILTER_SEQUENCE;

  private static final String NODE_FILTER_SEQUENCE_KEY =
      NODE_FILTER_SEQUENCE;

  private static final String NODE_FILTER_SEQUENCE_TOOL_NAME =
      NODE_FILTER_SEQUENCE;

  private static final String NODE_FILTER_SEQUENCE_TOOL_DESCR =
      "Node filter sequence.";

  @Bean
  public DepanFxResourceRegistry.Contribution
      nodeFilterFileOpenContribution() {
    return new NodeFileSequenceFileOpenContribution();
  }

  @Bean
  public DepanFxResourcePathMenuContribution nodeFilterSequencePathMenu() {
    return new NodeFilterSequencePathContribution();
  }

  @Bean
  public DepanFxNewResourceContribution nodeFilterSequenceNewMenu(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    return new NewContribution(workspace, dialogRunner);
  }

  private static class NodeFileSequenceFileOpenContribution
      extends DepanFxResourceRegistry.Principal<DepanFxNodeFilterSequenceData> {

    public NodeFileSequenceFileOpenContribution() {
      super(
          NODE_FILTER_SEQUENCE_LABEL,
          DepanFxNodeFilterSequenceData.class,
          DepanFxNodeFilterSequenceData.NODE_FILTER_SEQUENCE_TOOL_EXT,
          NODE_FILTER_SEQUENCE_KEY);
    }

    @Override
    protected void runDialog(
        DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData> wkspRsrc,
        DepanFxDialogRunner dialogRunner) {
      DepanFxNodeFilterSequenceToolDialog.runEditDialog(
          wkspRsrc, dialogRunner);
    }
  }

  private static class NodeFilterSequencePathContribution
      implements DepanFxResourcePathMenuContribution {

    @Override
    public boolean acceptsPath(Path rsrcPath) {
      return DepanFxBaseFilterData.NODE_FILTERS_TOOL_PATH.equals(rsrcPath);
    }

    @Override
    public void prepareCell(
        DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner,
        Cell<DepanFxWorkspaceMember> cell,
        DepanFxProjectMember member, DepanFxContextMenuBuilder builder) {
      builder.appendActionItem(NEW_NODE_FILTER_SEQUENCE_FILTER,
          e -> runCreateDialog(workspace, dialogRunner));
    }

    @Override
    public String getOrderKey() {
      return NODE_FILTER_SEQUENCE_KEY;
    }
  }

  private class NewContribution
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
      return DepanFxContextMenuBuilder.createActionItem(
          NODE_FILTER_SEQUENCE, e -> runCreateDialog(workspace, dialogRunner));
    }
  }

  private static void runCreateDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    DepanFxNodeFilterSequenceData newFilterSeq =
        new DepanFxNodeFilterSequenceData(
            NODE_FILTER_SEQUENCE_TOOL_NAME, NODE_FILTER_SEQUENCE_TOOL_DESCR,
            null, Collections.emptyList());
    DepanFxNodeFilterSequenceToolDialog.runCreateDialog(
        workspace.addScratchResource(newFilterSeq), dialogRunner);
  }
}
