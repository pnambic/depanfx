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
package com.pnambic.depanfx.edgematchers.gui;

import com.pnambic.depanfx.edgematchers.tooldata.DepanFxBaseMatcherDocument;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceChooser;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilterModel;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.util.Optional;
import java.util.function.BiConsumer;

import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;

/**
 * User selection of a link matcher resources.
 *
 * In general, any link matcher is acceptable.
 */
public class DepanFxLinkMatcherChooser {

  public static final String SELECT_LINK_MATCHER = "Select Link Matcher...";

  /**
   * Bind a pop-up to text field for the resource name.
   */
  public static class LinkMatcherControl {

    public final DepanFxWorkspace workspace;

    private final DepanFxDialogRunner dialogRunner;

    private final DepanFxEdgeMatcherDialogRegistry matcherDialogRegistry;

    private DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>
        linkMatcherRsrc;

    private final TextField linkMatcherField;

    public LinkMatcherControl(
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        DepanFxEdgeMatcherDialogRegistry matcherDialogRegistry,
        TextField linkMatcherField) {
      this.workspace = workspace;
      this.dialogRunner = dialogRunner;
      this.matcherDialogRegistry = matcherDialogRegistry;
      this.linkMatcherField = linkMatcherField;
      linkMatcherField.setContextMenu(buildContextMenu());
    }

    public void setLinkMatcherResource(
        DepanFxWorkspaceResource<DepanFxBaseMatcherDocument> linkMatcherRsrc) {
      this.linkMatcherRsrc = linkMatcherRsrc;
      linkMatcherField.setText(getLinkMatcherRsrcName());
    }

    public DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>
        getLinkMatcherResource() {

      return linkMatcherRsrc;
    }

    public String getLinkMatcherRsrcName() {
      if (linkMatcherRsrc != null) {
        return DepanFxProjects.asReferenceLabel(workspace, linkMatcherRsrc);
      }
      // Let the text input field show a prompt text.
      return null;
    }

    public void runLinkMatcherFinder() {
      DepanFxLinkMatcherChooser
          .runLinkMatcherFinder(
                workspace, dialogRunner, linkMatcherField.getScene(),
                matcherDialogRegistry)
          .ifPresent(this::setLinkMatcherResource);
    }

    private ContextMenu buildContextMenu() {
      DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
      builder.appendActionItem(
          SELECT_LINK_MATCHER, e -> runLinkMatcherFinder());
      return builder.build();
    }
  }

  /**
   * Bind a pop-up to table cell for the resource name.
   */
  public static class LinkMatcherCell<T>
      extends TableCell<T, String> {

    private final DepanFxWorkspace workspace;

    private final DepanFxDialogRunner dialogRunner;

    private final Scene scene;

    private final DepanFxEdgeMatcherDialogRegistry matcherDialogRegistry;

    private final BiConsumer<
            TableRow<T>, DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>>
        matcherConsumer;

    public LinkMatcherCell(
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        Scene scene,
        DepanFxEdgeMatcherDialogRegistry matcherDialogRegistry,
        BiConsumer<
            TableRow<T>,
            DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>>
            matcherConsumer) {
      this.workspace = workspace;
      this.dialogRunner = dialogRunner;
      this.scene = scene;
      this.matcherDialogRegistry = matcherDialogRegistry;
      this.matcherConsumer = matcherConsumer;
    }

    @Override
    protected void updateItem(String displayName, boolean empty) {
      super.updateItem(displayName, empty);

      if (empty) {
        setGraphic(null);
        setContextMenu(null);
        return;
      }
      setText(displayName);
      setContextMenu(buildContextMenu());
    }

    private ContextMenu buildContextMenu() {
      DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
      builder.appendActionItem(
          SELECT_LINK_MATCHER, e -> runLinkMatcherFinder());
      return builder.build();
    }

    private void runLinkMatcherFinder() {
      DepanFxLinkMatcherChooser
          .runLinkMatcherFinder(
              workspace, dialogRunner, scene, matcherDialogRegistry)
          .ifPresent(r -> matcherConsumer.accept(getTableRow(), r));
    }
  }

  /**
   * Provide an existing link matcher.
   */
  public static Optional<DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>>
  runLinkMatcherFinder(
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        Scene scene,
        DepanFxEdgeMatcherDialogRegistry matcherDialogRegistry) {

    DepanFxResourceChooser chooser = prepareChooser(
        workspace, dialogRunner, matcherDialogRegistry);
    return chooser.showOpenDialog(scene)
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(p -> workspace.getWorkspaceResource(
            p, DepanFxBaseMatcherDocument.class));
  }

  private static DepanFxResourceChooser prepareChooser(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxEdgeMatcherDialogRegistry matcherDialogRegistry) {

    DepanFxResourceChooser result =
        new DepanFxResourceChooser(workspace, dialogRunner);
    DepanFxResourcePerspectives.prepareResourceFinder(
        result, DepanFxProjects.TOOLS_PATH);

    DepanFxResourceFilterModel matcherFilter =
        matcherDialogRegistry.getResourceFilter();
    result.getExtensionFilters().add(matcherFilter);
    result.setSelectedExtensionFilter(matcherFilter);
    return result;
  }
}
