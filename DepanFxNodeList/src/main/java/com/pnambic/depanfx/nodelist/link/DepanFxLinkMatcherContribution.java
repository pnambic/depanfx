package com.pnambic.depanfx.nodelist.link;

import com.pnambic.depanfx.graph.context.ContextModelId;

import java.util.Optional;

public interface DepanFxLinkMatcherContribution {

  Optional<DepanFxLinkMatcher> getMatcherByGroup(DepanFxLinkMatcher matcherGroup);

  ContextModelId getModelId();

}
