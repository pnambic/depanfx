package com.pnambic.depanfx.workspace.gui;

import java.util.List;
import java.util.Optional;

import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspace.WorkspaceListener;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class DepanFxWorkspaceItem extends TreeItem<DepanFxWorkspaceMember> {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxWorkspaceItem.class);

  private boolean isFirstTimeChildren = true;

  public DepanFxWorkspaceItem(DepanFxWorkspace workspace) {
    super(workspace);
    setExpanded(true);
    workspace.addListener(new UpdateListener());
  }

  @Override
  public boolean isLeaf() {
    return false;
  }

  @Override
  public ObservableList<TreeItem<DepanFxWorkspaceMember>> getChildren() {
    if (isFirstTimeChildren) {
      isFirstTimeChildren = false;
      super.getChildren().setAll(buildChildren());
    }

    return super.getChildren();
  }

  private ObservableList<TreeItem<DepanFxWorkspaceMember>> buildChildren() {

    DepanFxWorkspace workspace = (DepanFxWorkspace) getValue();
    List<DepanFxProjectTree> projects = workspace.getProjectList();
    if (projects.isEmpty()) {
      return FXCollections.emptyObservableList();
    }

    ObservableList<TreeItem<DepanFxWorkspaceMember>> result =
        FXCollections.observableArrayList();
    for (DepanFxProjectTree project : projects) {
      result.add(new DepanFxProjectTreeItem(project));
    }
    return result;
  }

  private class UpdateListener implements WorkspaceListener {

    @Override
    public void onProjectAdded(DepanFxProjectTree project) {
      findProjectItem(project).ifPresentOrElse(
          pi -> LOG.warn("Project {} cannot be added to workspace again",
              project.getMemberName()),
          () -> getChildren().add(new DepanFxProjectTreeItem(project)));
    }

    @Override
    public void onProjectDeleted(DepanFxProjectTree project) {
      findProjectItem(project).ifPresentOrElse(
          getChildren()::remove,
          () -> LOG.warn("Unknonwn project {} cannot be removed from workspace",
              project.getMemberName()));
    }

    @Override
    public void onProjectChanged(DepanFxProjectTree project) {
      findProjectItem(project).ifPresentOrElse(
          pi -> { pi.setValue(null); pi.setValue(project);},
          () -> LOG.warn("Unknown project {} cannot be updated in workspace",
              project.getMemberName()));
    }
  }

  private Optional<TreeItem<DepanFxWorkspaceMember>> findProjectItem(
      DepanFxProjectTree project) {

    return getChildren().stream()
        .filter(ci -> project.getMemberPath().equals(
            ((DepanFxProjectMember) ci.getValue()).getMemberPath()))
        .findFirst();
  }
}
