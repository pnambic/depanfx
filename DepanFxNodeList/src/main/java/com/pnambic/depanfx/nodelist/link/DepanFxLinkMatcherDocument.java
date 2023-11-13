package com.pnambic.depanfx.nodelist.link;

import com.pnambic.depanfx.graph.context.ContextModelId;

import java.util.List;

public class DepanFxLinkMatcherDocument {

  public final static String BUILT_IN_LINK_MATCHER_PATH =
      "Tools/Link Matchers";

  private final ContextModelId contextModelId;

  private final List<DepanFxLinkMatcher> matchGroups;

  private final DepanFxLinkMatcher matcher;

  public DepanFxLinkMatcherDocument(ContextModelId contextModelId,
      List<DepanFxLinkMatcher> matchGroups, DepanFxLinkMatcher matcher) {
    this.contextModelId = contextModelId;
    this.matchGroups = matchGroups;
    this.matcher = matcher;
  }

  public ContextModelId getModelId() {
    return contextModelId;
  }

  public List<DepanFxLinkMatcher> getMatchGroups() {
    return matchGroups;
  }

  public DepanFxLinkMatcher getMatcher() {
    return matcher;
  }
}
