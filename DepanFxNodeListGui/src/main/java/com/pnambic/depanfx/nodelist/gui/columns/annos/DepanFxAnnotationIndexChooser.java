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
import java.util.function.BiConsumer;

import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;

public class DepanFxAnnotationIndexChooser {

  public static final String SELECT_ANNOTATION_INDEX = "Select Annotation Index...";

  public static final DepanFxResourceFilter ANNOTATION_INDEX_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Annotation Index",
          DepanFxAnnotationIndexData.ANNOTATION_INDEX_TOOL_EXT,
          DepanFxAnnotationIndexData.class);

  /**
   * Bind a pop-up to text field for the resource name.
   */
  public static class AnnotationIndexControl {

    public final DepanFxWorkspace workspace;

    private final DepanFxDialogRunner dialogRunner;

    private DepanFxWorkspaceResource<DepanFxAnnotationIndexData>
        annoIndexRsrc;

    private final TextField annoIndexField;

    public AnnotationIndexControl(
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        TextField annoIndexField) {
      this.workspace = workspace;
      this.dialogRunner = dialogRunner;
      this.annoIndexField = annoIndexField;
      annoIndexField.setContextMenu(buildContextMenu());
    }

    public void setAnnotationIndexRsrc(
        DepanFxWorkspaceResource<DepanFxAnnotationIndexData> annoIndexRsrc) {
      this.annoIndexRsrc = annoIndexRsrc;
      annoIndexField.setText(getAnnotationIndexRsrcName());
    }

    public DepanFxWorkspaceResource<DepanFxAnnotationIndexData>
        getAnnotationIndexResource() {

      return annoIndexRsrc;
    }

    public String getAnnotationIndexRsrcName() {
      if (annoIndexRsrc != null) {
        return annoIndexRsrc.getDocument().getMemberPath().toString();
      }
      // Let the text input field show a prompt text.
      return null;
    }

    public void runAnnotationIndexFinder() {
      DepanFxAnnotationIndexChooser
          .runAnnotationIndexFinder(
                workspace, dialogRunner, annoIndexField.getScene())
          .ifPresent(this::setAnnotationIndexRsrc);
    }

    private ContextMenu buildContextMenu() {
      DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
      builder.appendActionItem(
          SELECT_ANNOTATION_INDEX, e -> runAnnotationIndexFinder());
      return builder.build();
    }
  }

  /**
   * Bind a pop-up to table cell for the resource name.
   */
  public static class AnnotationIndexCell<T>
      extends TableCell<T, String> {

    private final DepanFxWorkspace workspace;

    private final DepanFxDialogRunner dialogRunner;

    private final Scene scene;

    private final BiConsumer<
            T, DepanFxWorkspaceResource<DepanFxAnnotationIndexData>>
        matcherConsumer;

    public AnnotationIndexCell(
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        Scene scene,
        BiConsumer<
                T, DepanFxWorkspaceResource<DepanFxAnnotationIndexData>>
            matcherConsumer) {
      this.workspace = workspace;
      this.dialogRunner = dialogRunner;
      this.scene = scene;
      this.matcherConsumer = matcherConsumer;
    }

    @Override
    protected void updateItem(String displayName, boolean empty) {
      super.updateItem(displayName, empty);

      if (empty) {
        setGraphic(null);
        setContextMenu(null);
        return;
      }
      setText(displayName);
      setContextMenu(buildContextMenu());
    }

    private ContextMenu buildContextMenu() {
      DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
      builder.appendActionItem(
          SELECT_ANNOTATION_INDEX, e -> runAnnotationIndexFinder());
      return builder.build();
    }

    private void runAnnotationIndexFinder() {
      DepanFxAnnotationIndexChooser
          .runAnnotationIndexFinder(workspace, dialogRunner, scene)
          .ifPresent(r -> matcherConsumer.accept(getTableRow().getItem(), r));
    }
  }

  /**
   * Provide an existing annotation index resource.
   */
  public static Optional<DepanFxWorkspaceResource<DepanFxAnnotationIndexData>>
      runAnnotationIndexFinder(
            DepanFxWorkspace workspace,
            DepanFxDialogRunner dialogRunner,
            Scene scene) {

    DepanFxResourceChooser chooser = prepareChooser(workspace, dialogRunner);
    return chooser.showOpenDialog(scene)
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(p -> workspace.getWorkspaceResource(
            p, DepanFxAnnotationIndexData.class));
  }

  private static DepanFxResourceChooser prepareChooser(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    DepanFxResourceChooser result =
        new DepanFxResourceChooser(workspace, dialogRunner);
    DepanFxResourcePerspectives.prepareResourceFinder(
        result, DepanFxProjects.TOOLS_PATH);
    result.getExtensionFilters().add(ANNOTATION_INDEX_FILTER);
    result.setSelectedExtensionFilter(ANNOTATION_INDEX_FILTER);
    return result;
  }
}
