package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.graph.context.ContextNodeId;
import com.pnambic.depanfx.graph.context.ContextNodeKindId;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListGraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListViewer;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeKeyColumnData;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

public class DepanFxNodeKeyColumn extends DepanFxAbstractColumn {

  private DepanFxWorkspaceResource columnDataRsrc;

  public DepanFxNodeKeyColumn(
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

  public DepanFxNodeKeyColumnData getColumnData() {
    return (DepanFxNodeKeyColumnData) columnDataRsrc.getResource();
  }

  @Override
  public String toString(DepanFxNodeListGraphNode member) {
    ContextNodeId nodeId = member.getGraphNode().getId();
    ContextNodeKindId kindId = nodeId.getContextNodeKindId();
    switch (getColumnData().getKeyChoice()) {
    case MODEL_KEY:
      return kindId.getContextModelId().getContextModelKey();
    case KIND_KEY:
      return kindId.getNodeKindKey();
    case NODE_KEY:
      return nodeId.getNodeKey();
    }
    return "<unknown key>";
  }
}
