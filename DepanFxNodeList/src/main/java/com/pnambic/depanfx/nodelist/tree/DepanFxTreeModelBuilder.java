package com.pnambic.depanfx.nodelist.tree;

import com.pnambic.depanfx.graph.model.GraphModel;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcher;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class DepanFxTreeModelBuilder {

  private final DepanFxLinkMatcher linkMatcher;

  public DepanFxTreeModelBuilder(DepanFxLinkMatcher linkMatcher) {
    this.linkMatcher = linkMatcher;
  }

  public DepanFxTreeModel traverseGraph(GraphModel model, DepanFxNodeList nodeList) {
    DepanFxAdjacencyModel adjModel = new DepanFxAdjacencyModel(linkMatcher);
    adjModel.withGraphModel(model);

    DepanFxDepthFirstTree dfsTree = new DepanFxDepthFirstTree(adjModel);
    dfsTree.buildFromNodeList(nodeList);
    Collection<GraphNode> roots = dfsTree.getRoots();
    Map<GraphNode, Collection<GraphNode>> nodeMembers = dfsTree.getNodeMembers();

    Collection<GraphNode> nonEmpty = roots.stream()
        .filter(n -> hasMembers(nodeMembers, n))
        .collect(Collectors.toList());

    return new DepanFxSimpleTreeModel(
        nodeList.getWorkspaceResource(), nodeMembers, nonEmpty);
  }

  private static boolean hasMembers(
      Map<GraphNode, Collection<GraphNode>> nodeMembers, GraphNode node) {
    Collection<GraphNode> members = nodeMembers.get(node);
    if (members != null) {
      return !members.isEmpty();
    }
    return false;
  }
}
