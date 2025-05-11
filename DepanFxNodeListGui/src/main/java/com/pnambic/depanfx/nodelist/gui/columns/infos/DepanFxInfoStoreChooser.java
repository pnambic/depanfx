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
package com.pnambic.depanfx.nodelist.gui.columns.infos;

import com.pnambic.depanfx.nodelist.tooldata.DepanFxInfoStoreData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceChooser;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.util.Optional;

import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;

public class DepanFxInfoStoreChooser {

  public static final String SELECT_INFO_STORE = "Select Info Source...";

  public static final DepanFxResourceFilter INFO_STORE_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Info Store",
          DepanFxInfoStoreData.INFO_STORE_TOOL_EXT,
          DepanFxInfoStoreData.class);

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private final TextField infoStoreField;

  private DepanFxWorkspaceResource<DepanFxInfoStoreData> infoStoreRsrc;

  public DepanFxInfoStoreChooser(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      TextField infoStoreField) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
    this.infoStoreField = infoStoreField;

    infoStoreField.setContextMenu(buildContextMenu());
  }

  public DepanFxWorkspaceResource<DepanFxInfoStoreData> getStoreResource() {
    return infoStoreRsrc;
  }

  public void setInfoStoreResource(
      DepanFxWorkspaceResource<DepanFxInfoStoreData> infoStoreRsrc) {
    this.infoStoreRsrc = infoStoreRsrc;
    infoStoreField.setText(getInfoStoreRsrcName());
  }

  public Optional<DepanFxWorkspaceResource<DepanFxInfoStoreData>>
  runInfoStoreFinder() {
    Optional<DepanFxWorkspaceResource<DepanFxInfoStoreData>> result =
        runInfoStoreFinder(workspace, dialogRunner, infoStoreField.getScene());
    result.ifPresent(this::setInfoStoreResource);
    return result;
  }

  private String getInfoStoreRsrcName() {
    if (infoStoreRsrc != null) {
      return DepanFxProjects.getDocumentLabel(infoStoreRsrc.getDocument());
    }
    // Let the text input field show a prompt text.
    return null;
  }

  private ContextMenu buildContextMenu() {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem(
        SELECT_INFO_STORE, e -> runInfoStoreFinder());
    return builder.build();
  }

  private static Optional<DepanFxWorkspaceResource<DepanFxInfoStoreData>>
  runInfoStoreFinder(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      Scene scene) {

   DepanFxResourceChooser chooser = prepareChooser(workspace, dialogRunner);
   return chooser.showOpenDialog(scene)
       .map(DepanFxProjectDocument.class::cast)
       .flatMap(p -> workspace.getWorkspaceResource(
           p, DepanFxInfoStoreData.class));
  }

  private static DepanFxResourceChooser prepareChooser(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    DepanFxResourceChooser result =
        new DepanFxResourceChooser(workspace, dialogRunner);
    DepanFxResourcePerspectives.prepareResourceFinder(
        result, DepanFxProjects.TOOLS_PATH);
    result.getExtensionFilters().add(INFO_STORE_FILTER);
    result.setSelectedExtensionFilter(INFO_STORE_FILTER);
    return result;
  }
}
