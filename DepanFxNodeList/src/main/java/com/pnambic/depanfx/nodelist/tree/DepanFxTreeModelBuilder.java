package com.pnambic.depanfx.nodelist.tree;

import com.pnambic.depanfx.graph.model.GraphModel;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.adjacency.DepanFxAdjacencyModel;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcher;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel.TreeMode;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DepanFxTreeModelBuilder {

  private final DepanFxLinkMatcher linkMatcher;

  private Map<GraphNode, Collection<GraphNode>> nodeMembers;

  private Collection<GraphNode> roots;

  private Map<GraphNode, TreeMode> treeModes;

  public DepanFxTreeModelBuilder(DepanFxLinkMatcher linkMatcher) {
    this.linkMatcher = linkMatcher;
  }

  public void traverseGraph(GraphModel model) {
    DepanFxAdjacencyModel adjModel = new DepanFxAdjacencyModel(linkMatcher);

    adjModel.withGraphModel(model);
    Map<GraphNode, List<GraphNode>> adjData = adjModel.getAdjacencyData();
  }

  public DepanFxTreeModel buildTreeModel() {
    return new DepanFxSimpleTreeModel(nodeMembers, roots, treeModes);
  }
}
