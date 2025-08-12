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
package com.pnambic.depanfx.edgematchers.gui;

import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcherSequenceDocument;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceChooser;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.Optional;

import javafx.scene.Scene;

public class DepanFxLinkMatcherSequenceChooser {

  /**
   * Provide an existing link display document.
   *
   * The layout resource is an anonymous object that various layout
   * processes will cast to the required type.
   */
  public static Optional<DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument>>
      runChooser(
            DepanFxWorkspace workspace,
            DepanFxDialogRunner dialogRunner, Scene scene) {

    DepanFxResourceChooser rsrcChooser =
        new DepanFxResourceChooser(workspace, dialogRunner);

    DepanFxResourcePerspectives.prepareResourceFinder(
        rsrcChooser, DepanFxLinkMatcherSequenceDocument.LINK_MATCHER_SEQUENCE_TOOL_PATH);

    rsrcChooser.getExtensionFilters().add(
        DepanFxLinkMatcherSequenceToolDialog.LINK_MATCHER_SEQUENCE_RSRC_FILTER);
    rsrcChooser.setSelectedExtensionFilter(
        DepanFxLinkMatcherSequenceToolDialog.LINK_MATCHER_SEQUENCE_RSRC_FILTER);

    return rsrcChooser.showOpenDialog(scene)
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(p -> workspace.getWorkspaceResource(
            p, DepanFxLinkMatcherSequenceDocument.class));
  }
}
