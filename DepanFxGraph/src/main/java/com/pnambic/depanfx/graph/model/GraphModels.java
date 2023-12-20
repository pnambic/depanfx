package com.pnambic.depanfx.graph.model;

public class GraphModels {

  private GraphModels() {
    // Prevent instantiation.
  }

  /**
   * Map a node to its instance in the graph model, or keep as itself
   * if the node is not present in the graph model.  This is appropriate for
   * domains where nodes may be deleted or added with respect to a graph model.
   *
   * Nodes in the graph model that match the supplied graph node must be
   * {@code isAssignableFrom()} to the original node.
   *
   * @throws ClassCastException if a matching node in the graph model is not
   *    assignable to the provided graph node.
   */
  @SuppressWarnings("unchecked")
  public static <T extends GraphNode> T mapGraphNode(
      T baseNode, GraphModel graphModel) {

    GraphNode foundNode = (GraphNode) graphModel.findNode(baseNode.getId());
    if (foundNode != null) {
      if (baseNode.getClass().isAssignableFrom(foundNode.getClass())) {
        return (T) foundNode;
      }
      throw new ClassCastException("Unexpected type for mapped node");
    }
    return baseNode;
  }

}
