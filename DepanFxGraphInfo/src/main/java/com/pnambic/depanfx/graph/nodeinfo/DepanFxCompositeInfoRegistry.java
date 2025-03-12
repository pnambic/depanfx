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
package com.pnambic.depanfx.graph.nodeinfo;

import com.pnambic.depanfx.base.DepanFxOrderableContribution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implement an info registry that is composed of local contributions
 * and various upstream registries.
 *
 * Local contributions have priority over contributions
 * from upstream registries.
 */
public class DepanFxCompositeInfoRegistry implements DepanFxInfoRegistry {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxCompositeInfoRegistry.class);

  private final List<DepanFxInfoRegistry> upstreamRegistries;

  private final List<DepanFxInfoRegistry.Contribution> compositeContribs;

  private DepanFxCompositeInfoRegistry(
      List<DepanFxInfoRegistry> upstreamRegistries,
      List<Contribution> compositeContribs) {

    this.upstreamRegistries = upstreamRegistries;
    this.compositeContribs = compositeContribs;
  }

  public static DepanFxCompositeInfoRegistry buildInfoRegistry(
      DepanFxInfoRegistry upstreamRegistry,
      List<Contribution> compositeContribs) {

    return new DepanFxCompositeInfoRegistry(
        Collections.singletonList(upstreamRegistry),
        rightSizeClone(compositeContribs));
  }

  public void addInfoContribution(DepanFxInfoRegistry.Contribution contrib) {
    compositeContribs.add(contrib);
  }

  @Override
  public Stream<Contribution> streamByLabel(String label) {
    // Let each constituent reduce the size of the list to sort.
    return Stream.concat(
        compositeContribs.stream()
            .filter(c -> label.equals(c.getInfoLabel())),
        upstreamRegistries.stream()
            .flatMap(registry -> registry.streamByLabel(label)))
        .sorted(DepanFxOrderableContribution.CONTRIB_COMPARE);
  }

  @Override
  public Optional<Contribution> getById(String id) {

    // The local contributions have priority over any upstream registries.
    List<Contribution> idContribs = compositeContribs.stream()
        .filter(c -> id.equals(c.getInfoId()))
        .collect(Collectors.toList());
    if (idContribs.size() > 1) {
      LOG.error("Multiple ({}) contributions for id {}",
          idContribs.size(), id);
    }
    if (!idContribs.isEmpty()) {
      return Optional.of(idContribs.get(0));
    }

    // Try with the upstream registries
    // Since idContribs is empty, just reuse it
    upstreamRegistries.stream()
        .map(registry -> registry.getById(id))
        .flatMap(Optional::stream)
        .forEach(idContribs::add);
    if (idContribs.size() > 1) {
      LOG.error("Multiple upstream ({}) contributions for id {}",
          idContribs.size(), id);
    }
    if (!idContribs.isEmpty()) {
      return Optional.of(idContribs.get(0));
    }
    return Optional.empty();
  }

  @Override
  public Stream<Contribution> streamContributions() {
    return streamComposite()
        .sorted(DepanFxOrderableContribution.CONTRIB_COMPARE);
  }

  private Stream<Contribution> streamComposite() {
    return Stream.concat(
        compositeContribs.stream(),
        upstreamRegistries.stream()
                .flatMap(registry -> registry.streamContributions()));
  }

  private static  <T> List<T> rightSizeClone(List<T> src) {
    int size = src.size();
    if (size > 0) {
      List<T> result = new ArrayList<T>(size);
      result.addAll(src);
      return result;
    }
    // No guidance, so standard ten slots
    return new ArrayList<>();
  }
}
