package com.pnambic.depanfx.git.builder;

import com.pnambic.depanfx.filesystem.graph.DirectoryNode;
import com.pnambic.depanfx.filesystem.graph.DocumentNode;
import com.pnambic.depanfx.graph.model.GraphModel;
import com.pnambic.depanfx.graph.model.GraphNode;

import java.io.File;

public class GitUtils {

  private GitUtils() {
    // Prevent instantiation.
  }

  public static DirectoryNode createDirectory(String docName) {
    return createDirectory(new File(docName));
  }

  public static DirectoryNode createDirectory(File docFile) {
    return new DirectoryNode(docFile.toPath());
  }

  public static DocumentNode createDocument(String docName) {
    return new DocumentNode(new File(docName).toPath());
  }

  /**
   * Map a node to its instance in the graph model, or keep as itself
   * if the node is not present in the graph model.  This is appropriate for
   * git file name, which may be deleted or added from the repository.
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
