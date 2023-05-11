package com.pnambic.depanfx.workspace.gui;

import java.util.List;

import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class DepanFxWorkspaceItem extends TreeItem<DepanFxWorkspaceMember> {

  private boolean isFirstTimeChildren = false;

  public DepanFxWorkspaceItem(DepanFxWorkspace workspace) {
    super(workspace);
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

    ObservableList<TreeItem<DepanFxWorkspaceMember>> result = FXCollections.observableArrayList();
    for (DepanFxProjectTree project : projects) {
      result.add(new DepanFxProjectTreeItem(project));
    }
    return result;
  }
}
