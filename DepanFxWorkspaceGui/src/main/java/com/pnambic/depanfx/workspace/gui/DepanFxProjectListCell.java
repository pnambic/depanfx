package com.pnambic.depanfx.workspace.gui;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListSection;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListSections;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListViewer;
import com.pnambic.depanfx.scene.DepanFxSceneController;
import com.pnambic.depanfx.scene.plugins.DepanFxNewResourceRegistry;
import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeCell;

/**
 * 'cuz {@code DepanFxWorkspace} doesn't know anything about menus.
 */
public class DepanFxProjectListCell extends TreeCell<DepanFxWorkspaceMember> {

  private static final String NEW_GRAPH_MENU = "New Graph";

  private static final char EXT_DOT = '.';

  private static final String OPEN_AS_LIST = "Open as ListView";

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxProjectListCell.class);

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxNewResourceRegistry newResourceRegistry;

  private final DepanFxSceneController scene;

  public DepanFxProjectListCell(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxNewResourceRegistry newResourceRegistry,
      DepanFxSceneController scene) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
    this.newResourceRegistry = newResourceRegistry;
    this.scene = scene;
  }

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
      return;
    }
    if (member instanceof DepanFxProjectDocument) {
      DepanFxProjectDocument document = (DepanFxProjectDocument) member;
      Path path = document.getMemberPath();
      Optional<String> optExt = getExtension(path.getFileName().toString());
      if (optExt.isPresent()) {
        switch (optExt.get()) {
          case "dgi":
            setContextMenu(graghDocContextMenu(document));
            break;
        }
      }
      return;
    }
  }

  private ContextMenu graphsContextMenu() {
    Menu newGraphMenu = new Menu(NEW_GRAPH_MENU);
    newGraphMenu.getItems().addAll(newResourceRegistry.buildNewResourceItems());

    ContextMenu result = new ContextMenu();
    result.getItems().add(newGraphMenu);
    return result;
  }

  private ContextMenu graghDocContextMenu(DepanFxProjectDocument document) {
    MenuItem openAsListMenuItem = new MenuItem(OPEN_AS_LIST);
    openAsListMenuItem.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {
        runOpenAsListAction(document.getMemberPath().toUri());
      }
    });

    ContextMenu result = new ContextMenu();
    result.getItems().add(openAsListMenuItem);
    return result;
  }

  private void runOpenAsListAction(URI graphDocUri) {
    try {
      GraphDocument graphDoc = (GraphDocument) workspace.importDocument(graphDocUri);
      DepanFxProjectDocument workspaceDoc = workspace.asProjectDocument(graphDocUri).get();
      DepanFxNodeList nodeList = DepanFxNodeLists.buildNodeList(workspaceDoc, graphDoc);

      List<DepanFxNodeListSection> sections = DepanFxNodeListSections.getFinalSection();
      DepanFxNodeListViewer viewer = new DepanFxNodeListViewer(nodeList, sections);
      String tabTitle = getTabTitle(workspaceDoc);
      Tab viewerTab = viewer.createWorkspaceTab(tabTitle);

      scene.addTab(viewerTab);
    } catch (IOException errIo) {
      LOG.error("Unable to open list view for {}", errIo);
    }
  }

  private static String getTabTitle(DepanFxProjectDocument workspaceDoc) {
    String fullName = workspaceDoc.getMemberPath().getFileName().toString();
    String baseName = getExtension(fullName)
        .map(e -> chopExtension(fullName, e.length() + 1))
        .orElse(fullName);
    return baseName + " nodes";
  }

  private static String chopExtension(String filename, int extSize) {
    int length = filename.length();
    return filename.substring(0, length - extSize);
  }

  private static Optional<String> getExtension(String filename) {
      int dotIndex = filename.lastIndexOf(EXT_DOT);
      if (dotIndex > 0 && dotIndex < filename.length() - 1) {
          return Optional.of(filename.substring(dotIndex + 1));
      }
      return Optional.empty();
  }
}
