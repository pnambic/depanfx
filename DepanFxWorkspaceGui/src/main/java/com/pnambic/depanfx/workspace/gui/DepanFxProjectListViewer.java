package com.pnambic.depanfx.workspace.gui;

import com.pnambic.depanfx.perspective.plugins.DepanFxResourceMenuRegistry;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceRegistry;
import com.pnambic.depanfx.perspective.workspace.controls.DepanFxProjectTreeCell;
import com.pnambic.depanfx.perspective.workspace.controls.DepanFxWorkspaceItem;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxSceneService;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;
import com.pnambic.depanfx.workspace.projects.DepanFxFileSystemProject;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;

public class DepanFxProjectListViewer {

  private static final String PROJECTS = "Projects";

  private static final String OPEN_PROJECT_CONTEXT_ITEM = "Open Project...";

  private static final String NEW_PROJECT_CONTEXT_ITEM = "New Project...";

  private static final String REFRESH_CONTEXT_ITEM = "Refresh";

  public static String ROOT_LABEL = PROJECTS;

  private final DepanFxWorkspace workspace;

  private final DepanFxSceneService sceneSrvc;

  private final DepanFxResourceRegistry rsrcRegistry;

  private final DepanFxResourceMenuRegistry rsrcMenuRegistry;

  private TreeView<DepanFxWorkspaceMember> workspaceView;

  public DepanFxProjectListViewer(
      DepanFxWorkspace workspace,
      DepanFxSceneService sceneSrvc,
      DepanFxResourceRegistry rsrcRegistry,
      DepanFxResourceMenuRegistry rsrcMenuRegistry) {
    this.workspace = workspace;
    this.sceneSrvc = sceneSrvc;
    this.rsrcRegistry = rsrcRegistry;
    this.rsrcMenuRegistry = rsrcMenuRegistry;

    workspaceView = createView();
  }

  public void newProject() {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    directoryChooser.setTitle(NEW_PROJECT_CONTEXT_ITEM);
    File selectedDirectory = directoryChooser.showDialog(null);

    if (selectedDirectory != null) {
      String projectName = selectedDirectory.getName();
      Path projectPath = selectedDirectory.toPath();
      DepanFxFileSystemProject projectSpi =
          new DepanFxFileSystemProject(projectName, projectPath);
      DepanFxProjectTree projectTree =
          DepanFxWorkspaceFactory.createDepanFxProjectTree(projectSpi);
      DepanFxProjects.createProjectStructure(projectTree, projectSpi);
      workspace.addProject(projectTree);
      workspace.setCurrentProject(projectTree);
    }
  }

  public void openProject() {
    DepanFxProjectChooser.runProjectFinder()
      .ifPresent(p -> {
          workspace.addProject(p);
          workspace.setCurrentProject(p);
      });
  }

  public void resetView() {
    workspaceView.setRoot(buildWorkspaceRoot());
  }

  public Tab createWorkspaceTab(String tabName) {
    Tab result = new Tab(tabName, workspaceView);
    result.setContextMenu(buildWorkspaceMenu());
    return result;
  }

  public Optional<DepanFxWorkspaceMember> getCurrentSelection() {
    TreeItem<DepanFxWorkspaceMember> selection =
        workspaceView.getSelectionModel().getSelectedItem();
    if (selection != null) {
      return Optional.of(selection.getValue());
    }
    return Optional.empty();
  }

  private TreeView<DepanFxWorkspaceMember> createView() {
    TreeView<DepanFxWorkspaceMember> result =
        new TreeView<>(buildWorkspaceRoot());
    result.setShowRoot(false);
    result.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    result.setCellFactory(
        p -> new DepanFxProjectTreeCell(
            workspace, sceneSrvc.getDialogRunner(),
            rsrcRegistry, rsrcMenuRegistry));
    result.setContextMenu(buildWorkspaceMenu());
    return result;
  }

  private ContextMenu buildWorkspaceMenu() {
    DepanFxContextMenuBuilder menuBuilder = new DepanFxContextMenuBuilder();

    menuBuilder.appendActionItem(
        OPEN_PROJECT_CONTEXT_ITEM, e -> openProject());
    menuBuilder.appendActionItem(
        NEW_PROJECT_CONTEXT_ITEM, e -> newProject());

    menuBuilder.appendSeparator();
    menuBuilder.appendActionItem(
        REFRESH_CONTEXT_ITEM, e -> resetView());
    return menuBuilder.build();
  }

  private TreeItem<DepanFxWorkspaceMember> buildWorkspaceRoot() {
    TreeItem<DepanFxWorkspaceMember> result =
        new DepanFxWorkspaceItem(workspace, m -> true);
    return result;
  }
}
