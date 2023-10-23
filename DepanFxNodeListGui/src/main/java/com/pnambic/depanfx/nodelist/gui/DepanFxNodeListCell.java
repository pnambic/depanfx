package com.pnambic.depanfx.nodelist.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.util.StringConverter;

public class DepanFxNodeListCell extends CheckBoxTreeCell<DepanFxNodeListMember> {

  private static final String INSERT_ABOVE_TREE_SECTION = "Insert Tree Section";

  // Allow cells to act on viewer (e.g. change sections, etc.)
  private final DepanFxNodeListViewer listViewer;

  public DepanFxNodeListCell(DepanFxNodeListViewer listViewer) {
    this.listViewer = listViewer;
    setConverter(new NameConverter());
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
    if (member instanceof DepanFxNodeListFlatSection) {
      setContextMenu(nodeListSectionMenu((DepanFxNodeListFlatSection) member));
      return;
    }
  }

  private ContextMenu nodeListSectionMenu(DepanFxNodeListFlatSection member) {
    MenuItem insertTreeSectionMenuItem =
        new MenuItem(INSERT_ABOVE_TREE_SECTION);
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

  private static class NameConverter extends StringConverter<TreeItem<DepanFxNodeListMember>> {

    @Override
    public String toString(TreeItem<DepanFxNodeListMember> object) {
      return object.getValue().getDisplayName();
    }

    @Override
    public TreeItem<DepanFxNodeListMember> fromString(String string) {
      throw new UnsupportedOperationException();
    }
  }
}
