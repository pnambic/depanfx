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

import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewNodeDisplayData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceChooser;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.Collections;
import java.util.Optional;

import javafx.scene.Scene;

public class DepanFxNodeDisplayDataChooser {

  public static final DepanFxResourceFilter NODE_DISPLAY_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Node Display",
          DepanFxNodeViewNodeDisplayData.NODE_VIEW_NODE_DISPLAY_EXT,
          DepanFxNodeViewNodeDisplayData.class);

  /**
   * Provide an existing link display document.
   */
  public static Optional<DepanFxWorkspaceResource<DepanFxNodeViewNodeDisplayData>>
      runNodeDisplayFinder(
            DepanFxWorkspace workspace,
            DepanFxDialogRunner dialogRunner, Scene scene) {

    DepanFxResourceChooser rsrcChooser =
        prepareChooser(workspace, dialogRunner);
    return rsrcChooser.showOpenDialog(scene)
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(p -> workspace.getWorkspaceResource(
            p, DepanFxNodeViewNodeDisplayData.class, Collections.emptyMap()));
  }

  private static DepanFxResourceChooser prepareChooser(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    DepanFxResourceChooser result =
        new DepanFxResourceChooser(workspace, dialogRunner);
    DepanFxResourcePerspectives.prepareResourceFinder(
        result, DepanFxNodeViewNodeDisplayData.NODE_DISPLAY_TOOL_PATH);
    result.getExtensionFilters().add(NODE_DISPLAY_FILTER);
    result.setSelectedExtensionFilter(NODE_DISPLAY_FILTER);
    return result;
  }
}
