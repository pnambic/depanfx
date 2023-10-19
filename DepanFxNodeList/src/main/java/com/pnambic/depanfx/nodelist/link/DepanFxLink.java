package com.pnambic.depanfx.nodelist.link;

import com.pnambic.depanfx.graph.model.GraphNode;

public interface DepanFxLink {

  GraphNode getSource();

  GraphNode getTarget();

  static class Simple implements DepanFxLink {

    private final GraphNode targret;

    private final GraphNode source;

    public Simple(GraphNode targret, GraphNode source) {
      this.targret = targret;
      this.source = source;
    }

    @Override
    public GraphNode getSource() {
      return source;
    }

    @Override
    public GraphNode getTarget() {
      return targret;
    }
  }
}
