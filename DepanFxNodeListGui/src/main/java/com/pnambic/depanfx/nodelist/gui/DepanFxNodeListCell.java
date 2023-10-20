package com.pnambic.depanfx.nodelist.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;

public class DepanFxNodeListCell extends TreeCell<DepanFxNodeListMember> {

  private static final String INSERT_ABOVE_TREE_SECTION = "Insert Tree Section";

  // Allow cells to act on viewer (e.g. change sections, etc.)
  private final DepanFxNodeListViewer listViewer;

  public DepanFxNodeListCell(DepanFxNodeListViewer listViewer) {
    this.listViewer = listViewer;
  }

  @Override
  protected void updateItem(DepanFxNodeListMember member, boolean empty) {
    super.updateItem(member, empty);

    // Visual space reserved for future use.
    if (empty) {
      setText(null);
      setGraphic(null);
      return;
    }
    // The normal case.
    if (member != null) {
      setText(member.getDisplayName());
      setGraphic(null);
      stylizeCell(member);
      return;
    }
    // Something unexpected.
    setText("<null>");
    setGraphic(null);
  }

  private void stylizeCell(DepanFxNodeListMember member) {
    if (member instanceof DepanFxNodeListFlatSection) {
      setContextMenu(nodeListSectionMenu((DepanFxNodeListFlatSection) member));
      return;
    }
  }

  private ContextMenu nodeListSectionMenu(DepanFxNodeListFlatSection member) {
    MenuItem insertTreeSectionMenuItem = new MenuItem(INSERT_ABOVE_TREE_SECTION);
    insertTreeSectionMenuItem.setOnAction(new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {
        runInsertTreeSectionAction(member);
      }
    });

    ContextMenu result = new ContextMenu();
    result.getItems().add(insertTreeSectionMenuItem);
    return result;
  }

  private void runInsertTreeSectionAction(DepanFxNodeListSection before) {
    listViewer.getMemberLinkMatcher().ifPresent(m -> {
        DepanFxTreeSection insert = new DepanFxTreeSection(m);
        listViewer.insertSection(before, insert);});
  }
}
