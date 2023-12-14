package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxFlatSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxTreeSectionData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.io.File;
import java.util.Collection;

import javafx.beans.value.ObservableValue;
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

  private static final String SELECT_TREE_SECTION = "Select Tree Section...";

  private static final String EDIT_TREE_SECTION = "Edit Tree Section...";

  private static final String INSERT_ABOVE_MEMBER_TREE_SECTION =
      "Insert Member Tree Section";

  private static final String SELECT_RECURSIVE = "Select Recursive";

  private static final String CLEAR_RECURSIVE = "Clear Recursive";

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
    return builder.build();
  }

  private void openTreeSectionEditor(DepanFxTreeSection member) {
    Dialog<DepanFxTreeSectionToolDialog> treeSectionEditor =
        listViewer.buildDialog(DepanFxTreeSectionToolDialog.class);

    treeSectionEditor.getController().setTooldata(member.getSectionData());
    treeSectionEditor.runDialog("Update Tree Section");
    treeSectionEditor.getController().getWorkspaceResource()
        .ifPresent(d -> updateSectionDataRsrc(member, d));
  }

  private void openTreeSectionFinder(DepanFxTreeSection member) {
    DepanFxWorkspace workspace = listViewer.getWorkspace();
    FileChooser fileChooser = prepareTreeSectionFinder(workspace);
    File selectedFile =
        fileChooser.showOpenDialog(getScene().getWindow());
    if (selectedFile != null) {
       workspace
          .toProjectDocument(selectedFile.getAbsoluteFile().toURI())
          .flatMap(p -> DepanFxWorkspaceFactory.loadDocument(
              workspace, p, DepanFxTreeSectionData.class))
          .ifPresent(d -> updateSectionDataRsrc(member, d));
    }
  }

  private void openFlatSectionEditor(DepanFxFlatSection member) {
    Dialog<DepanFxFlatSectionToolDialog> flatSectionEditor =
        DepanFxFlatSectionToolDialog.runEditDialog(
            member.getSectionDataRsrc(), listViewer.getDialogRunner(),
            "Update Tree Section");

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
          .flatMap(p -> DepanFxWorkspaceFactory.loadDocument(
              workspace, p, DepanFxFlatSectionData.class))
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
