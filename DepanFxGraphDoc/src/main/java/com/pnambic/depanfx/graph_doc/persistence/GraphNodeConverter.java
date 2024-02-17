package com.pnambic.depanfx.graph_doc.persistence;

import com.pnambic.depanfx.graph.context.GraphContextKeys;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.persistence.BasePersistObjectConverter;
import com.pnambic.depanfx.persistence.PersistMarshalContext;
import com.pnambic.depanfx.persistence.PersistUnmarshalContext;

public class GraphNodeConverter
    extends BasePersistObjectConverter<GraphNode> {

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
    GraphNode.class
  };

  public static final String GRAPH_NODE_TAG = "graph-node";

  @Override
  public Class<?> forType() {
    return GraphNode.class;
  }

  @Override
  public Class<?>[] getAllowTypes() {
    return ALLOW_TYPES;
  }

  @Override
  public String getTag() {
    return GRAPH_NODE_TAG;
  }

  @Override
  public void marshal(PersistMarshalContext dstContext, Object source) {
    GraphNode node = (GraphNode) source;
    marshalObject(dstContext, GRAPH_NODE_TAG, getNodeKey(node));
  }


  @Override
  public GraphNode unmarshal(PersistUnmarshalContext srcContext) {
    return (GraphNode) unmarshalOne(srcContext);
  }

  private String getNodeKey(GraphNode node) {
    return GraphContextKeys.toNodeKey(node.getId());
  }
}
