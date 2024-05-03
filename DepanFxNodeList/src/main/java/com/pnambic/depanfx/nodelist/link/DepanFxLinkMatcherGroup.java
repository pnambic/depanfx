package com.pnambic.depanfx.nodelist.link;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.model.GraphEdge;

import java.util.Arrays;
import java.util.List;
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

  /////////////////////////////////////
  // Common composites

  public static final List<DepanFxLinkMatcher> MEMBER_MATCHER_GROUP =
      Arrays.asList(new DepanFxLinkMatcher[] { DepanFxLinkMatcherGroup.MEMBER });

  public static boolean isContextModelMemberMatcher(
      ContextModelId modelId, DepanFxLinkMatcherDocument linkMatchDoc) {
    if (!linkMatchDoc.getMatchGroups()
        .contains(DepanFxLinkMatcherGroup.MEMBER)) {
      return false;
    }
    // [29-Nov-2023] Kludge for matches any, actual matcher provided later.
    if (linkMatchDoc.getModelId() == null) {
      return true;
    }
    return linkMatchDoc.getModelId().equals(modelId);

  }
}
