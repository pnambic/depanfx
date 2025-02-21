/*
 * Copyright 2024 The Depan Project Authors
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
package com.pnambic.depanfx.session.plugins;

import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.scene.DepanFxSceneService;
import com.pnambic.depanfx.scene.DepanFxSceneViewer;
import com.pnambic.depanfx.session.viewdata.DepanFxBaseViewerData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Registry based serialization support for scene viewers.
 */
@Component
public class DepanFxSceneViewerRegistry {

  public interface Contribution {

    boolean accepts(DepanFxBaseViewerData viewerData);

    boolean accepts(DepanFxSceneViewer viewer);

    Optional<DepanFxSceneViewer> buildViewer(
        DepanFxSceneService sceneSrvc, DepanFxBaseViewerData viewData);

    Optional<DepanFxBaseViewerData> getViewerData(DepanFxSceneViewer viewer);

    void prepareTransport(PersistDocumentTransportBuilder builder);
  }

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxSceneViewerRegistry.class);

  private final List<Contribution> contribs;

  @Autowired
  public DepanFxSceneViewerRegistry(List<Contribution> contribs) {
    this.contribs = contribs;
  }

  public Optional<DepanFxSceneViewer> buildViewer(
      DepanFxSceneService sceneSrvc, DepanFxBaseViewerData viewerData) {
    return lookupContrib(viewerData, "buildViewer")
        .flatMap(c -> c.buildViewer(sceneSrvc, viewerData));
  }

  public Optional<DepanFxBaseViewerData> getViewerData(
      DepanFxSceneViewer viewer) {
    return lookupContrib(viewer, "getViewerData")
        .flatMap(c -> c.getViewerData(viewer));
  }

  public void prepareTransport(PersistDocumentTransportBuilder builder) {
    contribs.forEach(c -> c.prepareTransport(builder));
  }

  private Optional<Contribution> lookupContrib(
      DepanFxBaseViewerData viewerData, String caller) {
    Optional<Contribution> result = contribs.stream()
        .filter(c -> c.accepts(viewerData))
        .findFirst();
    if (result.isEmpty()) {
      LOG.warn("Unexpected viewer {} for {}",
          viewerData.getClass().getName(), caller);
    }
    return result;
  }

  private Optional<Contribution> lookupContrib(
      DepanFxSceneViewer viewer, String caller) {
    Optional<Contribution> result = contribs.stream()
        .filter(c -> c.accepts(viewer))
        .findFirst();
    if (result.isEmpty()) {
      LOG.warn("Missing contributions for {} for {}",
          viewer.getClass().getName(), caller);
    }
    return result;
  }
}
