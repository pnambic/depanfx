package com.pnambic.depanfx.graph.context;

public interface ContextNodeId {

  GraphModel<?,?> getGraphModel();

  String getNodeKind();
}
