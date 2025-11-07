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
package com.pnambic.depanfx.nodelist.viewer;

import com.pnambic.depanfx.edgematchers.link.DepanFxLinkMatchersRegistry;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry;
import com.pnambic.depanfx.nodefilters.gui.DepanFxNodeFiltersDialogRegistry;
import com.pnambic.depanfx.nodefilters.model.DepanFxNodeFiltersRegistry;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxColumnRegistry;
import com.pnambic.depanfx.nodelist.gui.sections.folds.NodeListFoldController;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeFoldController;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.viewdata.DepanFxNodeListViewerData;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneService;
import com.pnambic.depanfx.scene.DepanFxSceneViewer;
import com.pnambic.depanfx.session.plugins.DepanFxSceneViewerRegistry;
import com.pnambic.depanfx.session.viewdata.DepanFxBaseViewerData;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Define how the welcome view is serialized.
 */
@Component
public class DepanFxNodeListViewerContribution
    implements DepanFxSceneViewerRegistry.Contribution {

  private static final Class<?>[] ALLOWED_TYPES = new Class<?>[] {
    DepanFxNodeListViewerData.class
  };

  private final DepanFxWorkspace workspace;

  private final DepanFxColumnRegistry columnRegistry;

  private final DepanFxInfoRegistry infoRegistry;

  private final DepanFxLinkMatchersRegistry matcherRegistry;

  private final DepanFxNodeFiltersRegistry filterRegistry;

  private final DepanFxNodeFiltersDialogRegistry filterDialogRegistry;

  @Autowired
  private DepanFxNodeListViewerContribution(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxColumnRegistry columnRegistry,
      DepanFxInfoRegistry infoRegistry,
      DepanFxLinkMatchersRegistry matcherRegistry,
      DepanFxNodeFiltersRegistry filterRegistry,
      DepanFxNodeFiltersDialogRegistry filterDialogRegistry) {
    this.workspace = workspace;
    this.columnRegistry = columnRegistry;
    this.infoRegistry = infoRegistry;
    this.matcherRegistry = matcherRegistry;
    this.filterRegistry = filterRegistry;
    this.filterDialogRegistry = filterDialogRegistry;
  }

  @Override
  public boolean accepts(DepanFxBaseViewerData viewerData) {
    return DepanFxNodeListViewerData.class.isAssignableFrom(
        viewerData.getClass());
  }

  @Override
  public boolean accepts(DepanFxSceneViewer viewer) {
    return DepanFxNodeListViewer.class.isAssignableFrom(
        viewer.getClass());
  }

  @Override
  public Optional<DepanFxSceneViewer> buildViewer(
      DepanFxSceneService sceneService, DepanFxBaseViewerData baseData) {

    DepanFxNodeListViewerData viewerData = (DepanFxNodeListViewerData) baseData;
    DepanFxWorkspaceResource<DepanFxNodeList> viewListRsrc =
        viewerData.getNodeListRsrc();
    DepanFxNodeFoldController nodeFolding =
        new NodeListFoldController(
            workspace, viewListRsrc.getResource().getGraphDocResource());
    return Optional.of(new DepanFxNodeListViewer(
        viewerData.getViewerTitle(),
        workspace, sceneService.getDialogRunner(),
        columnRegistry, infoRegistry, matcherRegistry,
        filterRegistry, filterDialogRegistry, nodeFolding,
        viewListRsrc, viewerData.getTableViewRsrc()));
  }

  @Override
  public Optional<DepanFxBaseViewerData> getViewerData(
      DepanFxSceneViewer viewer) {
    DepanFxNodeListViewer listViewer = (DepanFxNodeListViewer) viewer;

    DepanFxBaseViewerData result = new DepanFxNodeListViewerData(
        listViewer.getViewerTitle(),
        listViewer.getNodeListResource(),
        listViewer.getTableViewResource());
    return Optional.of(result);
  }

  @Override
  public void prepareTransport(PersistDocumentTransportBuilder builder) {
    builder.addAllowedType(ALLOWED_TYPES);
  }
}
