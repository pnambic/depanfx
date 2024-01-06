package com.pnambic.depanfx.graph_doc.xstream;

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
import com.pnambic.depanfx.persistence.AbstractObjectXmlConverter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class GraphModelConverter
    extends AbstractObjectXmlConverter<GraphModel> {

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
  public void marshal(Object source, HierarchicalStreamWriter writer,
      MarshallingContext context, Mapper mapper) {
    GraphModel graph = (GraphModel) source;
    context.put(GraphModel.class, graph);

    // Save all nodes and any info.
    for (Node<? extends ContextNodeId> node : graph.getNodes()) {
      marshalObject(node, writer, context, mapper);
      marshalNodeInfo(graph, (GraphNode) node, writer, context, mapper);
    }

    // Save all edges and any info.
    for (Edge<? extends ContextNodeId, ? extends ContextRelationId> edge : graph.getEdges()) {
      marshalObject(edge, writer, context, mapper);
      marshalEdgeInfo(graph, (GraphEdge) edge, writer, context, mapper);
    }

    // Save any model info.
    graph.streamModelInfo()
        .forEach(info -> marshal(info, writer, context, mapper));
  }

  @Override
  public GraphModel unmarshal(HierarchicalStreamReader reader,
      UnmarshallingContext context, Mapper mapper) {

    DepanFxGraphModelBuilder builder = new SimpleGraphModelBuilder();
    context.put(DepanFxGraphModelBuilder.class, builder);

    while (reader.hasMoreChildren()) {

      Object element = unmarshalOne(reader, context, mapper);
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
      GraphModel graph, GraphEdge edge,
      HierarchicalStreamWriter writer, MarshallingContext context,
      Mapper mapper) {
    if (graph.hasEdgeInfo(edge)) {
      List<GraphEdgeInfo> infos = graph.streamEdgeInfo(edge)
          .collect(Collectors.toList());
      EdgeInfoBlock infoBlock = new EdgeInfoBlock(edge, infos);
      marshalObject(infoBlock, writer, context, mapper);
    }
  }

  private void marshalNodeInfo(
      GraphModel graph, GraphNode node,
      HierarchicalStreamWriter writer, MarshallingContext context,
      Mapper mapper) {
    if (graph.hasNodeInfo(node) ) {
      List<GraphNodeInfo> infos = graph.streamNodeInfo(node)
          .collect(Collectors.toList());
      NodeInfoBlock infoBlock = new NodeInfoBlock(node, infos);
      marshalObject(infoBlock, writer, context, mapper);
    }
  }
}
