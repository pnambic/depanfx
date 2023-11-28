package com.pnambic.depanfx.nodelist.link;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.nio.file.Path;
import java.util.List;

public class DepanFxLinkMatcherDocument {

  public final static String LINK_MATCHER_TOOL_DIR =
      "Link Matchers";

  public static final Path LINK_MATCHER_TOOL_PATH =
      DepanFxProjects.TOOLS_PATH.resolve(LINK_MATCHER_TOOL_DIR);

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
