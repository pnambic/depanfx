package com.pnambic.depanfx.nodelist.tree;

import com.pnambic.depanfx.graph.api.Edge;
import com.pnambic.depanfx.graph.context.ContextNodeId;
import com.pnambic.depanfx.graph.context.ContextRelationId;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphModel;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcher;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;

import java.util.Collection;
import java.util.stream.Collectors;

public class DepanFxTreeModelBuilder {

  private final DepanFxLinkMatcher linkMatcher;

  public DepanFxTreeModelBuilder(DepanFxLinkMatcher linkMatcher) {
    this.linkMatcher = linkMatcher;
  }

  public DepanFxTreeModel traverseGraph(GraphModel model, DepanFxNodeList nodeList) {
    DepanFxAdjacencyModel adjModel = buildAdjacencyModel(model);

    Collection<GraphNode> nodes = nodeList.getNodes();
    DepanFxDepthFirstTree dfsTree = new DepanFxDepthFirstTree(adjModel, nodes);
    dfsTree.buildFromNodes(nodes);
    Collection<GraphNode> roots = dfsTree.getRoots();
    DepanFxAdjacencyModel nodeMembers = dfsTree.getNodeMembers();

    Collection<GraphNode> nonEmpty = roots.stream()
        .filter(n -> hasMembers(nodeMembers, n))
        .collect(Collectors.toList());

    return new DepanFxSimpleTreeModel(
        nodeList.getGraphDocResource(), nodeMembers, nonEmpty);
  }

  private static boolean hasMembers(
      DepanFxAdjacencyModel nodeMembers, GraphNode node) {
    return !nodeMembers.getAdjacentNodes(node).isEmpty();
  }

  public DepanFxAdjacencyModel buildAdjacencyModel(GraphModel model) {

    DepanFxSimpleAdjacencyModel result = new DepanFxSimpleAdjacencyModel();
    for (Edge<? extends ContextNodeId, ? extends ContextRelationId> edge : model.getEdges()) {
      linkMatcher.match((GraphEdge) edge)
          .ifPresent(result::addAdjacency);
    }
    return result;
  }
}
