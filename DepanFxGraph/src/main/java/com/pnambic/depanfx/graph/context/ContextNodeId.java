package com.pnambic.depanfx.graph.context;

public interface ContextNodeId {
  
  ContextNodeKindId getContextNodeKindId();

  String getNodeKey();

  String getSimpleName();

  // String getNodeName(ContextNodeId contextId);
}
