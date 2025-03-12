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
package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry;
import com.pnambic.depanfx.nodefilters.model.DepanFxNodeFiltersRegistry;
import com.pnambic.depanfx.nodeview.layouts.DepanFxNodeLayoutRegistry;
import com.pnambic.depanfx.nodeview.viewdata.DepanFxNodeViewPanelData;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.scene.DepanFxSceneService;
import com.pnambic.depanfx.scene.DepanFxSceneViewer;
import com.pnambic.depanfx.session.plugins.DepanFxSceneViewerRegistry;
import com.pnambic.depanfx.session.viewdata.DepanFxBaseViewerData;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Define how the node view panel is serialized.
 */
@Component
public class DepanFxNodeViewPanelContribution
    implements DepanFxSceneViewerRegistry.Contribution {

  private static final Class<?>[] ALLOWED_TYPES = new Class<?>[] {
    DepanFxNodeViewPanelData.class
  };

  private final DepanFxWorkspace workspace;

  private final DepanFxNodeLayoutRegistry layoutRegistry;

  private final DepanFxNodeFiltersRegistry filterRegistry;

  private final DepanFxInfoRegistry infoRegistry;

  @Autowired
  private DepanFxNodeViewPanelContribution(
      DepanFxWorkspace workspace,
    DepanFxNodeLayoutRegistry layoutRegistry,
    DepanFxNodeFiltersRegistry filterRegistry,
    DepanFxInfoRegistry infoRegistry) {
    this.workspace = workspace;
    this.layoutRegistry = layoutRegistry;
    this.filterRegistry = filterRegistry;
    this.infoRegistry = infoRegistry;
  }

  @Override
  public boolean accepts(DepanFxBaseViewerData viewerData) {
    return DepanFxNodeViewPanelData.class.isAssignableFrom(
        viewerData.getClass());
  }

  @Override
  public boolean accepts(DepanFxSceneViewer viewer) {
    return DepanFxNodeViewPanel.class.isAssignableFrom(
        viewer.getClass());
  }

  @Override
  public Optional<DepanFxSceneViewer> buildViewer(
      DepanFxSceneService sceneSrvc, DepanFxBaseViewerData baseData) {

    DepanFxNodeViewPanelData viewerData = (DepanFxNodeViewPanelData) baseData;
    return Optional.of(new DepanFxNodeViewPanel(
        workspace, layoutRegistry, filterRegistry, infoRegistry,
        viewerData.getNodeViewRsrc()));
  }

  @Override
  public Optional<DepanFxBaseViewerData> getViewerData(
      DepanFxSceneViewer viewer) {
    DepanFxNodeViewPanel listViewer = (DepanFxNodeViewPanel) viewer;

    return Optional.of(
        new DepanFxNodeViewPanelData(listViewer.getViewDataRsrc()));
  }

  @Override
  public void prepareTransport(PersistDocumentTransportBuilder builder) {
    builder.addAllowedType(ALLOWED_TYPES);
  }
}
