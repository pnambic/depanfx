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
package com.pnambic.depanfx.perspective.workspace.controls;

import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.plugins.DepanFxOrderableContribution;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceMenuRegistry;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceRegistry;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.function.Function;

import javafx.scene.control.Cell;
import javafx.scene.control.ContextMenu;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Common behaviors for tree or list cells that contain
 * {@link DepanFxWorkspaceMember}s.
 */
public class DepanFxWorkspaceMemberCells {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxWorkspaceMemberCells.class);

  // Menu texts
  private static final String DELETE_DOCUMENT = "Delete Document";

  private static final String DELETE_CONTAINER = "Delete Container";

  private static final String SET_AS_CURRENT_PROJECT = "Set As Current Project";

  // Our state
  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxResourceRegistry rsrcRegistry;

  private final DepanFxResourceMenuRegistry rsrcMenuRegistry;

  private final Function<DepanFxWorkspaceMember, ImageView> rsrcImageSrc;

  // Manage Font tweeks (e.g. embolden).
  private Font previousFont;

  public DepanFxWorkspaceMemberCells(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxResourceRegistry rsrcRegistry,
      DepanFxResourceMenuRegistry rsrcMenuRegistry,
      Function<DepanFxWorkspaceMember, ImageView> rsrcImageSrc) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
    this.rsrcRegistry = rsrcRegistry;
    this.rsrcMenuRegistry = rsrcMenuRegistry;
    this.rsrcImageSrc = rsrcImageSrc;
  }

  public void updateItem(
      Cell<DepanFxWorkspaceMember> cell,
      DepanFxWorkspaceMember member,
      boolean empty) {

    // Visual space reserved for future use.
    if (empty) {
      cell.setText(null);
      cell.setContextMenu(null);
      cell.setGraphic(null);
      return;
    }
    // The normal case.
    if (member != null) {
      cell.setText(getCellText(cell, member));
      cell.setContextMenu(buildMemberContextMenu(cell, member));
      cell.setGraphic(rsrcImageSrc.apply(member));
      return;
    }
    // Something unexpected.
    cell.setText("<null>");
    cell.setContextMenu(null);
    cell.setGraphic(null);
  }

  private String getCellText(
      Cell<DepanFxWorkspaceMember> cell, DepanFxWorkspaceMember member) {
    if (isCurrentProject(member)) {
      embolden(cell);
      return member.getMemberName() + " [curr]";
    }
    return member.getMemberName();
  }

  public void restoreFont(Cell<DepanFxWorkspaceMember> cell) {
    if (previousFont != null) {
      cell.setFont(previousFont);
      previousFont = null;
    }
  }

  private void embolden(Cell<DepanFxWorkspaceMember> cell) {
    previousFont = cell.getFont();
    cell.setFont(
        Font.font(previousFont.getFamily(),
            FontWeight.BOLD,
            previousFont.getSize()));
  }

  private ContextMenu buildMemberContextMenu(
      Cell<DepanFxWorkspaceMember> cell, DepanFxWorkspaceMember member) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();

    // Specific menus for documents (based on extensions) shown first.
    if (member instanceof DepanFxProjectDocument document) {
      prepareDocumentMenu(
          dialogRunner, workspace, cell, document, builder);
    }
    if (member instanceof DepanFxProjectMember project) {
      prepareProjectMenu(
          dialogRunner, workspace, cell, project, builder);
    }
    // Projects trees are not project members or documents.
    if (member instanceof DepanFxProjectTree) {
      appendProjectContextMenu(builder, (DepanFxProjectTree) member);
    }
    // Delete, if available, comes last
    if (member instanceof DepanFxProjectDocument) {
      // Don't offer delete for documents in the built-in project.
      DepanFxProjectDocument document = (DepanFxProjectDocument) member;
      if (!document.getProject().equals(workspace.getBuiltInProjectTree())) {
        appendDeleteDocument(builder, document);
      }
    }

    return builder.build();
  }

  private void prepareDocumentMenu(
      DepanFxDialogRunner dialogRunner,
      DepanFxWorkspace workspace,
      Cell<DepanFxWorkspaceMember> cell,
      DepanFxProjectDocument document,
      DepanFxContextMenuBuilder builder) {

    rsrcRegistry.streamContributions(workspace, document)
        .sorted(DepanFxOrderableContribution.CONTRIB_COMPARE)
        .forEach(c -> prepareDocumentCell(
            workspace, dialogRunner, cell, c, document, builder));
  }

  private void prepareProjectMenu(
      DepanFxDialogRunner dialogRunner,
      DepanFxWorkspace workspace,
      Cell<DepanFxWorkspaceMember> cell,
      DepanFxProjectMember project,
      DepanFxContextMenuBuilder builder) {

    // Built-in project cannot create new documents.
    if (project.getProject().equals(workspace.getBuiltInProjectTree())) {
      return;
    }

    rsrcMenuRegistry.prepareMemberMenu(
        dialogRunner, workspace, cell, project, builder);
  }

  private void prepareDocumentCell(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      Cell<DepanFxWorkspaceMember> cell,
      DepanFxResourceRegistry.Contribution contrib,
      DepanFxProjectDocument document,
      DepanFxContextMenuBuilder builder) {
    DepanFxResourcePerspectives.installOnOpen(cell, document.getMemberPath(),
        p -> contrib.openDocument(workspace, dialogRunner, document));
    builder.appendActionItem(
        fmtEditAction(contrib),
        e -> contrib.openDocument(workspace, dialogRunner, document));
  }

  private void appendProjectContextMenu(
      DepanFxContextMenuBuilder builder, DepanFxProjectTree project) {

    // Built-in project is never a candidate for the current project
    // Show, but disable for current project
    if (project != workspace.getBuiltInProjectTree()) {
      builder.appendActionItem(SET_AS_CURRENT_PROJECT,
          e -> workspace.setCurrentProject(project))
          .setDisable(isCurrentProject(project));
    }
  }

  private void appendDeleteDocument(
      DepanFxContextMenuBuilder builder, DepanFxProjectDocument projDoc) {
    builder.appendConditionalSeparator();
    builder.appendActionItem(DELETE_DOCUMENT,
        e -> projDoc.getProject().deleteDocument(projDoc));
  }

  private boolean isCurrentProject(DepanFxWorkspaceMember member) {
    if (member instanceof DepanFxProjectTree) {
      return workspace.getCurrentProject().filter(member::equals).isPresent();
    }
    return false;
  }

  private String fmtEditAction(
      DepanFxResourceRegistry.Contribution contrib) {
    return MessageFormat.format("Edit {0}...", contrib.getResourceLabel());
  }
}
