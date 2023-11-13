package com.pnambic.depanfx.nodelist.link;

import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;

public interface DepanFxLink {

  GraphNode getSource();

  GraphNode getTarget();

  public static class Simple implements DepanFxLink {

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

  static abstract class OnEdge implements DepanFxLink {

    protected final GraphEdge edge;

    public OnEdge(GraphEdge edge) {
      this.edge = edge;
    }
  }

  static class Forward extends OnEdge {

    public Forward(GraphEdge edge) {
      super(edge);
    }

    @Override
    public GraphNode getSource() {
      return edge.getHead();
    }

    @Override
    public GraphNode getTarget() {
      return edge.getTail();
    }
  }

  static class Reverse extends OnEdge {

    public Reverse(GraphEdge edge) {
      super(edge);
    }

    @Override
    public GraphNode getSource() {
      return edge.getTail();
    }

    @Override
    public GraphNode getTarget() {
      return edge.getHead();
    }
  }
}
