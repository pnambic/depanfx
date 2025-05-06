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
package com.pnambic.depanfx.nodelist.gui.columns.annos;

import com.pnambic.depanfx.graph.nodeanno.DepanFxAnnotationIndexData;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.plugins.DepanFxNewResourceContribution;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

import javafx.scene.control.MenuItem;

@Configuration
public class DepanFxAnnotationConfiguration {

  private static final String ANNOTATION_INDEX_TOOL_NAME =
      "Annotation Index";

  public static final String ANNOTATION_INDEX_TOOL_DESCR =
      "Index of annotation keys to their infos.";

  @Bean
  public DepanFxNewResourceContribution newAnnotationIndex(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    return new NewAnnotationIndexContribution(workspace, dialogRunner);
  }

  private class NewAnnotationIndexContribution
    implements DepanFxNewResourceContribution {

    private final DepanFxWorkspace workspace;

    private final DepanFxDialogRunner dialogRunner;

    public NewAnnotationIndexContribution(
        DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
      this.workspace = workspace;
      this.dialogRunner = dialogRunner;
    }

    @Override
    public MenuItem createNewResourceMenuItem() {
      return DepanFxContextMenuBuilder.createActionItem(
          ANNOTATION_INDEX_TOOL_NAME, e -> runCreateDialog());
    }

    private void runCreateDialog() {
      DepanFxAnnotationIndexData annoData =
          new DepanFxAnnotationIndexData(
              ANNOTATION_INDEX_TOOL_NAME, ANNOTATION_INDEX_TOOL_DESCR,
              Collections.emptyList());
      DepanFxAnnotationIndexToolDialog.runCreateDialog(
          dialogRunner, workspace.addScratchResource(annoData));
    }
  }
}
