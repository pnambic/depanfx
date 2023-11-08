package com.pnambic.depanfx.workspace.gui;

import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxProjectTree.ProjectTreeListener;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class DepanFxProjectTreeItem extends TreeItem<DepanFxWorkspaceMember> {

  private boolean isFirstTimeChildren = true;

  public DepanFxProjectTreeItem(DepanFxProjectTree project) {
    super(project);
    setExpanded(true);
    project.addListener(new UpdateListener());
  }

  @Override
  public boolean isLeaf() {
    return false;
  }

  @Override
  public ObservableList<TreeItem<DepanFxWorkspaceMember>> getChildren() {
    if (isFirstTimeChildren) {
      isFirstTimeChildren = false;
      DepanFxProjectMemberItemBuilder builder =
          new DepanFxProjectMemberItemBuilder((DepanFxProjectMember) getValue());
      super.getChildren().setAll(builder.buildChildren());
    }

    return super.getChildren();
  }

  /**
   * If the parent for the updated member is absent, the operation is
   * affecting the root.  The should probably generate an error, but for
   * now it is quietly ignored.  Mostly, the UI avoids generating delete
   * requests for root members.
   */
  private class UpdateListener implements ProjectTreeListener {

    @Override
    public void onContainerAdded(DepanFxProjectContainer projDir) {
      projDir.getParent()
          .map(c -> findContainerItem(DepanFxProjectTreeItem.this, c))
          .ifPresent(c -> c.getChildren().add(
                new DepanFxProjectContainerItem(projDir)));
    }

    @Override
    public void onContainerDeleted(DepanFxProjectContainer projDir) {
      Optional<TreeItem<DepanFxWorkspaceMember>> optParentItem =
          projDir.getParent()
              .map(c -> findContainerItem(DepanFxProjectTreeItem.this, c));
      Optional<TreeItem<DepanFxWorkspaceMember>> optMemberItem =
          optParentItem .map(i -> findMemberItem(i, projDir));
      if (optMemberItem.isPresent()) {
        optParentItem.get().getChildren().remove(optMemberItem.get());
      }
    }

    public void onDocumentAdded(DepanFxProjectDocument projDoc) {
      projDoc.getParent()
          .map(c -> findContainerItem(DepanFxProjectTreeItem.this, c))
          .ifPresent(c -> c.getChildren().add(
              new DepanFxProjectDocumentItem(projDoc)));
    }

    @Override
    public void onDocumentDeleted(DepanFxProjectDocument projDoc) {
      Optional<TreeItem<DepanFxWorkspaceMember>> optParentItem =
          projDoc.getParent()
              .map(c -> findContainerItem(DepanFxProjectTreeItem.this, c));
      Optional<TreeItem<DepanFxWorkspaceMember>> optMemberItem =
          optParentItem .map(i -> findMemberItem(i, projDoc));
      if (optMemberItem.isPresent()) {
        optParentItem.get().getChildren().remove(optMemberItem.get());
      }
    }
  }

  private TreeItem<DepanFxWorkspaceMember> findMemberItem(
      TreeItem<DepanFxWorkspaceMember> container,
      DepanFxProjectMember member) {
    return container.getChildren().stream()
        .filter(ci -> member.getMemberPath().equals(
            ((DepanFxProjectMember) ci.getValue()).getMemberPath()))
        .findFirst()
        .orElse(null);
  }

  private TreeItem<DepanFxWorkspaceMember> findContainerItem(
      TreeItem<DepanFxWorkspaceMember> container,
      DepanFxProjectContainer projDir) {

    for (TreeItem<DepanFxWorkspaceMember> child : container.getChildren()) {
      DepanFxProjectMember projMember = (DepanFxProjectMember) child.getValue();
      if (projMember instanceof DepanFxProjectContainer) {
        if (projMember.getMemberPath().equals(projDir.getMemberPath())) {
          return child;
        }
        // Found an ancestor .. recurse to find the parent.
        if (projDir.getMemberPath().startsWith(projMember.getMemberPath())) {
          return findContainerItem(child, projDir);
        }
      }
    }
    return null;
  }
}
