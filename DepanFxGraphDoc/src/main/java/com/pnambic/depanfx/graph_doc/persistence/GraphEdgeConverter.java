package com.pnambic.depanfx.graph_doc.persistence;

import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph.model.GraphRelation;
import com.pnambic.depanfx.graph_doc.builder.DepanFxGraphModelBuilder;
import com.pnambic.depanfx.persistence.BasePersistObjectConverter;
import com.pnambic.depanfx.persistence.PersistMarshalContext;
import com.pnambic.depanfx.persistence.PersistUnmarshalContext;

public class GraphEdgeConverter
    extends BasePersistObjectConverter<GraphEdge> {

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
    GraphEdge.class
  };

  public static final String GRAPH_EDGE_TAG = "graph-edge";

  public static final String RELATION_TAG = "relation";

  private static final String HEAD_TAG = "head";

  private static final String TAIL_TAG = "tail";

  @Override
  public Class<?> forType() {
    return GraphEdge.class;
  }

  @Override
  public Class<?>[] getAllowTypes() {
    return ALLOW_TYPES;
  }

  @Override
  public String getTag() {
    return GRAPH_EDGE_TAG;
  }

  @Override
  public void marshal(PersistMarshalContext dstContext, Object source) {
    GraphEdge edge = (GraphEdge) source;

    marshalObject(dstContext, edge.getRelation());
    marshalProperty(dstContext, HEAD_TAG, edge.getHead());
    marshalProperty(dstContext, TAIL_TAG, edge.getTail());
  }

  @Override
  public GraphEdge unmarshal(PersistUnmarshalContext srcContext) {
    DepanFxGraphModelBuilder modelBuilder =
       (DepanFxGraphModelBuilder) srcContext.getContextValue(
           DepanFxGraphModelBuilder.class);

    GraphRelation relation = (GraphRelation) unmarshalOne(srcContext);

    GraphEdgeBuilder edgeBuilder = new GraphEdgeBuilder(modelBuilder);
    while (srcContext.hasMoreChildren()) {
      edgeBuilder.addNode(srcContext);
    }

    GraphEdge edge = edgeBuilder.newGraphEdge(relation);
    modelBuilder.addEdge(edge);
    return edge;
  }

  private class GraphEdgeBuilder {

    private final DepanFxGraphModelBuilder modelBuilder;

    private GraphNode headNode;

    private GraphNode tailNode;

    public GraphEdgeBuilder(DepanFxGraphModelBuilder modelBuilder) {
      this.modelBuilder = modelBuilder;
    }

    public void addNode(PersistUnmarshalContext srcContext) {

      srcContext.moveDown();
      GraphNode node = (GraphNode) unmarshalOne(srcContext);
      GraphNode mappedNode = modelBuilder.mapNode(node);
      switch (srcContext.getNodeName()) {
        case HEAD_TAG:
          headNode = mappedNode;
          break;
        case TAIL_TAG:
          tailNode = mappedNode;
          break;
      }
      srcContext.moveUp();
    }

    public GraphEdge newGraphEdge(GraphRelation relation) {
      return new GraphEdge(headNode, tailNode, relation);
    }
  }
}
