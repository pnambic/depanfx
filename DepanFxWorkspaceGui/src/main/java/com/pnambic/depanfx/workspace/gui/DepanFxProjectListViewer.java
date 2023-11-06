package com.pnambic.depanfx.workspace.gui;

import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherRegistry;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneController;
import com.pnambic.depanfx.scene.plugins.DepanFxNewResourceRegistry;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.io.File;
import java.nio.file.Path;

import javafx.scene.control.Tab;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;

public class DepanFxProjectListViewer {

  private static final String PROJECTS = "Projects";

  private static final String NEW_PROJECT_CONTEXT_ITEM = "New Project ..";

  private static final String OPEN_PROJECT_CONTEXT_ITEM = "Open Project ..";

  private static final String REFRESH_CONTEXT_ITEM = "Refresh";

  public static String ROOT_LABEL = PROJECTS;

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxLinkMatcherRegistry linkMatcherRegistry;

  private final DepanFxNewResourceRegistry newResourceRegistry;

  private final DepanFxSceneController scene;

  private TreeView<DepanFxWorkspaceMember> workspaceView;

  public DepanFxProjectListViewer(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxLinkMatcherRegistry linkMatcherRegistry,
      DepanFxNewResourceRegistry newResourceRegistry,
      DepanFxSceneController scene) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
    this.linkMatcherRegistry = linkMatcherRegistry;
    this.newResourceRegistry = newResourceRegistry;
    this.scene = scene;

    workspaceView = createView();
  }

  public void newProject() {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    directoryChooser.setTitle(NEW_PROJECT_CONTEXT_ITEM);
    File selectedDirectory = directoryChooser.showDialog(null);

    if (selectedDirectory != null) {
      String projectName = selectedDirectory.getName();
      Path projectPath = selectedDirectory.toPath();
      DepanFxProjects.createProjectStructure(projectName, projectPath);
      DepanFxProjectTree project =
          DepanFxWorkspaceFactory.createDepanFxProjectTree(
              projectName, projectPath);
      workspace.addProject(project);

      DepanFxProjectTreeItem projectItem = new DepanFxProjectTreeItem(project);
      workspaceView.getRoot().getChildren().add(projectItem);
    }
  }

  public void openProject() {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    directoryChooser.setTitle(OPEN_PROJECT_CONTEXT_ITEM);
    File selectedDirectory = directoryChooser.showDialog(null);

    if (selectedDirectory != null) {
      String projectName = selectedDirectory.getName();
      Path projectPath = selectedDirectory.toPath();
      DepanFxProjectTree project =
          DepanFxWorkspaceFactory.createDepanFxProjectTree(
              projectName, projectPath);
      workspace.addProject(project);

      DepanFxProjectTreeItem projectItem = new DepanFxProjectTreeItem(project);
      workspaceView.getRoot().getChildren().add(projectItem);
    }
  }

  public void resetView() {
    workspaceView.setRoot(buildWorkspaceRoot());
  }

  public Tab createWorkspaceTab(String tabName) {
    Tab result = new Tab(tabName, workspaceView);
    DepanFxContextMenuBuilder menuBuilder = new DepanFxContextMenuBuilder();

    menuBuilder.appendActionItem(
        NEW_PROJECT_CONTEXT_ITEM, e -> newProject());
    menuBuilder.appendActionItem(
        OPEN_PROJECT_CONTEXT_ITEM, e -> openProject());

    menuBuilder.appendSeparator();
    menuBuilder.appendActionItem(
        REFRESH_CONTEXT_ITEM, e -> resetView());

    result.setContextMenu(menuBuilder.build());
    return result;
  }

  private TreeView<DepanFxWorkspaceMember> createView() {
    TreeView<DepanFxWorkspaceMember> result =
        new TreeView<>(buildWorkspaceRoot());
    result.setShowRoot(false);
    result.setCellFactory(new WorkspaceCellFactory());
    return result;
  }

  private TreeItem<DepanFxWorkspaceMember> buildWorkspaceRoot() {
    TreeItem<DepanFxWorkspaceMember> result =
        new DepanFxWorkspaceItem(workspace);

    // Since the root is not shown, the root should be expanded so it's
    // children are shown.
    result.setExpanded(true);

    // Populate with current projects.
    for (DepanFxProjectTree project : workspace.getProjectList()) {
      DepanFxProjectTreeItem projectItem = new DepanFxProjectTreeItem(project);
      result.getChildren().add(projectItem);
    }
    return result;
  }

  private class WorkspaceCellFactory
      implements Callback<TreeView<DepanFxWorkspaceMember>, TreeCell<DepanFxWorkspaceMember>> {

    @Override
    public TreeCell<DepanFxWorkspaceMember> call(TreeView<DepanFxWorkspaceMember> param) {

      return new DepanFxProjectListCell(
        workspace, dialogRunner, linkMatcherRegistry, newResourceRegistry, scene);
    }
  }
}
