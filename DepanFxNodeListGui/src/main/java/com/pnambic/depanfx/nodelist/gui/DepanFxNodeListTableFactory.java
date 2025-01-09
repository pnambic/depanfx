package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.nodelist.gui.columns.DepanFxColumnRegistry;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumn;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSection;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxSectionRegistry;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxBaseColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxBaseSectionData;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.TreeTableColumn;

/**
 * Provides components for structural changes to the node list table.
 */
public class DepanFxNodeListTableFactory {

  private final DepanFxNodeListTableAdapter tableAdapter;

  public DepanFxNodeListTableFactory(
      DepanFxNodeListTableAdapter tableAdapter) {
    this.tableAdapter = tableAdapter;
  }

  public DepanFxNodeListColumn createTableColumn(
      DepanFxWorkspaceResource<? extends DepanFxBaseColumnData> columnRsrc) {
    return DepanFxColumnRegistry.toColumn(tableAdapter, columnRsrc);
  }

  public DepanFxNodeListSection createTableSection(
      DepanFxWorkspaceResource<? extends DepanFxBaseSectionData> sectionRsrc) {
    return DepanFxSectionRegistry.createSection(tableAdapter, sectionRsrc);
  }

  public DepanFxNodeListCell createTableCell() {
    return new DepanFxNodeListCell(tableAdapter);
  }

  public TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember>
      createNameColumn() {

    // The first column is always the name, and it is not saved.
    TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember> result =
        new TreeTableColumn<>("Node Name");
    result.setCellFactory(p -> createTableCell());
    result.setCellValueFactory(
        p -> new ReadOnlyObjectWrapper<>(p.getValue().getValue()));
    result.setPrefWidth(DepanFxSceneControls.layoutWidthMs(30));
    return result;
  }
}
