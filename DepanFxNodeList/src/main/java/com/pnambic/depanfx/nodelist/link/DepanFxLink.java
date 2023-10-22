package com.pnambic.depanfx.nodelist.link;

import com.pnambic.depanfx.graph.model.GraphNode;

public interface DepanFxLink {

  GraphNode getSource();

  GraphNode getTarget();

  static class Simple implements DepanFxLink {

    private final GraphNode source;

    private final GraphNode target;

    public Simple(GraphNode source, GraphNode target) {
      this.source = source;
      this.target = target;
    }

    @Override
    public GraphNode getSource() {
      return source;
    }

    @Override
    public GraphNode getTarget() {
      return target;
    }
  }
}
