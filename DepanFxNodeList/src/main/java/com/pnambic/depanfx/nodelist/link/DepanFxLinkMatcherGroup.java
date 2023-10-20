package com.pnambic.depanfx.nodelist.link;

import com.pnambic.depanfx.graph.model.GraphEdge;

import java.util.Optional;

/**
 * Uses instances of a matcher group only as a key for matcher lookup.
 */
public class DepanFxLinkMatcherGroup implements DepanFxLinkMatcher {

  @Override
  public Optional<DepanFxLink> match(GraphEdge edge) {
    return Optional.empty();
  }

  public static final DepanFxLinkMatcherGroup MEMBER =
      new DepanFxLinkMatcherGroup();

  public static final DepanFxLinkMatcherGroup USES =
      new DepanFxLinkMatcherGroup();

  public static final DepanFxLinkMatcherGroup REFS =
      new DepanFxLinkMatcherGroup();
}
