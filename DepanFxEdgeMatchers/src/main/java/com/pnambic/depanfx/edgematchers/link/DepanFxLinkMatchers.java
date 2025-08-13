package com.pnambic.depanfx.edgematchers.link;

import com.pnambic.depanfx.graph.api.Relation;
import com.pnambic.depanfx.graph.context.ContextRelationId;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLink;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcher;

import java.util.List;
import java.util.Optional;

public class DepanFxLinkMatchers {

  private DepanFxLinkMatchers() {
    // Prevent instantiation.
  }

  public static final DepanFxLinkMatcher EMPTY_MATCHER =
      new DepanFxLinkMatcher() {

        @Override
        public Optional<DepanFxLink> match(GraphEdge edge) {
          return Optional.empty();
        }
      };

  public static final DepanFxLinkMatcher ALL_EDGES_FORWARD =

      new DepanFxLinkMatcher() {

        @Override
        public Optional<DepanFxLink> match(GraphEdge edge) {
          return Optional.of(new DepanFxLinks.Forward(edge));
        }
  };

  public static final DepanFxLinkMatcher ALL_EDGES_REVERSE =

      new DepanFxLinkMatcher() {

        @Override
        public Optional<DepanFxLink> match(GraphEdge edge) {
          return Optional.of(new DepanFxLinks.Reverse(edge));
        }
  };

  public static class ForwardRelation implements DepanFxLinkMatcher {

    private final Relation<? extends ContextRelationId> forwardRelation;

    public ForwardRelation(
        Relation<? extends ContextRelationId> forwardRelation) {
      this.forwardRelation = forwardRelation;
    }

    @Override
    public Optional<DepanFxLink> match(GraphEdge edge) {
      if (edge.getRelation() == forwardRelation) {
        return Optional.of(new DepanFxLinks.Forward(edge));
      }
      return Optional.empty();
    }
  }

  public static class ReverseRelation implements DepanFxLinkMatcher {

    private final Relation<? extends ContextRelationId> reverseRelation;

    public ReverseRelation(
        Relation<? extends ContextRelationId> reverseRelation) {
      this.reverseRelation = reverseRelation;
    }

    @Override
    public Optional<DepanFxLink> match(GraphEdge edge) {
      if (edge.getRelation() == reverseRelation) {
        return Optional.of(new DepanFxLinks.Reverse(edge));
      }
      return Optional.empty();
    }
  }

  public static class Composite
      implements DepanFxLinkMatcher {

    private final List<DepanFxLinkMatcher> matchers;

    public Composite(List<DepanFxLinkMatcher> matchers) {
      this.matchers = matchers;
    }

    @Override
    public Optional<DepanFxLink> match(GraphEdge edge) {
      return matchers.stream()
          .flatMap(m -> m.match(edge).stream())
          .findFirst();
    }
  }
}
