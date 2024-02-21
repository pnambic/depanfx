package com.pnambic.depanfx.workspace.gui;

import com.pnambic.depanfx.perspective.plugins.DepanFxResourceMenuRegistry;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneController;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeCell;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * 'cuz {@code DepanFxWorkspace} doesn't know anything about menus.
 */
public class DepanFxProjectListCell extends TreeCell<DepanFxWorkspaceMember> {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxProjectListCell.class);

  // Menu texts
  private static final String DELETE_DOCUMENT = "Delete Document";

  private static final String DELETE_CONTAINER = "Delete Container";

  private static final String SET_AS_CURRENT_PROJECT = "Set As Current Project";

  // Our state
  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxResourceMenuRegistry rsrcMenuRegistry;

  private final DepanFxSceneController scene;

  // Manage Font tweeks (e.g. embolden).
  private Font previousFont;

  public DepanFxProjectListCell(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxResourceMenuRegistry rsrcMenuRegistry,
      DepanFxSceneController scene) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
    this.rsrcMenuRegistry = rsrcMenuRegistry;
    this.scene = scene;
  }

  @Override
  protected void updateItem(DepanFxWorkspaceMember member, boolean empty) {
    restoreFont();
    super.updateItem(member, empty);

    // Visual space reserved for future use.
    if (empty) {
      setText(null);
      setGraphic(null);
      setContextMenu(null);
      return;
    }
    // The normal case.
    if (member != null) {
      setText(getCellText(member));
      setGraphic(null);
      setContextMenu(buildMemberContextMenu(member));
      return;
    }
    // Something unexpected.
    setText("<null>");
    setGraphic(null);
    setContextMenu(null);
  }

  private String getCellText(DepanFxWorkspaceMember member) {
    if (isCurrentProject(member)) {
      embolden();
      return member.getMemberName() + " [curr]";
    }
    return member.getMemberName();
  }

  private void restoreFont() {
    if (previousFont != null) {
      setFont(previousFont);
      previousFont = null;
    }
  }

  private void embolden() {
    previousFont = getFont();
    setFont(
        Font.font(previousFont.getFamily(),
            FontWeight.BOLD,
            previousFont.getSize()));
  }

  private ContextMenu buildMemberContextMenu(DepanFxWorkspaceMember member) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();

    // Specific menus for documents (based on extensions) shown first.
    if (member instanceof DepanFxProjectDocument) {
      rsrcMenuRegistry.prepareDocumentMenu(
          dialogRunner, workspace, this,
          (DepanFxProjectDocument) member, builder);
      rsrcMenuRegistry.prepareAnalysisMenu(
          scene, dialogRunner, workspace, this,
          (DepanFxProjectDocument) member, builder);
    }
    if (member instanceof DepanFxProjectMember) {
      rsrcMenuRegistry.prepareMemberMenu(
          dialogRunner, workspace, this,
          (DepanFxProjectMember) member, builder);
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
}
