package com.pnambic.depanfx.graph.context;

import com.google.common.base.Joiner;
import com.pnambic.depanfx.graph.model.GraphRelation;

public class GraphContextKeys {

  private static final String CONTEXT_SEP = ":";

  private static Joiner NODE_KEY_JOINER = Joiner.on(CONTEXT_SEP);

  private GraphContextKeys() {
    // Prevent instantiation.
  }

  public static String toNodeKindKey(ContextNodeKindId nodeKindId) {
    ContextModelId contextId = nodeKindId.getContextModelId();
    return NODE_KEY_JOINER.join(
        contextId.getContextModelKey(),
        nodeKindId.getNodeKindKey());
  }

  public static String toNodeKey(ContextNodeId nodeId) {
    ContextNodeKindId kindId = nodeId.getContextNodeKindId();
    ContextModelId contextId = kindId.getContextModelId();
    return NODE_KEY_JOINER.join(
        contextId.getContextModelKey(),
        kindId.getNodeKindKey(),
        nodeId.getNodeKey());
  }

  public static String toRelationKey(GraphRelation relation) {
    return toRelationKey(relation.getId());
  }

  public static String toRelationKey(ContextRelationId relationId) {
    ContextModelId contextId = relationId.getContextModelId();
    return NODE_KEY_JOINER.join(
        contextId.getContextModelKey(),
        relationId.getRelationKey());
  }
}
