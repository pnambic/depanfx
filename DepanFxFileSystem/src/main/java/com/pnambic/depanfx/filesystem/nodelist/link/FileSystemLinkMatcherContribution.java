package com.pnambic.depanfx.filesystem.nodelist.link;

import com.pnambic.depanfx.filesystem.context.FileSystemContextDefinition;
import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcher;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherContribution;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherGroup;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FileSystemLinkMatcherContribution
    implements DepanFxLinkMatcherContribution {

  @Override
  public Optional<DepanFxLinkMatcher> getMatcherByGroup(
      DepanFxLinkMatcher matcherGroup) {
    if (matcherGroup == DepanFxLinkMatcherGroup.MEMBER) {
      return Optional.of(FileSystemLinkMatchers.MEMBER);
    }

    return Optional.empty();
  }

  @Override
  public ContextModelId getModelId() {
    return FileSystemContextDefinition.MODEL_ID;
  }
}
