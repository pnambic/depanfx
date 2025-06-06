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
package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxFxmlDialog;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DepanFxFxmlDialog
@FxmlView("node-views-merge-dialog.fxml")
public class DepanFxNodeViewsMergeDialog {

  private static final Logger LOG = LoggerFactory.getLogger(
      DepanFxNodeViewsMergeDialog.class);

  public static void runMergeDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    LOG.info("merging");
  }
}
