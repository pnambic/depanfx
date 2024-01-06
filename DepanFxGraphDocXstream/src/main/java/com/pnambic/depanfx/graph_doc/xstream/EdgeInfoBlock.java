package com.pnambic.depanfx.graph_doc.xstream;

import com.pnambic.depanfx.graph.info.GraphEdgeInfo;
import com.pnambic.depanfx.graph.model.GraphEdge;

import java.util.Collection;
import java.util.stream.Stream;

public class EdgeInfoBlock {

  private final GraphEdge edge;

  private final Collection<GraphEdgeInfo> infos;

  public EdgeInfoBlock(GraphEdge edge, Collection<GraphEdgeInfo> infos) {
    this.edge = edge;
    this.infos = infos;
  }

  public GraphEdge getEdge() {
    return edge;
  }

  public Stream<GraphEdgeInfo> streamInfos() {
    return infos.stream();
  }
}
