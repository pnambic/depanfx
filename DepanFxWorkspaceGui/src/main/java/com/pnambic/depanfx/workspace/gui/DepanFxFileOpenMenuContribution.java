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
package com.pnambic.depanfx.workspace.gui;

import com.pnambic.depanfx.perspective.plugins.DepanFxResourceRegistry;
import com.pnambic.depanfx.scene.DepanFxSceneService;
import com.pnambic.depanfx.scene.DepanFxSceneViewer;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuContribution;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuItems;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import javafx.event.ActionEvent;

@Component
public class DepanFxFileOpenMenuContribution
    extends DepanFxSceneMenuContribution.Basic<DepanFxWorkspaceViewer> {

  private final DepanFxWorkspace workspace;

  private final DepanFxResourceRegistry openRegistry;

  @Autowired
  public DepanFxFileOpenMenuContribution(
      DepanFxWorkspace workspace,
      DepanFxResourceRegistry openRegistry) {
    super(DepanFxSceneMenuItems.FILE_OPEN_ITEM, DepanFxWorkspaceViewer.class);
    this.workspace = workspace;
    this.openRegistry = openRegistry;
  }

  @Override
  public boolean acceptsMenuItemKey(
      DepanFxSceneService sceneSrvc, String menuItemKey) {
    if (! super.acceptsMenuItemKey(sceneSrvc, menuItemKey)) {
      return false;
    }
    return getDocument(sceneSrvc)
        .map(d -> openRegistry.opensDocument(workspace, d))
        .orElse(false);
  }

  @Override
  public boolean forViewer(DepanFxSceneViewer viewer) {
    if (viewer instanceof DepanFxWorkspaceViewer wksp) {
      return wksp.getCurrentSelection()
          .map(s -> s instanceof DepanFxProjectDocument)
          .orElse(false);
      }
    return false;
  }

  @Override
  public void handleEvent(
      DepanFxSceneService sceneSrvc, ActionEvent event) {
    getDocument(sceneSrvc)
        .ifPresent(d -> openRegistry.openDocument(
            workspace, sceneSrvc, d));
  }

  private Optional<DepanFxProjectDocument> getDocument(
      DepanFxSceneService sceneSrvc) {
    Optional<DepanFxWorkspaceMember> optMember =
        sceneSrvc.getViewer(DepanFxWorkspaceViewer.class)
        .flatMap(v -> v.getCurrentSelection());

    if (optMember.get() instanceof DepanFxProjectDocument document) {
      return Optional.of(document);
    }
    return Optional.empty();
  }
}
