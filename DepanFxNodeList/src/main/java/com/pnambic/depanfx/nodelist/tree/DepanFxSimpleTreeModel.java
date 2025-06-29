package com.pnambic.depanfx.nodelist.tree;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeFoldData.NodeNest;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class DepanFxSimpleTreeModel
    implements DepanFxTreeModel, DepanFxAdjacencyModel {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxSimpleTreeModel.class);

  private final DepanFxWorkspaceResource<GraphDocument> graphDocResource;

  private final DepanFxAdjacencyModel nodeMembers;

  private final Collection<GraphNode> roots;

  public DepanFxSimpleTreeModel(
      DepanFxWorkspaceResource<GraphDocument> graphDocResource,
      DepanFxAdjacencyModel nodeMembers,
      Collection<GraphNode> roots) {
    this.graphDocResource = graphDocResource;
    this.nodeMembers = nodeMembers;
    this.roots = roots;
  }

  @Override
  public DepanFxWorkspaceResource<GraphDocument> getGraphDocResource() {
    return graphDocResource;
  }

  @Override
  public Collection<GraphNode> getMembers(GraphNode node) {
    return nodeMembers.getAdjacentNodes(node);
  }

  @Override
  public Collection<GraphNode> getRoots() {
    return roots;
  }

  @Override
  public TreeMode getTreeMode(GraphNode node) {
    if (getMembers(node).size() > 0) {
      return TreeMode.FORK;
    }
    return TreeMode.LEAF;
  }

  @Override
  public DepanFxNodeList getReachableGraphNodes(
      Collection<GraphNode> startNodes, Collection<GraphNode> filterNodes) {

    DepanFxDepthFirstTree treeDft =
        new DepanFxDepthFirstTree(this, filterNodes);
    treeDft.buildFromNodes(startNodes);
    return new DepanFxNodeList(
        "Reachable nodes", "Reachable nodes",
        graphDocResource, treeDft.getTreeMembers());
  }

  @Override // DepanFxAdjacencyModel
  public Collection<GraphNode> getAdjacentNodes(GraphNode node) {
    return getMembers(node);
  }

  @Override // DepanFxTreeModel
  public DepanFxTreeModel subTreeModel(GraphNode subRoot) {
    DepanFxSimpleAdjacencyModel subModel = new DepanFxSimpleAdjacencyModel();
    recurseTreeModel(subModel, subRoot);

    return new DepanFxSimpleTreeModel(
        graphDocResource,
        subModel,
        new ArrayList<>(Collections.singleton(subRoot)) );
  }

  public void addTreeModel(DepanFxTreeModel addTree) {
    if (nodeMembers instanceof DepanFxSimpleAdjacencyModel baseModel) {
      if (addTree instanceof DepanFxSimpleTreeModel simple) {

        // Add the adjacency data from the sub-tree
        baseModel.addAdjacencies(simple.nodeMembers);
        List<GraphNode> treeRoots = baseModel.computeRootNodes();
        treeRoots.stream()
            .filter(n -> !roots.contains(n))
            .forEach(n -> roots.add(n));
        return;
      }
      return;
    }
  }

  private void recurseTreeModel(
      DepanFxSimpleAdjacencyModel result, GraphNode subRoot) {
    if (!result.getAdjacentNodes(subRoot).isEmpty()) {
      LOG.info("Odd to see {} again", subRoot);
    }

    nodeMembers.getAdjacentNodes(subRoot).stream()
        .forEach(members -> {
          result.addAdjacency(subRoot, members);
          recurseTreeModel(result, members);
        });
  }

  @Override
  public Stream<NodeNest> streamNodeParent() {
    return nodeMembers.streamNodeParent();
  }
}
