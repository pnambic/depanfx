package com.pnambic.depanfx.nodelist.link;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

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

  public static Optional<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
      getMemberMatcherRsrc(DepanFxWorkspace workspace, ContextModelId modelId) {
    return DepanFxProjects.getBuiltIn(
        workspace, DepanFxLinkMatcherDocument.class,
        c -> isContextModelMemberMatcher(modelId, c.getDocument()));
  }

  /**
   * For referenced matchers, a null model id indicates that
   * the owner tool will resolve the actual member matchers later on.
   */
  public static boolean isContextModelMatcherResource(
      ContextModelId modelId,
      DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> matcherRsrc) {
    DepanFxLinkMatcherDocument matcher = matcherRsrc.getResource();
    if (!matcher.getMatchGroups()
        .contains(DepanFxLinkMatcherGroup.MEMBER)) {
      return false;
    }
    // [29-Nov-2023] Kludge for matches any, actual matcher provided later.
    if (matcher.getModelId() == null) {
      return true;
    }
    return matcher.getModelId().equals(modelId);
  }

  private static boolean isContextModelMemberMatcher(
      ContextModelId modelId, DepanFxLinkMatcherDocument linkMatchDoc) {
    if (!linkMatchDoc.getMatchGroups()
        .contains(DepanFxLinkMatcherGroup.MEMBER)) {
      return false;
    }
    // Avoid NPE, not what we are looking for.
    if (linkMatchDoc.getModelId() == null) {
      return false;
    }
    return linkMatchDoc.getModelId().equals(modelId);
  }
}
