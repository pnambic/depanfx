package com.pnambic.depanfx.perspective.workspace.controls;

import com.pnambic.depanfx.perspective.plugins.DepanFxResourceMenuRegistry;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * 'cuz {@code DepanFxWorkspace} doesn't know anything about menus.
 */
public class DepanFxProjectListCell extends ListCell<DepanFxWorkspaceMember> {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxProjectListCell.class);

  // Menu texts
  private static final String DELETE_DOCUMENT = "Delete Document";

  private static final String DELETE_CONTAINER = "Delete Container";

  // Our state
  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxResourceMenuRegistry rsrcMenuRegistry;

  public DepanFxProjectListCell(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxResourceMenuRegistry rsrcMenuRegistry) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
    this.rsrcMenuRegistry = rsrcMenuRegistry;
  }

  @Override
  protected void updateItem(DepanFxWorkspaceMember member, boolean empty) {
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
      setGraphic(getCellIcon(member));
      setContextMenu(buildMemberContextMenu(member));
      return;
    }
    // Something unexpected.
    setText("<null>");
    setGraphic(null);
    setContextMenu(null);
  }

  private String getCellText(DepanFxWorkspaceMember member) {
    return member.getMemberName();
  }

  private ImageView getCellIcon(DepanFxWorkspaceMember member) {
    return getIconImage(member)
        .map(i -> new ImageView(i))
        .orElse(null);
  }

  private Optional<Image> getIconImage(DepanFxWorkspaceMember member) {
    switch (member) {
    case DepanFxProjectDocument document:
      return DepanFxWorkspaceIcons.loadDepanIcon(
          DepanFxWorkspaceIcons.Icon.DOCUMENT);
    case DepanFxProjectContainer container:
      return DepanFxWorkspaceIcons.loadDepanIcon(
          DepanFxWorkspaceIcons.Icon.CONTAINER);
    default:
      break;
    }
    return DepanFxWorkspaceIcons.loadDepanIcon(
        DepanFxWorkspaceIcons.Icon.DOCUMENT);
  }

  private ContextMenu buildMemberContextMenu(DepanFxWorkspaceMember member) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();

    // Specific menus for documents (based on extensions) shown first.
    if (member instanceof DepanFxProjectDocument document) {
      rsrcMenuRegistry.prepareDocumentMenu(
          dialogRunner, workspace, this, document, builder);
    }
    if (member instanceof DepanFxProjectMember project) {
      rsrcMenuRegistry.prepareMemberMenu(
          dialogRunner, workspace, this, project, builder);
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

  private void appendDeleteDocument(
      DepanFxContextMenuBuilder builder, DepanFxProjectDocument projDoc) {
    builder.appendConditionalSeparator();
    builder.appendActionItem(DELETE_DOCUMENT,
        e -> projDoc.getProject().deleteDocument(projDoc));
  }
}
