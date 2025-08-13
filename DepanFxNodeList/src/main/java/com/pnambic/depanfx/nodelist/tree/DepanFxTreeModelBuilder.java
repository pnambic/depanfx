package com.pnambic.depanfx.nodelist.tree;

import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcher;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.graph_doc.model.GraphModel;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.Collection;
import java.util.stream.Collectors;

public class DepanFxTreeModelBuilder {

  private final DepanFxLinkMatcher linkMatcher;

  public DepanFxTreeModelBuilder(DepanFxLinkMatcher linkMatcher) {
    this.linkMatcher = linkMatcher;
  }

  public DepanFxTreeModel traverseGraph(
      DepanFxWorkspaceResource<GraphDocument> graphRsrc,
      Collection<GraphNode> nodes) {
    GraphModel graph = graphRsrc.getResource().getGraph();
    DepanFxAdjacencyModel adjModel = buildAdjacencyModel(graph);

    DepanFxDepthFirstTree dfsTree = new DepanFxDepthFirstTree(adjModel, nodes);
    dfsTree.buildFromNodes(nodes);
    Collection<GraphNode> roots = dfsTree.getRoots();
    DepanFxAdjacencyModel nodeMembers = dfsTree.getNodeMembers();

    Collection<GraphNode> nonEmpty = roots.stream()
        .filter(n -> hasMembers(nodeMembers, n))
        .collect(Collectors.toList());

    return new DepanFxSimpleTreeModel(graphRsrc, nodeMembers, nonEmpty);
  }

  private static boolean hasMembers(
      DepanFxAdjacencyModel nodeMembers, GraphNode node) {
    return !nodeMembers.getAdjacentNodes(node).isEmpty();
  }

  public DepanFxAdjacencyModel buildAdjacencyModel(GraphModel model) {

    DepanFxSimpleAdjacencyModel result = new DepanFxSimpleAdjacencyModel();
    model.getEdges().stream()
        .flatMap(e -> linkMatcher.match((GraphEdge) e).stream())
        .forEach(result::addAdjacency);

    return result;
  }
}
