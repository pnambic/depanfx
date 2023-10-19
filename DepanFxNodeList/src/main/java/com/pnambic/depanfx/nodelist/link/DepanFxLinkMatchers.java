package com.pnambic.depanfx.nodelist.link;

import com.pnambic.depanfx.graph.api.Relation;
import com.pnambic.depanfx.graph.context.ContextRelationId;
import com.pnambic.depanfx.graph.model.GraphEdge;

import java.util.List;
import java.util.Optional;

public class DepanFxLinkMatchers {

  public static class DepanFxForwardLinkMatcher implements DepanFxLinkMatcher {

    private final Relation<? extends ContextRelationId> forwardRelation;

    public DepanFxForwardLinkMatcher(
        Relation<? extends ContextRelationId> forwardRelation) {
      this.forwardRelation = forwardRelation;
    }

    @Override
    public Optional<DepanFxLink> match(GraphEdge edge) {
      if (edge.getRelation() == forwardRelation) {
        return Optional.of(
            new DepanFxLink.Simple(edge.getHead(), edge.getTail()));
      }
      return Optional.empty();
    }

  }

  public static class DepanFxReverseLinkMatcher implements DepanFxLinkMatcher {

    private final Relation<? extends ContextRelationId> reverseRelation;

    public DepanFxReverseLinkMatcher(
        Relation<? extends ContextRelationId> reverseRelation) {
      this.reverseRelation = reverseRelation;
    }

    @Override
    public Optional<DepanFxLink> match(GraphEdge edge) {
      if (edge.getRelation() == reverseRelation) {
        return Optional.of(
            new DepanFxLink.Simple(edge.getTail(), edge.getHead()));
      }
      return Optional.empty();
    }
  }

  public static class DepanFxCompositeLinkMatcher
      implements DepanFxLinkMatcher {

    private final List<DepanFxLinkMatcher> matchers;

    public DepanFxCompositeLinkMatcher(List<DepanFxLinkMatcher> matchers) {
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
