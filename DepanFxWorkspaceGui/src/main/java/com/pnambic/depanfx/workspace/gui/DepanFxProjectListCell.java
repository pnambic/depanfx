package com.pnambic.depanfx.workspace.gui;

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListSection;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListSections;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListViewer;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherRegistry;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneController;
import com.pnambic.depanfx.scene.plugins.DepanFxNewResourceRegistry;
import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
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

  private final DepanFxLinkMatcherRegistry linkMatcherRegistry;

  private final DepanFxNewResourceRegistry newResourceRegistry;

  private final DepanFxSceneController scene;

  public DepanFxProjectListCell(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxLinkMatcherRegistry linkMatcherRegistry,
      DepanFxNewResourceRegistry newResourceRegistry,
      DepanFxSceneController scene) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
    this.newResourceRegistry = newResourceRegistry;
    this.linkMatcherRegistry = linkMatcherRegistry;
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
            setContextMenu(graphDocContextMenu(document));
            break;
          case "dnli":
            setContextMenu(nodeListContextMenu(document));
            break;
        }
      }
      return;
    }
  }

  private ContextMenu graphsContextMenu() {
    Menu newGraphMenu = new Menu(NEW_GRAPH_MENU);
    newGraphMenu.getItems().addAll(newResourceRegistry.buildNewResourceItems());

    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendSubMenu(newGraphMenu);
    return builder.build();
  }

  private ContextMenu graphDocContextMenu(DepanFxProjectDocument document) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem(
        OPEN_AS_LIST,
        e -> runOpenAsListAction(document.getMemberPath().toUri()));
    return builder.build();
  }

  private ContextMenu nodeListContextMenu(DepanFxProjectDocument document) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem(
        OPEN_AS_LIST,
        e -> runOpenNodeListAction(document.getMemberPath().toUri()));
    return builder.build();
  }

  private void runOpenAsListAction(URI graphDocUri) {
    try {
      Optional<DepanFxProjectDocument> optProjDoc =
          workspace.toProjectDocument(graphDocUri);
      Optional<DepanFxWorkspaceResource> optWkspRsrc =
          optProjDoc.flatMap(workspace::getWorkspaceResource);
      Optional<DepanFxNodeList> optNodeList =
          optWkspRsrc.map(DepanFxNodeLists::buildNodeList);
      optNodeList.ifPresent(nl -> {
        String title = getGraphTabTitle(optWkspRsrc.get().getDocument());
        addNodeListViewToScene(nl, title);
      });
    } catch (RuntimeException errCaught) {
      LOG.error("Unable to open list view for {}", graphDocUri, errCaught);
    }
  }

  private void runOpenNodeListAction(URI nodeListUri) {
    try {
      Optional<DepanFxProjectDocument> optProjDoc =
          workspace.toProjectDocument(nodeListUri);
      Optional<DepanFxWorkspaceResource> optWkspRsrc =
          optProjDoc.flatMap(workspace::getWorkspaceResource);
      Optional<DepanFxNodeList> optNodeList =
          optWkspRsrc.map(r -> (DepanFxNodeList) r.getResource());
      optNodeList.ifPresent(nl -> {
            String title = buildNodeListTabTitle(optWkspRsrc.get().getDocument());
            addNodeListViewToScene(nl, title);
          });
    } catch (RuntimeException errCaught) {
      LOG.error("Unable to open list view for {}", nodeListUri, errCaught);
    }
  }

  private void addNodeListViewToScene(
      DepanFxNodeList nodeList, String tabTitle) {
    List<DepanFxNodeListSection> sections =
        DepanFxNodeListSections.getFinalSection();
    DepanFxNodeListViewer viewer =
        new DepanFxNodeListViewer(
            dialogRunner, linkMatcherRegistry, nodeList, sections);

    viewer.prependMemberTree();
    Tab viewerTab = viewer.createWorkspaceTab(tabTitle);
    scene.addTab(viewerTab);
  }

  private static String buildNodeListTabTitle(DepanFxProjectDocument projDoc) {
    String fullName = projDoc.getMemberPath().getFileName().toString();
    return getExtension(fullName)
        .map(e -> chopExtension(fullName, e.length() + 1))
        .orElse(fullName);
  }

  private static String getGraphTabTitle(DepanFxProjectDocument projDoc) {
    return buildNodeListTabTitle(projDoc) + " nodes";
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
