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

import com.pnambic.depanfx.perspective.plugins.DepanFxResourceOpenRegistry;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneService;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuContribution;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuItems;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;

@Component
public class DepanFxFileOpenMenuContribution
    implements DepanFxSceneMenuContribution {

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxResourceOpenRegistry openRegistry;

  @Autowired
  public DepanFxFileOpenMenuContribution(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxResourceOpenRegistry openRegistry) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
    this.openRegistry = openRegistry;
  }

  @Override
  public boolean acceptsEvent(
      DepanFxSceneService sceneSrvc, ActionEvent event) {
    MenuItem item = (MenuItem) event.getSource();
    if ( ! item.idProperty().getValue().equals(
        DepanFxSceneMenuItems.FILE_OPEN_ITEM)) {
      return false;
    }
    Optional<DepanFxWorkspaceViewer> optWkspViewer =
        sceneSrvc.getViewer(DepanFxWorkspaceViewer.class);
    if (optWkspViewer.isEmpty()) {
      return false;
    }
    DepanFxWorkspaceViewer wkspViewer = optWkspViewer.get();
    Optional<DepanFxWorkspaceMember> optMember =
        wkspViewer.getCurrentSelection();
    if (optMember.isEmpty()) {
      return false;
    }
    return optMember.get() instanceof DepanFxProjectDocument;
  }

  @Override
  public void handleEvent(
      DepanFxSceneService sceneSrvc, ActionEvent event) {
    Optional<DepanFxWorkspaceMember> optMember =
        sceneSrvc.getViewer(DepanFxWorkspaceViewer.class)
        .flatMap(v -> v.getCurrentSelection());

    if (optMember.get() instanceof DepanFxProjectDocument document) {
      openRegistry.openDocument(workspace, dialogRunner, document);
    }
  }
}
