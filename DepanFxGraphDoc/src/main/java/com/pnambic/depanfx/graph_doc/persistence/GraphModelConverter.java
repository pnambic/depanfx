package com.pnambic.depanfx.graph_doc.persistence;

import com.pnambic.depanfx.graph.api.Edge;
import com.pnambic.depanfx.graph.api.Node;
import com.pnambic.depanfx.graph.context.ContextNodeId;
import com.pnambic.depanfx.graph.context.ContextRelationId;
import com.pnambic.depanfx.graph.info.GraphEdgeInfo;
import com.pnambic.depanfx.graph.info.GraphModelInfo;
import com.pnambic.depanfx.graph.info.GraphNodeInfo;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphModel;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.builder.DepanFxGraphModelBuilder;
import com.pnambic.depanfx.graph_doc.builder.SimpleGraphModelBuilder;
import com.pnambic.depanfx.graph_doc.docdata.EdgeInfoBlock;
import com.pnambic.depanfx.graph_doc.docdata.NodeInfoBlock;
import com.pnambic.depanfx.persistence.BasePersistObjectConverter;
import com.pnambic.depanfx.persistence.PersistMarshalContext;
import com.pnambic.depanfx.persistence.PersistUnmarshalContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class GraphModelConverter
    extends BasePersistObjectConverter<GraphModel> {

  private static final Logger LOG = LoggerFactory.getLogger(GraphModelConverter.class);

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
    GraphModel.class
  };

  public static final String GRAPH_MODEL_TAG = "graph-model";

  @Override
  public Class<?> forType() {
    return GraphModel.class;
  }

  @Override
  public Class<?>[] getAllowTypes() {
    return ALLOW_TYPES;
  }

  @Override
  public String getTag() {
    return GRAPH_MODEL_TAG;
  }

  @Override
  public void marshal(PersistMarshalContext dstContext, Object source) {
    GraphModel graph = (GraphModel) source;
    dstContext.putContextValue(GraphModel.class, graph);

    // Save all nodes and any info.
    for (Node<? extends ContextNodeId> node : graph.getNodes()) {
      marshalObject(dstContext, node);
      marshalNodeInfo(dstContext, graph, (GraphNode) node);
    }

    // Save all edges and any info.
    for (Edge<? extends ContextNodeId, ? extends ContextRelationId> edge
        : graph.getEdges()) {
      marshalObject(dstContext, edge);
      marshalEdgeInfo(dstContext, graph, (GraphEdge) edge);
    }

    // Save any model info.
    graph.streamModelInfo()
        .forEach(info -> marshal(dstContext, info));
  }


  @Override
  public GraphModel unmarshal(PersistUnmarshalContext srcContext) {

    DepanFxGraphModelBuilder builder = new SimpleGraphModelBuilder();
    srcContext.putContextValue(DepanFxGraphModelBuilder.class, builder);

    while (srcContext.hasMoreChildren()) {

      Object element = unmarshalOne(srcContext);
      if (element instanceof GraphNode) {
        builder.mapNode((GraphNode) element);
      } else if (element instanceof GraphEdge) {
        builder.addEdge((GraphEdge) element);
      } else if (element instanceof EdgeInfoBlock) {
        EdgeInfoBlock edgeInfoBlock = (EdgeInfoBlock) element;
        GraphEdge edge = edgeInfoBlock.getEdge();
        edgeInfoBlock.streamInfos()
            .forEach(info -> builder.addEdgeInfo(edge, info.getClass(), info));
      } else if (element instanceof NodeInfoBlock) {
        NodeInfoBlock nodeInfoBlock = (NodeInfoBlock) element;
        GraphNode node = nodeInfoBlock.getNode();
        nodeInfoBlock.streamInfos()
            .forEach(info -> builder.addNodeInfo(node, info.getClass(), info));
      } else if (element instanceof GraphModelInfo) {
        GraphModelInfo graphInfo = (GraphModelInfo) element;
        builder.addModelInfo(graphInfo.getClass(), graphInfo);
      } else {
        LOG.warn("Unrecognized graph element {}", element.getClass());
      }
    }
    return builder.createGraphModel();
  }

  private void marshalEdgeInfo(
      PersistMarshalContext dstContext, GraphModel graph, GraphEdge edge) {

    if (graph.hasEdgeInfo(edge)) {
      List<GraphEdgeInfo> infos = graph.streamEdgeInfo(edge)
          .collect(Collectors.toList());
      EdgeInfoBlock infoBlock = new EdgeInfoBlock(edge, infos);
      marshalObject(dstContext, infoBlock);
    }
  }

  private void marshalNodeInfo(
      PersistMarshalContext dstContext, GraphModel graph, GraphNode node) {
    if (graph.hasNodeInfo(node) ) {
      List<GraphNodeInfo> infos = graph.streamNodeInfo(node)
          .collect(Collectors.toList());
      NodeInfoBlock infoBlock = new NodeInfoBlock(node, infos);
      marshalObject(dstContext, infoBlock);
    }
  }
}
