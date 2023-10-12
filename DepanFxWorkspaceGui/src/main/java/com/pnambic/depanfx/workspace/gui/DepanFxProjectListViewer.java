package com.pnambic.depanfx.workspace.gui;

import java.io.File;
import java.nio.file.Path;

import com.pnambic.depanfx.scene.plugins.DepanFxNewResourceRegistry;
import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
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

  private final DepanFxNewResourceRegistry newResourceRegistry;

  private final DepanFxWorkspace workspace;

  private TreeView<DepanFxWorkspaceMember> workspaceView;

  public DepanFxProjectListViewer(
      DepanFxWorkspace workspace,
      DepanFxNewResourceRegistry newResourceRegistry) {
    this.workspace = workspace;
    this.newResourceRegistry = newResourceRegistry;
    this.workspaceView = createView(workspace);
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
          DepanFxWorkspaceFactory.createDepanFxProjectTree(projectName, projectPath);
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
          DepanFxWorkspaceFactory.createDepanFxProjectTree(projectName, projectPath);
      workspace.addProject(project);

      DepanFxProjectTreeItem projectItem = new DepanFxProjectTreeItem(project);
      workspaceView.getRoot().getChildren().add(projectItem);
    }
  }

  public void refreshView() {
    workspaceView.refresh();
  }

  public Tab createWorkspaceTab(String tabName) {
    Tab result = new Tab(tabName, workspaceView);
    ContextMenu contextMenu = new ContextMenu();
    ObservableList<MenuItem> contextList = contextMenu.getItems();
    contextList.add(createNewProjectItem());
    contextList.add(createOpenProjectItem());
    contextList.add(new SeparatorMenuItem());
    contextList.add(createRefreshItem());
    result.setContextMenu(contextMenu);
    return result;
  }

  private TreeView<DepanFxWorkspaceMember> createView(DepanFxWorkspace workspace) {
    TreeItem<DepanFxWorkspaceMember> treeRoot = new DepanFxWorkspaceItem(workspace);

    TreeView<DepanFxWorkspaceMember> result = new TreeView<>(treeRoot);
    result.setShowRoot(false);
    result.setCellFactory(new WorkspaceCellFactory());
    return result;
  }

  private MenuItem createNewProjectItem() {
    MenuItem result = new MenuItem(NEW_PROJECT_CONTEXT_ITEM);

    result.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {
        newProject();
      }
    });
    return result;
  }

  private MenuItem createOpenProjectItem() {
    MenuItem result = new MenuItem(OPEN_PROJECT_CONTEXT_ITEM);

    result.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {
        openProject();
      }
    });
    return result;
  }

  private MenuItem createRefreshItem() {
    MenuItem result = new MenuItem(REFRESH_CONTEXT_ITEM);

    result.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {
        refreshView();
      }
    });
    return result;
  }

  private class WorkspaceCellFactory
      implements Callback<TreeView<DepanFxWorkspaceMember>, TreeCell<DepanFxWorkspaceMember>> {

    @Override
    public TreeCell<DepanFxWorkspaceMember> call(TreeView<DepanFxWorkspaceMember> param) {

      return new WorkspaceCell();
    }
  }

  private class WorkspaceCell extends TreeCell<DepanFxWorkspaceMember> {

    private static final String NEW_GRAPH_MENU = "New Graph";

    @Override
    protected void updateItem(DepanFxWorkspaceMember member, boolean empty) {
      super.updateItem(member, empty);

      // Visual space reserved for future use.
      if (empty) {
        setText(null);
        setGraphic(null);
        return;
      }
      // The normal case.
      if (member != null) {
        setText(member.getMemberName());
        setGraphic(null);
        stylizeCell(member);
        return;
      }
      // Something unexpected.
      setText("<null>");
      setGraphic(null);
    }

    private void stylizeCell(DepanFxWorkspaceMember member) {
      if (member instanceof DepanFxProjectContainer) {
        String name = ((DepanFxProjectContainer) member).getMemberName();
        switch (name) {
          case DepanFxProjects.GRAPHS_CONTAINER:
            setContextMenu(graphsContextMenu());
            break;
        }
      }
    }

    private ContextMenu graphsContextMenu() {
      Menu newGraphMenu = new Menu(NEW_GRAPH_MENU);
      newGraphMenu.getItems().addAll(newResourceRegistry.buildNewResourceItems());

      ContextMenu result = new ContextMenu();
      result.getItems().add(newGraphMenu);
      return result;
    }
  }
}
