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
package com.pnambic.depanfx.workspace.gui;

import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceMenuRegistry;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceRegistry;
import com.pnambic.depanfx.scene.DepanFxSceneService;
import com.pnambic.depanfx.scene.DepanFxSceneViewer;
import com.pnambic.depanfx.session.plugins.DepanFxSceneViewerRegistry;
import com.pnambic.depanfx.session.viewdata.DepanFxBaseViewerData;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.viewdata.DepanFxWorkspaceViewerData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

/**
 * Define how the welcome view is serialized.
 */
@Component
public class DepanFxWorkspaceViewerContribution
    implements DepanFxSceneViewerRegistry.Contribution {

  private static final Class<?>[] ALLOWED_TYPES = new Class<?>[] {
    DepanFxWorkspaceViewerData.class
  };

  private final DepanFxWorkspace workspace;

  private final DepanFxResourceRegistry rsrcRegistry;

  private final DepanFxResourceMenuRegistry rsrcMenuRegistry;

  @Autowired
  private DepanFxWorkspaceViewerContribution(
      DepanFxWorkspace workspace,
      DepanFxResourceRegistry rsrcRegistry,
      DepanFxResourceMenuRegistry rsrcMenuRegistry) {
    this.workspace = workspace;
    this.rsrcRegistry = rsrcRegistry;
    this.rsrcMenuRegistry = rsrcMenuRegistry;
  }

  @Override
  public boolean accepts(DepanFxBaseViewerData viewerData) {
    return DepanFxWorkspaceViewerData.class.isAssignableFrom(
        viewerData.getClass());
  }

  @Override
  public boolean accepts(DepanFxSceneViewer viewer) {
    return DepanFxWorkspaceViewer.class.isAssignableFrom(
        viewer.getClass());
  }

  @Override
  public Optional<DepanFxSceneViewer> buildViewer(
      DepanFxSceneService sceneSrvc, DepanFxBaseViewerData viewData) {

    return Optional.of(new DepanFxWorkspaceViewer(
        workspace,
        rsrcRegistry, rsrcMenuRegistry, Collections.emptyMap(),
        DepanFxWorkspaceViewer.WORKSPACE_TAB));
  }

  @Override
  public Optional<DepanFxBaseViewerData> getViewerData(
      DepanFxSceneViewer viewer) {
    return Optional.of(DepanFxWorkspaceViewerData.MARKER);
  }

  @Override
  public void prepareTransport(PersistDocumentTransportBuilder builder) {
    builder.addAllowedType(ALLOWED_TYPES);
  }
}
