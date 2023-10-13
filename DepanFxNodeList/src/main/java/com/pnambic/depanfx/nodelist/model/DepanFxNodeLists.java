package com.pnambic.depanfx.nodelist.model;

import java.util.Collection;
import java.util.stream.Collectors;
import com.google.common.collect.ImmutableList;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;

public class DepanFxNodeLists {

  public static DepanFxNodeList buildNodeList(
      DepanFxProjectDocument projectDoc,
      GraphDocument graphDoc) {
    Collection<GraphNode> nodes = graphDoc.getGraph().getNodes().stream()
        .map(GraphNode.class::cast)
        .collect(Collectors.toList());
    return new DepanFxNodeList(projectDoc, nodes);
  }
}
