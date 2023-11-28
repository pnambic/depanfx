package com.pnambic.depanfx.workspace.gui;

import com.pnambic.depanfx.git.gui.DepanFxGitRepoToolDialog;
import com.pnambic.depanfx.git.gui.DepanFxGitRepoToolDialogs;
import com.pnambic.depanfx.git.tooldata.DepanFxGitRepoData;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListSection;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListSections;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListViewer;
import com.pnambic.depanfx.nodelist.gui.DepanFxTreeSectionToolDialog;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxTreeSectionConfiguration;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxTreeSectionData;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneController;
import com.pnambic.depanfx.scene.plugins.DepanFxNewResourceRegistry;
import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Window;

/**
 * 'cuz {@code DepanFxWorkspace} doesn't know anything about menus.
 */
public class DepanFxProjectListCell extends TreeCell<DepanFxWorkspaceMember> {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxProjectListCell.class);

  // Menu texts
  private static final String NEW_GRAPH_MENU = "New Graph";

  private static final String NEW_THEORY_MENU = "New Theory";

  private static final String OPEN_AS_LIST = "Open as ListView";

  private static final String DELETE_DOCUMENT = "Delete Document";

  private static final String DELETE_CONTAINER = "Delete Container";

  private static final String SET_AS_CURRENT_PROJECT = "Set As Current Project";

  private static final String EDIT_GIT_REPO = "Edit git Repo Data...";

  private static final String NEW_GIT_REPO = "New git Repo Data...";

  private static final String EDIT_TREE_SECTION_DATA = "Edit Tree Section Data...";

  private static final String NEW_TREE_SECTION_DATA = "New Tree Section Data...";

  private static final String NEW_TREE_SECTION_NAME = "New Tree Section";

  private static final String NEW_TREE_SECTION_DESCR = "New tree section.";

  // Our state
  private static final char EXTENSION_DOT = '.';

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxNewResourceRegistry newResourceRegistry;

  private final DepanFxTreeSectionConfiguration treeSectionConfig;

  private final DepanFxSceneController scene;

  // Manage Font tweeks (e.g. embolden).
  private Font previousFont;

  public DepanFxProjectListCell(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxNewResourceRegistry newResourceRegistry,
      DepanFxTreeSectionConfiguration treeSectionConfig,
      DepanFxSceneController scene) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
    this.newResourceRegistry = newResourceRegistry;
    this.treeSectionConfig = treeSectionConfig;
    this.scene = scene;
  }

  @Override
  protected void updateItem(DepanFxWorkspaceMember member, boolean empty) {
    super.updateItem(member, empty);
    restoreFont();

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

    if (member instanceof DepanFxProjectContainer) {
      String name = ((DepanFxProjectContainer) member).getMemberName();
      switch (name) {
        case DepanFxProjects.GRAPHS_CONTAINER:
          builder.appendSubMenu(graphsContextMenu());
          break;

        case DepanFxProjects.ANALYSES_CONTAINER:
          builder.appendSubMenu(analysisContextMenu());
          break;
        case DepanFxGitRepoToolDialogs.GIT_REPOS_TOOL_DIR:
          builder.appendActionItem(NEW_GIT_REPO,
              e -> runNewGitRepoAction());
          break;
        case DepanFxTreeSectionData.TREE_SECTIONS_TOOL_DIR:
          builder.appendActionItem(NEW_TREE_SECTION_DATA,
              e -> runNewTreeSectionDataAction());
          break;
      }
      if (member instanceof DepanFxProjectTree) {
        appendProjectContextMenu(builder, (DepanFxProjectTree) member);
      }
    }
    if (member instanceof DepanFxProjectDocument) {
      DepanFxProjectDocument document = (DepanFxProjectDocument) member;
      Path docPath = document.getMemberPath();
      Optional<String> optExt = getExtension(docPath.getFileName().toString());
      if (optExt.isPresent()) {
        switch (optExt.get()) {
          case "dgi":
            setOnMouseClicked(
                e -> handleMouseClick(e, docPath, this::runOpenAsListAction));
            builder.appendActionItem(
                OPEN_AS_LIST,
                e -> runOpenAsListAction(docPath));
            break;
          case "dnli":
            setOnMouseClicked(
                e -> handleMouseClick(e, docPath,
                    this::runOpenNodeListAction));
            builder.appendActionItem(
                OPEN_AS_LIST,
                e -> runOpenNodeListAction(docPath));
            break;
          case DepanFxGitRepoData.GIT_REPO_TOOL_EXT:
            setOnMouseClicked(
                e -> handleMouseClick(e, docPath,
                    this::runEditGitRepoAction));
            builder.appendActionItem(
                EDIT_GIT_REPO,
                e -> runEditGitRepoAction(docPath));
            break;
          case DepanFxTreeSectionData.TREE_SECTION_TOOL_EXT:
            setOnMouseClicked(
                e -> handleMouseClick(e, docPath,
                    this::runEditTreeSectionDataAction));
            builder.appendActionItem(
                EDIT_TREE_SECTION_DATA,
                e -> runEditTreeSectionDataAction(docPath));
            break;
        }
      }
      // Don't offer delete for documents in the built-in project.
      if (!document.getProject().equals(workspace.getBuiltInProjectTree())) {
        appendDeleteDocument(builder, document);
      }
    }

    return builder.build();
  }

  private void handleMouseClick(
      MouseEvent event, Path path, Consumer<Path> onDoubleClick) {
    if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
      onDoubleClick.accept(path);
    }
  }

  private Menu graphsContextMenu() {
    Menu newGraphMenu = new Menu(NEW_GRAPH_MENU);
    newGraphMenu.getItems().addAll(newResourceRegistry.buildNewResourceItems());
    return newGraphMenu;
  }

  private Menu analysisContextMenu() {
    Menu newGraphMenu = new Menu(NEW_THEORY_MENU);
    newGraphMenu.getItems().addAll(newResourceRegistry.buildNewAnalysisItems());
    return newGraphMenu;
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
    builder.appendActionItem(DELETE_DOCUMENT,
        e -> projDoc.getProject().deleteDocument(projDoc));
  }

  private void runOpenAsListAction(Path graphDocPath) {
    try {
      Optional<DepanFxProjectDocument> optProjDoc =
          workspace.toProjectDocument(graphDocPath.toUri());
      Optional<DepanFxWorkspaceResource> optWkspRsrc =
          optProjDoc.flatMap(workspace::getWorkspaceResource);
      Optional<DepanFxNodeList> optNodeList =
          optWkspRsrc.map(DepanFxNodeLists::buildNodeList);
      optNodeList.ifPresent(nl -> {
        String title = getGraphTabTitle(optWkspRsrc.get().getDocument());
        addNodeListViewToScene(nl, title);
      });
    } catch (RuntimeException errCaught) {
      LOG.error("Unable to open list view for {}",
          graphDocPath.toUri(), errCaught);
    }
  }

  private void runOpenNodeListAction(Path graphDocPath) {
    try {
      Optional<DepanFxProjectDocument> optProjDoc =
          workspace.toProjectDocument(graphDocPath.toUri());
      Optional<DepanFxWorkspaceResource> optWkspRsrc =
          optProjDoc.flatMap(workspace::getWorkspaceResource);
      Optional<DepanFxNodeList> optNodeList =
          optWkspRsrc.map(r -> (DepanFxNodeList) r.getResource());
      optNodeList.ifPresent(nl -> {
            String title = buildNodeListTabTitle(optWkspRsrc.get().getDocument());
            addNodeListViewToScene(nl, title);
          });
    } catch (RuntimeException errCaught) {
      LOG.error("Unable to open list view for {}",
          graphDocPath.toUri(), errCaught);
    }
  }

  private void runNewGitRepoAction() {

    DepanFxGitRepoToolDialog.Aware srcDlg =
        new DepanFxGitRepoToolDialog.Aware() {

          @Override
          public Window getChooserWindow() {
            return DepanFxProjectListCell.this.getScene().getWindow();
          }

          @Override
          public DepanFxGitRepoData getTooldata() {
            return DepanFxGitRepoToolDialogs.buildInitialGitRepoData();
          }

          @Override
          public void setTooldata(DepanFxGitRepoData repoData) {
            // Not used.
          }
        };
    DepanFxGitRepoToolDialogs.runGitRepoCreate(srcDlg , dialogRunner);
  }

  private void runEditGitRepoAction(Path gitRepoPath) {
    try {
      Optional<DepanFxProjectDocument> optProjDoc =
          workspace.toProjectDocument(gitRepoPath.toUri());

      optProjDoc
          .flatMap(workspace::getWorkspaceResource)
          .ifPresent(
                r -> DepanFxGitRepoToolDialogs.runGitRepoEdit(r, dialogRunner));
    } catch (RuntimeException errCaught) {
      LOG.error("Unable to open git repository data {} for edit",
          gitRepoPath.toUri(), errCaught);
    }
  }

  private void runNewTreeSectionDataAction() {
    try {
      DepanFxTreeSectionData sectionData =
          treeSectionConfig.buildInitialTreeSection(
              NEW_TREE_SECTION_NAME, NEW_TREE_SECTION_DESCR);
      DepanFxTreeSectionToolDialog.runCreateDialog(
          sectionData, dialogRunner, NEW_TREE_SECTION_DATA);
    } catch (RuntimeException errCaught) {
      LOG.error("Unable to create tree section data", errCaught);
    }
  }

  private void runEditTreeSectionDataAction(Path docPath) {
    try {
      workspace.toProjectDocument(docPath.toUri())
          .flatMap(workspace::getWorkspaceResource)
          .ifPresent(r -> DepanFxTreeSectionToolDialog.runEditDialog(
              r, dialogRunner, EDIT_TREE_SECTION_DATA));
    } catch (RuntimeException errCaught) {
      LOG.error("Unable to open git repository data {} for edit",
          docPath, errCaught);
    }
  }

  private void addNodeListViewToScene(
      DepanFxNodeList nodeList, String tabTitle) {
    List<DepanFxNodeListSection> sections =
        DepanFxNodeListSections.getFinalSection();
    DepanFxNodeListViewer viewer =
        new DepanFxNodeListViewer(
            workspace, dialogRunner, nodeList, sections);

    viewer.prependMemberTree();
    Tab viewerTab = viewer.createWorkspaceTab(tabTitle);
    scene.addTab(viewerTab);
  }

  private boolean isCurrentProject(DepanFxWorkspaceMember member) {
    if (member instanceof DepanFxProjectTree) {
      return workspace.getCurrentProject().filter(member::equals).isPresent();
    }
    return false;
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
      int dotIndex = filename.lastIndexOf(EXTENSION_DOT);
      if (dotIndex > 0 && dotIndex < filename.length() - 1) {
          return Optional.of(filename.substring(dotIndex + 1));
      }
      return Optional.empty();
  }
}
