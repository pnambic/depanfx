package com.pnambic.depanfx.nodelist.tree;

import com.pnambic.depanfx.graph.model.GraphNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DepanFxTreeDepthFirstTraversal {

  public enum NodeRole {
    ROOT, MEMBER
  };

  public enum NodeState {
    PENDING, EXPLORING, COMPLETE
  };

  public class NodeStatus {

    public NodeRole role;

    public NodeState state;

    public int enterTime;

    public int leaveTime;

    public NodeStatus() {
      this.role = NodeRole.ROOT;
      this.state = NodeState.PENDING;
      this.enterTime = -1;
      this.leaveTime = -1;
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
      role = NodeRole.MEMBER;
    }
  }

  private final DepanFxTreeModel treeModel;

  private final Map<GraphNode, NodeStatus> nodeInfo = new HashMap<>();

  private final Map<GraphNode, Collection<GraphNode>> nodeMembers = new HashMap<>();

  private int time;

  public DepanFxTreeDepthFirstTraversal(DepanFxTreeModel treeModel) {
    this.treeModel = treeModel;
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

  private void visit(GraphNode visitNode, NodeStatus visitStatus) {
    visitStatus.enter();

    Collection<GraphNode> members = treeModel.getMembers(visitNode);
    for (GraphNode memberNode : members) {
      NodeStatus memberStatus = nodeInfo.computeIfAbsent(
          memberNode, k -> new NodeStatus());
      memberStatus.setMember();
      if (NodeState.PENDING == memberStatus.state) {
        addMember(visitNode, memberNode);
        visit(memberNode, memberStatus);
      }
    }

    visitStatus.leave();
  }

  public Collection<GraphNode> getTreeMembers() {
    return nodeInfo.keySet();
  }

  public Map<GraphNode, Collection<GraphNode>> getNodeMembers() {
    return nodeMembers;
  }

  public Collection<GraphNode> getRoots() {
    return nodeInfo.entrySet().stream()
        .filter(e -> e.getValue().role == NodeRole.ROOT)
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
  }

  private void addMember(GraphNode container, GraphNode member) {
    nodeMembers
        .computeIfAbsent(container, k-> new ArrayList<>())
        .add(member);
  }

  private int timeTick() {
    return time++;
  }
}
