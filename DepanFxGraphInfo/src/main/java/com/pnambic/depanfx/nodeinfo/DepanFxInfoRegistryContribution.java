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
package com.pnambic.depanfx.nodeinfo;

import com.pnambic.depanfx.base.DepanFxOrderableContribution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DepanFxInfoRegistryContribution implements DepanFxInfoRegistry {

  // @SuppressWarnings("unused")
  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxInfoRegistry.class);

  private final Collection<DepanFxInfoRegistry.Contribution> contribs;

  @Autowired
  public DepanFxInfoRegistryContribution(Collection<DepanFxInfoRegistry.Contribution> contribs) {
    this.contribs = contribs;
  }

  @Override
  public Stream<Contribution> streamContributions() {
    return contribs.stream()
        .sorted(DepanFxOrderableContribution.CONTRIB_COMPARE);
  }

  @Override
  public Optional<Contribution> getById(String id) {

    List<Contribution> idContribs = contribs.stream()
        .filter(c -> id.equals(c.getInfoId()))
        .collect(Collectors.toList());
    if (idContribs.size() > 1) {
      LOG.error("Multiple ({}) contributions for id {}",
          idContribs.size(), id);
    }
    if (!idContribs.isEmpty()) {
      return Optional.of(idContribs.get(0));
    }
    return Optional.empty();
  }

  @Override
  public Stream<Contribution> streamByLabel(String label) {

    return contribs.stream()
        .filter(c -> label.equals(c.getInfoLabel()))
        .sorted(DepanFxOrderableContribution.CONTRIB_COMPARE);
  }

  /**
   * Provide those contributions from the stream that claim to be able
   * to open the supplied document.
   */
  private Stream<Contribution> selectContributions(Object infoSpec) {

    return contribs.stream()
        .filter(c -> c.acceptsInfo(infoSpec));
  }
}
