package com.pnambic.depanfx.nodelist.model;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import com.google.common.collect.ImmutableList;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;

public class DepanFxNodeLists {

  private DepanFxNodeLists() {
    // Prevent instantiation.
  }

  public static DepanFxNodeList buildNodeList(
      DepanFxProjectDocument projectDoc,
      GraphDocument graphDoc) {
    Collection<GraphNode> nodes = graphDoc.getGraph().getNodes().stream()
        .map(GraphNode.class::cast)
        .collect(Collectors.toList());
    return new DepanFxNodeList(projectDoc, nodes);
  }

  public static DepanFxNodeList remove(
      DepanFxNodeList base, DepanFxNodeList omit) {
    Set<GraphNode> omitSet = new HashSet<>(omit.getNodes());

    Collection<GraphNode> nodes = base.getNodes().stream()
        .filter(n -> !omitSet.contains(n))
        .collect(Collectors.toList());

    return new DepanFxNodeList(base.getDocRef(), nodes);
  }
}
