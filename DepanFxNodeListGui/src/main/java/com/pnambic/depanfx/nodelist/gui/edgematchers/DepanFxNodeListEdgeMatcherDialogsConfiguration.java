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
package com.pnambic.depanfx.nodelist.gui.edgematchers;

import com.pnambic.depanfx.edgematchers.gui.DepanFxEdgeMatcherDialogRegistry;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxBaseMatcherDocument;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListEdgeMatcherData;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilterModel;
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

import javafx.scene.control.Cell;
import javafx.scene.control.MenuItem;

@Configuration
public class DepanFxNodeListEdgeMatcherDialogsConfiguration {

  public static final String NODE_LIST_MATCHER_ORDER_KEY = "Node List Matcher";

  public static final String NODE_LIST_MATCHER = "Node List Matcher";

  public static final String EDIT_NODE_LIST_MATCHER =
      "Edit Node List Matcher ...";

  public static final String NEW_NODE_LIST_MATCHER =
      "New Node List Matcher...";

  private static final String NODE_LIST_MATCHER_LABEL =
      NODE_LIST_MATCHER;

  private static final String NODE_LIST_MATCHER_KEY =
      NODE_LIST_MATCHER;

  private static final String NODE_LIST_MATCHER_TOOL_NAME =
      NODE_LIST_MATCHER;

  private static final String NODE_LIST_MATCHER_TOOL_DESCR =
      "Node list edge matcher.";

  @Bean
  public DepanFxResourceRegistry.Contribution nodeListMatcherFileOpenContrib() {
    return new NodeListMatcherFileOpenContribution();
  }

  @Bean
  public DepanFxResourcePathMenuContribution nodeListMatcherPathMenu() {
    return new NodeListMatcherPathContribution();
  }

  @Bean
  public DepanFxNewResourceContribution nodeListMatcherNewMenu(
      DepanFxDialogRunner dialogRunner) {
    return new NewContribution(dialogRunner);
  }

  @Bean
  public DepanFxEdgeMatcherDialogRegistry.Contribution
  nodeListMatcherDialogContribution(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    return new NodeListMatcherDialogContribution();
  }

  private static class NodeListMatcherFileOpenContribution extends
      DepanFxResourceRegistry.Principal<DepanFxNodeListEdgeMatcherData> {

    private NodeListMatcherFileOpenContribution() {
      super(
          NODE_LIST_MATCHER_LABEL,
          DepanFxNodeListEdgeMatcherData.class,
          DepanFxNodeListEdgeMatcherData.NODE_LIST_EDGE_MATCHER_TOOL_EXT,
          NODE_LIST_MATCHER_KEY);
    }

    @Override
    protected void runDialog(DepanFxDialogRunner dialogRunner,
        DepanFxWorkspaceResource<DepanFxNodeListEdgeMatcherData> wkspRsrc) {
      DepanFxNodeListEdgeMatcherDialog.runEditDialog(dialogRunner, wkspRsrc);
    }
  }

  private static class NodeListMatcherPathContribution
      implements DepanFxResourcePathMenuContribution {

    @Override
    public boolean acceptsPath(Path rsrcPath) {
      return DepanFxNodeListEdgeMatcherData.LINK_MATCHER_TOOL_PATH
          .equals(rsrcPath);
    }

    @Override
    public void prepareCell(
        DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner,
        Cell<DepanFxWorkspaceMember> cell,
        DepanFxProjectMember member, DepanFxContextMenuBuilder builder) {
      builder.appendActionItem(NEW_NODE_LIST_MATCHER,
          e -> runCreateDialog(dialogRunner));
    }

    @Override
    public String getOrderKey() {
      return NODE_LIST_MATCHER_ORDER_KEY;
    }
  }

  private static class NewContribution
    implements DepanFxNewResourceContribution {

    private final DepanFxDialogRunner dialogRunner;

    public NewContribution(DepanFxDialogRunner dialogRunner) {
      this.dialogRunner = dialogRunner;
    }

    @Override
    public MenuItem createNewResourceMenuItem() {
      return DepanFxMenuItemFactory.createActionItem(
          NEW_NODE_LIST_MATCHER, e -> runCreateDialog(dialogRunner));
    }
  }

  private static void runCreateDialog(DepanFxDialogRunner dialogRunner) {
    DepanFxNodeListEdgeMatcherDialog.runCreateDialog(dialogRunner);
  }

  private static class NodeListMatcherDialogContribution
      implements DepanFxEdgeMatcherDialogRegistry.Contribution {

    @Override
    public boolean accepts(DepanFxBaseMatcherDocument matcherInfo) {
      return DepanFxNodeListEdgeMatcherData.class
          .isAssignableFrom(matcherInfo.getClass());
    }

    @Override
    public String getOrderKey() {
      return NODE_LIST_MATCHER_ORDER_KEY;
    }

    @Override
    public DepanFxResourceFilterModel getResourceFilter() {
      return DepanFxNodeListEdgeMatcherDialog.NODE_LIST_EDGE_RSRC_FILTER;
    }
  }
}
