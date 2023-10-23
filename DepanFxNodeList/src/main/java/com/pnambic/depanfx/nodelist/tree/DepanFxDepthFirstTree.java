package com.pnambic.depanfx.nodelist.tree;

import com.pnambic.depanfx.graph.model.GraphNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DepanFxDepthFirstTree {

  public enum NodeState {
    PENDING, EXPLORING, COMPLETE
  };

  public class NodeStatus {

    public NodeState state;

    public int enterTime;

    public int leaveTime;

    public int memberCount;

    public NodeStatus() {
      this.state = NodeState.PENDING;
      this.enterTime = -1;
      this.leaveTime = -1;
      this.memberCount = 0;
    }

    public void enter() {
      state = NodeState.EXPLORING;
      enterTime = timeTick();
    }

    public void leave() {
      state = NodeState.COMPLETE;
      leaveTime = timeTick();
    }

    public void setMember() {
      memberCount++;
    }
  }

  private final DepanFxAdjacencyModel adjModel;

  private final Map<GraphNode, NodeStatus> nodeInfo = new HashMap<>();

  private final DepanFxSimpleAdjacencyModel nodeMembers =
      new DepanFxSimpleAdjacencyModel();

  private int time;

  public DepanFxDepthFirstTree(DepanFxAdjacencyModel adjModel) {
    this.adjModel = adjModel;
  }

  public void buildFromNodes(Collection<GraphNode> nodes) {
    for (GraphNode node : nodes) {
      nodeInfo.put(node, new NodeStatus());
    }

    for (GraphNode node : nodes) {
      NodeStatus nodeStatus = nodeInfo.get(node);
      if (NodeState.PENDING == nodeStatus.state) {
          visit(node, nodeStatus);
      }
    }
  }

  public DepanFxAdjacencyModel getNodeMembers() {
    return nodeMembers;
  }

  public Collection<GraphNode> getTreeMembers() {
    return nodeInfo.keySet();
  }

  public Collection<GraphNode> getRoots() {
    return nodeInfo.entrySet().stream()
        .filter(e -> e.getValue().memberCount == 0)
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
  }

  private void visit(GraphNode visitNode, NodeStatus visitStatus) {
    visitStatus.enter();

    Collection<GraphNode> members = adjModel.getAdjacentNodes(visitNode);
    for (GraphNode memberNode : members) {
      // May need to generalize, with customizable NodeStatus strategies.
      NodeStatus memberStatus = nodeInfo.computeIfAbsent(
          memberNode, k -> new NodeStatus());

      memberStatus.setMember();

      // May need to generalize, with customizable add member strategies.
      // Add all members for this visit node, even if they have been separately
      // visited.  Often, that is a self visit.
      nodeMembers.addAdjacency(visitNode, memberNode);

      if (NodeState.PENDING == memberStatus.state) {
        visit(memberNode, memberStatus);
      }
    }

    visitStatus.leave();
  }

  private int timeTick() {
    return time++;
  }
}
