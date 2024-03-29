package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxFlatSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxTreeSectionData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class DepanFxNodeListCell
    extends CheckBoxTreeTableCell<DepanFxNodeListMember, DepanFxNodeListMember> {

  private static final String SELECT_FLAT_SECTION = "Select Flat Section...";

  private static final String EDIT_FLAT_SECTION = "Edit Flat Section...";

  // Tree section actions
  private static final String SELECT_TREE_SECTION = "Select Tree Section...";

  private static final String EDIT_TREE_SECTION = "Edit Tree Section...";

  private static final String EXPORT_TO_CSV = "Export to CSV...";

  private static final String INSERT_ABOVE_MEMBER_TREE_SECTION =
      "Insert Member Tree Section";

  // Fork/Directory actions
  private static final String SELECT_RECURSIVE = "Select Recursive";

  private static final String CLEAR_RECURSIVE = "Clear Recursive";

  private static final String EXPAND_CHILDREN = "Expand Children";

  // Allow cells to act on viewer (e.g. change sections, etc.)
  private final DepanFxNodeListViewer listViewer;

  public DepanFxNodeListCell(DepanFxNodeListViewer listViewer) {
    this.listViewer = listViewer;
    setConverter(new NameConverter());
    setSelectedStateCallback(new SelectionState());
  }

  @Override
  public void updateItem(DepanFxNodeListMember member, boolean empty) {
    super.updateItem(member, empty);

    // Visual space reserved for future use.
    if (empty) {
      return;
    }
    // The normal case.
    if (member != null) {
      stylizeCell(member);
      return;
    }
    // Something unexpected.
    setText("<null>");
    setGraphic(null);
  }

  private void stylizeCell(DepanFxNodeListMember member) {

    if (member instanceof DepanFxFlatSection) {
      setContextMenu(nodeListSectionMenu((DepanFxFlatSection) member));
      return;
    }

    if (member instanceof DepanFxTreeFork) {
      setContextMenu(treeForkMenu((DepanFxTreeFork) member));
      return;
    }

    if (member instanceof DepanFxTreeSection) {
      setContextMenu(treeSectionMenu((DepanFxTreeSection) member));
      return;
    }

    // Otherwise clear the context menu
    setContextMenu(null);
  }

  private ContextMenu nodeListSectionMenu(DepanFxFlatSection member) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem(EDIT_FLAT_SECTION,
        e -> openFlatSectionEditor(member));
    builder.appendActionItem(SELECT_FLAT_SECTION,
        e -> openFlatSectionFinder(member));

    builder.appendSeparator();
    builder.appendActionItem(
        EXPORT_TO_CSV,
        e -> runExportToCsvAction(member));

    builder.appendSeparator();
    builder.appendActionItem(
        INSERT_ABOVE_MEMBER_TREE_SECTION,
        e -> runInsertMemberTreeSectionAction(member));
    return builder.build();
  }

  private ContextMenu treeSectionMenu(DepanFxTreeSection member) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem(EDIT_TREE_SECTION,
        e -> openTreeSectionEditor(member));
    builder.appendActionItem(SELECT_TREE_SECTION,
        e -> openTreeSectionFinder(member));

    builder.appendSeparator();
    builder.appendActionItem(
        EXPORT_TO_CSV,
        e -> runExportToCsvAction(member));
    return builder.build();
  }

  private ContextMenu treeForkMenu(DepanFxTreeFork fork) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem(
        SELECT_RECURSIVE,
        e -> runSelectRecursiveAction(fork, true));
    builder.appendActionItem(
        CLEAR_RECURSIVE,
        e -> runSelectRecursiveAction(fork, false));
    builder.appendSeparator();
    builder.appendActionItem(
        EXPAND_CHILDREN,
        e -> runExpandChildrenAction());
    return builder.build();
  }

  private void runExpandChildrenAction() {
    TreeItem<DepanFxNodeListMember> tree = getTableRow().getTreeItem();
    BreadthExpander expander = new BreadthExpander(100);
    expander.addBreadthItems(tree.getChildren());
    expander.expandChildren();
    tree.setExpanded(true);
  }

  private void runExportToCsvAction(DepanFxFlatSection flatSection) {
    DepanFxExportFlatSectionDialog.runExportDialog(flatSection, listViewer);
  }

  private void runExportToCsvAction(DepanFxTreeSection treeSection) {
    DepanFxExportTreeSectionDialog.runExportDialog(
        treeSection, listViewer.getDialogRunner());
  }

  private void openTreeSectionEditor(DepanFxTreeSection member) {
    Dialog<DepanFxTreeSectionToolDialog> treeSectionEditor =
        DepanFxTreeSectionToolDialog.runEditDialog(
            member.getProjDoc(), member.getSectionData(),
            listViewer.getDialogRunner());
    treeSectionEditor.getController().getWorkspaceResource()
        .ifPresent(d -> updateSectionDataRsrc(member, d));
  }

  private void openTreeSectionFinder(DepanFxTreeSection member) {
    DepanFxWorkspace workspace = listViewer.getWorkspace();
    FileChooser fileChooser = prepareTreeSectionFinder(workspace);
    File selectedFile =
        fileChooser.showOpenDialog(getScene().getWindow());
    if (selectedFile != null) {
       workspace.toProjectDocument(selectedFile.getAbsoluteFile().toURI())
          .flatMap(p -> workspace.getWorkspaceResource(
              p, DepanFxTreeSectionData.class))
          .ifPresent(d -> updateSectionDataRsrc(member, d));
    }
  }

  private void openFlatSectionEditor(DepanFxFlatSection member) {
    DepanFxFlatSectionData sectionData = member.getSectionData();
    Dialog<DepanFxFlatSectionToolDialog> flatSectionEditor =
        DepanFxFlatSectionToolDialog.runEditDialog(
            member.getSectionDataRsrc().getDocument(), sectionData,
            listViewer.getDialogRunner());

    flatSectionEditor.getController().getWorkspaceResource()
        .ifPresent(d -> updateSectionDataRsrc(member, d));
  }

  private void openFlatSectionFinder(DepanFxFlatSection member) {
    DepanFxWorkspace workspace = listViewer.getWorkspace();
    FileChooser fileChooser = prepareFlatSectionFinder(workspace);
    File selectedFile =
        fileChooser.showOpenDialog(getScene().getWindow());
    if (selectedFile != null) {
       workspace
          .toProjectDocument(selectedFile.getAbsoluteFile().toURI())
          .flatMap(p -> workspace.getWorkspaceResource(
              p, DepanFxFlatSectionData.class))
          .ifPresent(d -> updateSectionDataRsrc(member, d));
    }
  }

  private void updateSectionDataRsrc(
      DepanFxFlatSection member, DepanFxWorkspaceResource dataRsrc) {
    member.setSectionDataRsrc(dataRsrc);
    listViewer.resetView();
  }

  private void updateSectionDataRsrc(
      DepanFxTreeSection member, DepanFxWorkspaceResource dataRsrc) {
    member.setSectionDataRsrc(dataRsrc);
    listViewer.resetView();
  }

  private void runInsertMemberTreeSectionAction(DepanFxNodeListSection before) {
    listViewer.insertMemberTreeSection(before);
  }

  private void runSelectRecursiveAction(DepanFxTreeFork fork, boolean value) {
    // Do the root
    GraphNode selectNode = fork.getGraphNode();
    listViewer.doSelectGraphNodeAction(selectNode, value);

    // Then all the reachable nodes.
    Collection<GraphNode> nodes = fork.getDecendants();
    listViewer.doSelectGraphNodesAction(nodes, value);
  }

  private FileChooser prepareFlatSectionFinder(DepanFxWorkspace workspace) {
    FileChooser result = DepanFxResourcePerspectives.prepareToolFinder(
        workspace, DepanFxNodeListSectionData.SECTIONS_TOOL_PATH);
    DepanFxFlatSectionToolDialog.setFlatSectionTooldataFilters(result);
    return result;
  }

  private FileChooser prepareTreeSectionFinder(DepanFxWorkspace workspace) {
    FileChooser result = DepanFxResourcePerspectives.prepareToolFinder(
        workspace, DepanFxNodeListSectionData.SECTIONS_TOOL_PATH);
    DepanFxTreeSectionToolDialog.setTreeSectionTooldataFilters(result);
    return result;
  }

  private static class BreadthExpander {

    private int expandLimit;

    private List<TreeItem<DepanFxNodeListMember>> breadthItems =
        new ArrayList<>();

    public BreadthExpander(int expandLimit) {
      this.expandLimit = expandLimit;
    }

    public void addBreadthItems(
        List<TreeItem<DepanFxNodeListMember>> moreItems) {
      breadthItems.addAll(moreItems);
    }

    public void expandChildren() {
      for (int next = 0; next < breadthItems.size(); next++) {
        if (expandLimit <= 0) {
          return;
        }
        TreeItem<DepanFxNodeListMember> currItem = breadthItems.get(next);
        ObservableList<TreeItem<DepanFxNodeListMember>> children =
            currItem.getChildren();
        addBreadthItems(children);
        if (!currItem.isExpanded()) {
          expandLimit -= children.size();
          currItem.setExpanded(true);
        }
      }
    }
  }

  private class SelectionState
      implements Callback<Integer, ObservableValue<Boolean>> {

    @Override
    public ObservableValue<Boolean> call(Integer param) {
      TreeItem<DepanFxNodeListMember> item =
          listViewer.getTreeItem(param.intValue());
      return listViewer.getCheckBoxObservable(item.getValue());
    }
  }

  private static class NameConverter
      extends StringConverter<DepanFxNodeListMember> {

    @Override
    public String toString(DepanFxNodeListMember member) {
      if (member != null) {
        return member.getDisplayName();
      }
      return "<empty>";
    }

    @Override
    public DepanFxNodeListMember fromString(String string) {
      throw new UnsupportedOperationException();
    }
  }
}
