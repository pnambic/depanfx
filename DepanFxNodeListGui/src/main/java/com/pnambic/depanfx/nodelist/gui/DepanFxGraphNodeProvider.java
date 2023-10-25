package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;

/**
 * This functional interface delivers the {@link GraphNode} associated with
 * the underlying object (e.g. {@code this}.
 */
@FunctionalInterface
public interface DepanFxGraphNodeProvider {

  /**
   * The {@link GraphNode} associated with the underlying object
   * (e.g. {@code this}.
   */
  GraphNode getGraphNode();
}
