package com.pnambic.depanfx.graph_doc.docdata;

import com.pnambic.depanfx.graph.info.GraphNodeInfo;
import com.pnambic.depanfx.graph.model.GraphModel;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;

import java.util.Collection;
import java.util.stream.Stream;

public class NodeInfoBlock {

  public static final String NODE_INFO_TAG = "node-info";

  public static final Class<?>[] ALLOWED_INFO_BLOCKS =
      new Class[] { EdgeInfoBlock.class };

  private static final String INFOS_FIELD_NAME = "infos";

  private final GraphNode node;

  private final Collection<GraphNodeInfo> infos;

  public NodeInfoBlock(GraphNode node, Collection<GraphNodeInfo> infos) {
    this.node = node;
    this.infos = infos;
  }

  public static void prepareTransport(PersistDocumentTransportBuilder builder) {
    builder.addAlias(NODE_INFO_TAG, NodeInfoBlock.class);
    builder.addAllowedType(ALLOWED_INFO_BLOCKS);

    builder.addImplicitCollection(NodeInfoBlock.class, INFOS_FIELD_NAME);
  }

  public GraphNode getNode() {
    return node;
  }

  public GraphNode mapNode(GraphModel model) {
    GraphNode result = (GraphNode) model.findNode(node.getId());
    if (null != result) {
      return result;
    }

    return node;
  }

  public Stream<GraphNodeInfo> streamInfos() {
    return infos.stream();
  }
}
