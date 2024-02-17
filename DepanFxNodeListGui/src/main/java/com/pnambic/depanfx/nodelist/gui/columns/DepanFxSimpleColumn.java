package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListGraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListViewer;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxSimpleColumnData;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import javafx.scene.control.ContextMenu;

public class DepanFxSimpleColumn extends DepanFxAbstractColumn {

  private DepanFxWorkspaceResource columnDataRsrc;

  public DepanFxSimpleColumn(
      DepanFxNodeListViewer listViewer,
      DepanFxWorkspaceResource columnDataRsrc) {
    super(listViewer);
    this.columnDataRsrc = columnDataRsrc;
  }

  @Override
  public String getColumnLabel() {
    return getColumnData().getColumnLabel();
  }

  @Override
  protected double getWidthMs() {
    return getColumnData().getWidthMs();
  }

  @Override
  public String toString(DepanFxNodeListGraphNode member) {
    return member.getGraphNode()
        .getId().getContextNodeKindId().getContextModelId()
        .getContextModelKey();
  }

  public DepanFxSimpleColumnData getColumnData() {
    return (DepanFxSimpleColumnData) columnDataRsrc.getResource();
  }

  @Override
  protected ContextMenu buildColumnContextMenu(
      DepanFxDialogRunner depanFxDialogRunner) {
    return null;
  }
}
