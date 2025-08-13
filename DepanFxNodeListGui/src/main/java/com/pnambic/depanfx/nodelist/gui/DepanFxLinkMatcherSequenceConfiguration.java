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
package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcherSequenceDocument;
import com.pnambic.depanfx.edgematchers.gui.DepanFxLinkMatcherSequenceToolDialog;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourcePathMenuContribution;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceRegistry;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxMenuItemFactory;
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
public class DepanFxLinkMatcherSequenceConfiguration {

  public static final String LINK_MATCHER_SEQUENCE = "Link Matcher Sequence";

  public static final String EDIT_LINK_MATCHER_SEQUENCE_FILTER =
      "Edit Link Matcher Sequence...";

  public static final String NEW_LINK_MATCHER_FILTER =
      "New Link Matcher Sequence...";

  private static final String LINK_MATCHER_SEQUENCE_LABEL =
      LINK_MATCHER_SEQUENCE;

  private static final String LINK_MATCHER_SEQUENCE_KEY =
      LINK_MATCHER_SEQUENCE;

  private static final String LINK_MATCHER_SEQUENCE_TOOL_NAME =
      LINK_MATCHER_SEQUENCE;

  private static final String LINK_MATCHER_SEQUENCE_TOOL_DESCR =
      "Link matcher sequence.";

  @Bean
  public DepanFxResourceRegistry.Contribution linkMatcherFileOpenContrib() {
    return new LinkMatcherSequenceFileOpenContribution();
  }

  @Bean
  public DepanFxResourcePathMenuContribution linkMatcherSequencePathMenu() {
    return new LinkMatcherSequencePathContribution();
  }

  @Bean
  public DepanFxNewResourceContribution linkMatcherSequenceNewMenu(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    return new NewContribution(workspace, dialogRunner);
  }

  private static class LinkMatcherSequenceFileOpenContribution extends
      DepanFxResourceRegistry.Principal<DepanFxLinkMatcherSequenceDocument> {

    private LinkMatcherSequenceFileOpenContribution() {
      super(
          LINK_MATCHER_SEQUENCE_LABEL,
          DepanFxLinkMatcherSequenceDocument.class,
          DepanFxLinkMatcherSequenceDocument.LINK_MATCHER_SEQUENCE_TOOL_EXT,
          LINK_MATCHER_SEQUENCE_KEY);
    }

    @Override
    protected void runDialog(DepanFxDialogRunner dialogRunner,
        DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> wkspRsrc) {
      DepanFxLinkMatcherSequenceToolDialog.runEditDialog(
          wkspRsrc, dialogRunner);
    }
  }

  private static class LinkMatcherSequencePathContribution
      implements DepanFxResourcePathMenuContribution {

    @Override
    public boolean acceptsPath(Path rsrcPath) {
      return DepanFxLinkMatcherSequenceDocument.LINK_MATCHER_SEQUENCE_TOOL_PATH
          .equals(rsrcPath);
    }

    @Override
    public void prepareCell(
        DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner,
        Cell<DepanFxWorkspaceMember> cell,
        DepanFxProjectMember member, DepanFxContextMenuBuilder builder) {
      builder.appendActionItem(NEW_LINK_MATCHER_FILTER,
          e -> runCreateDialog(workspace, dialogRunner));
    }

    @Override
    public String getOrderKey() {
      return LINK_MATCHER_SEQUENCE_KEY;
    }
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
          LINK_MATCHER_SEQUENCE, e -> runCreateDialog(workspace, dialogRunner));
    }
  }

  private static void runCreateDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    DepanFxLinkMatcherSequenceDocument newMatcherSeq =
        new DepanFxLinkMatcherSequenceDocument(
            LINK_MATCHER_SEQUENCE_TOOL_NAME, LINK_MATCHER_SEQUENCE_TOOL_DESCR,
            null, Collections.emptyList());

    DepanFxLinkMatcherSequenceToolDialog.runCreateDialog(
        workspace.addScratchResource(newMatcherSeq), dialogRunner);
  }
}
