/*
 * Copyright 2025 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pnambic.depanfx.edgematchers.link;

import com.pnambic.depanfx.edgematchers.tooldata.DepanFxBaseMatcherDocument;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcher;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Provide translation and serialization/transport from matcher data
 * to matcher instances.
 */
@Component
public class DepanFxLinkMatchersRegistry {

  public interface Contribution {

    boolean accepts(DepanFxBaseMatcherDocument filterInfo);

    DepanFxLinkMatcher buildMatcher(
        DepanFxBaseMatcherDocument filterInfo);

    void prepareTransport(PersistDocumentTransportBuilder builder);
  }

  public static abstract class TransportableContribution
      implements Contribution {

    private final Class<?> contribType;

    public TransportableContribution(Class<?> contribType) {
      this.contribType = contribType;
    }

    @Override
    public boolean accepts(DepanFxBaseMatcherDocument matcherInfo) {
      return contribType.isAssignableFrom(matcherInfo.getClass());
    }

    protected void prepareTransport(
        PersistDocumentTransportBuilder builder,
        String aliasTag,
        Class<?> transportType) {
      Class<?>[] allowType = new Class<?>[] { transportType };
      builder.addAllowedType(allowType);
      builder.addAlias(aliasTag, transportType);
    }
  }

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxLinkMatchersRegistry.class);

  private final List<Contribution> contribs;

  public DepanFxLinkMatchersRegistry(List<Contribution> contribs) {
    this.contribs = contribs;
  }

  public DepanFxLinkMatcher buildMatcher(
      DepanFxBaseMatcherDocument filterInfo) {
    return lookupMatcher(filterInfo).get();
  }

  public Optional<DepanFxLinkMatcher> lookupMatcher(
      DepanFxBaseMatcherDocument filterInfo) {
    return lookupContrib(filterInfo)
        .map(c -> c.buildMatcher(filterInfo));
  }

  public void prepareTransport(PersistDocumentTransportBuilder builder) {
    contribs.forEach(c -> c.prepareTransport(builder));
  }

  private Optional<Contribution> lookupContrib(
      DepanFxBaseMatcherDocument filterInfo) {
    Optional<Contribution> result = contribs.stream()
        .filter(c -> c.accepts(filterInfo))
        .findFirst();
    if (result.isEmpty()) {
      LOG.warn("Unexpected filter {}",
          filterInfo.getClass().getName());
    }
    return result;
  }
}
