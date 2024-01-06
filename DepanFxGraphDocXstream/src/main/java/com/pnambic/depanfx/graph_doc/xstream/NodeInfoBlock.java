package com.pnambic.depanfx.graph_doc.xstream;

import com.pnambic.depanfx.graph.info.GraphNodeInfo;
import com.pnambic.depanfx.graph.model.GraphNode;

import java.util.Collection;
import java.util.stream.Stream;

public class NodeInfoBlock {

  private final GraphNode node;

  private final Collection<GraphNodeInfo> infos;

  public NodeInfoBlock(GraphNode node, Collection<GraphNodeInfo> infos) {
    this.node = node;
    this.infos = infos;
  }

  public GraphNode getNode() {
    return node;
  }

  public Stream<GraphNodeInfo> streamInfos() {
    return infos.stream();
  }
}
