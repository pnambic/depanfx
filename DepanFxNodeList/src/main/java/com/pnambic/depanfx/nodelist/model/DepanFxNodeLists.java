package com.pnambic.depanfx.nodelist.model;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DepanFxNodeLists {

  private DepanFxNodeLists() {
    // Prevent instantiation.
  }

  public static DepanFxNodeList buildNodeList(
      DepanFxWorkspaceResource wkspRsrc) {

    GraphDocument graphDoc = (GraphDocument) wkspRsrc.getResource();
    Collection<GraphNode> nodes = graphDoc.getGraph().getNodes().stream()
        .map(GraphNode.class::cast)
        .collect(Collectors.toList());
    return new DepanFxNodeList(wkspRsrc, nodes);
  }

  public static DepanFxNodeList remove(
      DepanFxNodeList base, DepanFxNodeList omit) {
    Set<GraphNode> omitSet = new HashSet<>(omit.getNodes());

    Collection<GraphNode> nodes = base.getNodes().stream()
        .filter(n -> !omitSet.contains(n))
        .collect(Collectors.toList());

    return new DepanFxNodeList(base.getWorkspaceResource(), nodes);
  }

  public static DepanFxNodeList buildEmptyNodeList(DepanFxNodeList base) {
    return new DepanFxNodeList(
        base.getWorkspaceResource(), Collections.emptyList());
  }
}
