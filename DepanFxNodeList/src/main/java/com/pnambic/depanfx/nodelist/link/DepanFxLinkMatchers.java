package com.pnambic.depanfx.nodelist.link;

import com.pnambic.depanfx.graph.api.Relation;
import com.pnambic.depanfx.graph.context.ContextRelationId;
import com.pnambic.depanfx.graph.model.GraphEdge;

import java.util.List;
import java.util.Optional;

public class DepanFxLinkMatchers {

  DepanFxLinkMatchers() {
    // Prevent instantiation.
  }

  public static class ForwardRelation implements DepanFxLinkMatcher {

    private final Relation<? extends ContextRelationId> forwardRelation;

    public ForwardRelation(
        Relation<? extends ContextRelationId> forwardRelation) {
      this.forwardRelation = forwardRelation;
    }

    @Override
    public Optional<DepanFxLink> match(GraphEdge edge) {
      if (edge.getRelation() == forwardRelation) {
        return Optional.of(new DepanFxLink.Forward(edge));
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
        return Optional.of(new DepanFxLink.Reverse(edge));
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
      for (DepanFxLinkMatcher matcher : matchers) {
        Optional<DepanFxLink> optMatch = matcher.match(edge);
        if (optMatch.isPresent()) {
          return optMatch;
        }
      }
      return Optional.empty();
    }

    public Optional<DepanFxLink> matchX(GraphEdge edge) {
      return matchers.stream()
          .map(m -> m.match(edge))
          .filter(Optional::isPresent)
          .findFirst()
          .get();
    }
  }
}
