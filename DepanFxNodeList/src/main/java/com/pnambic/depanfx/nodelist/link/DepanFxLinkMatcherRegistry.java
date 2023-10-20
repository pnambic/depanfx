package com.pnambic.depanfx.nodelist.link;

import com.pnambic.depanfx.graph.context.ContextModelId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DepanFxLinkMatcherRegistry {

  public List<DepanFxLinkMatcherContribution> contributions;

  @Autowired
  public DepanFxLinkMatcherRegistry(
      List<DepanFxLinkMatcherContribution> contributions) {
    this.contributions = contributions;
  }

  public Optional<DepanFxLinkMatcher> getMatcherByGroup(
      ContextModelId modelId,
      DepanFxLinkMatcher matcherGroup) {
    // Prolly use a map in the future
    return contributions.stream()
        .filter(c -> c.getModelId() == modelId)
        .map(c -> c.getMatcherByGroup(matcherGroup))
        .findFirst()
        .get();
  }
}
