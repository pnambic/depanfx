package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class DepanFxNodeListCell
    extends CheckBoxTreeTableCell<DepanFxNodeListMember, DepanFxNodeListMember> {

  private static final String INSERT_ABOVE_MEMBER_TREE_SECTION =
      "Insert Member Tree Section";

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

    if (member instanceof DepanFxNodeListFlatSection) {
      setContextMenu(nodeListSectionMenu((DepanFxNodeListFlatSection) member));
      return;
    }
  }

  private ContextMenu nodeListSectionMenu(DepanFxNodeListFlatSection member) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem(
        INSERT_ABOVE_MEMBER_TREE_SECTION,
        e -> runInsertMemberTreeSectionAction(member));
    return builder.build();
  }

  private void runInsertMemberTreeSectionAction(DepanFxNodeListSection before) {
    listViewer.insertMemberTreeSection(before);
  }

  private class SelectionState
      implements Callback<Integer, ObservableValue<Boolean>> {

    @Override
    public ObservableValue<Boolean> call(Integer param) {
      TreeItem<DepanFxNodeListMember> item = listViewer.getTreeItem(param.intValue());
      return listViewer.getCheckbooxObservable(item.getValue());
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
