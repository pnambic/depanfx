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
import com.pnambic.depanfx.graph.nodeanno.DepanFxAnnotationIndexData.AnnotationSpecification;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListChooser;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListData;
import com.pnambic.depanfx.perspective.DepanFxBaseToolDialog;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxActionTableCell;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxFxmlDialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.scene.DepanFxTableColumnBinder;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.StringConverter;

@DepanFxFxmlDialog
@FxmlView("annotation-index-tool-dialog.fxml")
public class DepanFxAnnotationIndexToolDialog
    extends DepanFxBaseToolDialog<DepanFxAnnotationIndexData>{

  public static final String SELECT_INFO = "Select Info";

  // Annotation definitions probably belong somewhere else
  public static final String ANNOTATIONS_TOOL_DIR = "Annotations";

  public static final Path ANNOTATION_TOOL_PATH =
      DepanFxNodeListData.NODE_LIST_TOOL_PATH.resolve(ANNOTATIONS_TOOL_DIR);

  public static final ExtensionFilter ANNOTATION_INDEX_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Annotation Index",
          DepanFxAnnotationIndexData.ANNOTATION_INDEX_TOOL_EXT);

  public static final DepanFxResourceFilter ANNOTATION_INDEX_RSRC_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Annotation Index",
          DepanFxAnnotationIndexData.ANNOTATION_INDEX_TOOL_EXT,
          DepanFxAnnotationIndexData.class);

  private static final String CREATE_ANNOTATION_INDEX =
      "Create Annotation Index";

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxInfoRegistry infoRegistry;

  @FXML
  private TableView<EditAnnotationSpec> annotationsTable;

  @Autowired
  public DepanFxAnnotationIndexToolDialog(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxInfoRegistry infoRegistry) {
    super(workspace, DepanFxAnnotationIndexData.class);
    this.dialogRunner = dialogRunner;
    this.infoRegistry = infoRegistry;
  }

  public static Dialog<DepanFxAnnotationIndexToolDialog> runCreateDialog(
      DepanFxDialogRunner dialogRunner,
      DepanFxWorkspaceResource<DepanFxAnnotationIndexData> annoRsrc) {
    Dialog<DepanFxAnnotationIndexToolDialog> result =
        DepanFxResourcePerspectives.prepareDialog(
            annoRsrc, dialogRunner,
            DepanFxAnnotationIndexToolDialog.class);
    result.getController().setToolResource(annoRsrc);
    result.runDialog(CREATE_ANNOTATION_INDEX);
    return result;
  }

  @Override
  public void setToolResource(
      DepanFxWorkspaceResource<DepanFxAnnotationIndexData> toolRsrc) {
    super.setToolResource(toolRsrc);
    List<EditAnnotationSpec> editAnnos =
        toolRsrc.getResource().streamAnnotations()
          .map(a -> new EditAnnotationSpec(
              a.getAnnotationLabel(),
              a.getAnnotationKey(),
              a.getAnnotationInfo()))
          .collect(Collectors.toList());
    annotationsTable.setItems(FXCollections.observableList(editAnnos));
  }

  @FXML
  public void initialize() {
    annotationsTable.setContextMenu(buildAnnoIndexTableMenu());

    DepanFxTableColumnBinder<EditAnnotationSpec> columnBinder =
        new DepanFxTableColumnBinder<>(annotationsTable);

    TableColumn<EditAnnotationSpec, String> labelColumn =
        columnBinder.bind("annotationLabel");
    labelColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    labelColumn.setOnEditCommit(this::onUpdateLabelEvent);

    TableColumn<EditAnnotationSpec, String> keyColumn =
        columnBinder.bind("annotationKey");
    keyColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    keyColumn.setOnEditCommit(this::onUpdateKeyEvent);

    TableColumn<EditAnnotationSpec, DepanFxInfoRegistry.Contribution> infoColumn =
        columnBinder.bind("annotationInfo");
    infoColumn.setCellValueFactory(f -> f.getValue().annotationInfoProperty());
    infoColumn.setCellFactory(
        ComboBoxTableCell.forTableColumn(new InfoConverter(), getInfos()));
    infoColumn.setOnEditCommit(this::onUpdateInfoEvent);

    TableColumn<EditAnnotationSpec, String> rowActionColumn =
        columnBinder.next();
    DepanFxActionTableCell.prepareColumn(
        rowActionColumn, p -> new AnnotationActions());

    // Size filePath to remaining room
    keyColumn.prefWidthProperty().bind(
        annotationsTable.widthProperty()
            .subtract(labelColumn.widthProperty())
            .subtract(infoColumn.widthProperty())
            .subtract(rowActionColumn.widthProperty())
            .subtract(2));
  }

  @FXML
  public void addAnnotationRow() {
    addNewAnnotation();
  }

  @Override
  protected DepanFxAnnotationIndexData prepareResult() {
    return new DepanFxAnnotationIndexData(
        getToolName(), getToolDescription(), buildAnnotations());
  }

  @Override
  protected void setTooldataFilters(FileChooser result) {
    result.getExtensionFilters().add(ANNOTATION_INDEX_FILTER);
    result.setSelectedExtensionFilter(ANNOTATION_INDEX_FILTER);
  }

  @Override
  protected File buildInitialDestinationFile() {
    return buildToolInitialDestination(
        DepanFxAnnotationIndexData.ANNOTATION_INDEX_TOOL_EXT,
        ANNOTATION_TOOL_PATH);
  }

  @Override
  protected String getInputCheckFailureText() {
    return  "Annotation Index Save Confirmation Error";
  }

  private ObservableList<DepanFxInfoRegistry.Contribution> getInfos() {
    return FXCollections.observableArrayList(
        infoRegistry.streamContributions().collect(Collectors.toList()));
  }

  private ContextMenu buildAnnoIndexTableMenu() {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem("New Annotation", this::onNewAnnotation);
    return builder.build();
  }

  private void onNewAnnotation(ActionEvent actionevent1) {
    addNewAnnotation();
  }

  private void onUpdateLabelEvent(
      CellEditEvent<EditAnnotationSpec, String> updateEvent) {
    getEventAnnotation(updateEvent)
        .annoLabelProp.set(updateEvent.getNewValue());
  }

  private void onUpdateKeyEvent(
      CellEditEvent<EditAnnotationSpec, String> updateEvent) {
    getEventAnnotation(updateEvent)
        .annoKeyProp.set(updateEvent.getNewValue());
  }

  private void onUpdateInfoEvent(
      CellEditEvent<EditAnnotationSpec, DepanFxInfoRegistry.Contribution> updateEvent) {
    getEventAnnotation(updateEvent)
        .annoInfoProp.set(updateEvent.getNewValue());
  }

  private void addNewAnnotation() {
    annotationsTable.getItems().add(new EditAnnotationSpec(
        "Annotation", "annotation", null));
  }

  private EditAnnotationSpec getEventAnnotation(
      CellEditEvent<EditAnnotationSpec, ?> updateEvent) {
    return updateEvent.getTableView().getItems().get(
        updateEvent.getTablePosition().getRow());
  }

  /**
   * Transform to a serializable type.
   */
  private Collection<AnnotationSpecification> buildAnnotations() {
    return annotationsTable.getItems().stream()
        .map(e -> new AnnotationSpecification(
            e.annoLabelProp.getValue(),
            e.annoKeyProp.getValue(),
            e.annoInfoProp.getValue()))
        .collect(Collectors.toList());
  }

  private class AnnotationActions
      extends DepanFxActionTableCell<EditAnnotationSpec> {

    public AnnotationActions() {
      super(annotationsTable.getItems());
    }

    @Override
    protected void populateContextMenu(DepanFxContextMenuBuilder builder) {
      builder.appendActionItem(SELECT_INFO,
          e -> runInfoChooser(getIndex()));
    }

    private void runInfoChooser(int index) {
      DepanFxNodeListChooser.runNodeListChooser(
          workspace, dialogRunner, getScene())
      .ifPresent(r -> updateCellResource(index, r));
    }

    private void updateCellResource(
        int cellIndex,
        DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc) {
    }
  }

  private class InfoConverter
      extends StringConverter<DepanFxInfoRegistry.Contribution> {

    @Override
    public String toString(DepanFxInfoRegistry.Contribution contribution) {
      if (contribution != null) {
        return contribution.getInfoLabel();
      }
      return "";
    }

    @Override
    public DepanFxInfoRegistry.Contribution fromString(String label) {
      return infoRegistry.streamByLabel(label)
          .findFirst()
          .orElse(null);
    }
  }

  public static class EditAnnotationSpec {

    public StringProperty annoLabelProp;

    public StringProperty annoKeyProp;

    public SimpleObjectProperty<DepanFxInfoRegistry.Contribution> annoInfoProp;

    public EditAnnotationSpec(
        String annoLabel, String annoKey,
        DepanFxInfoRegistry.Contribution annoInfo) {
      this.annoLabelProp = new SimpleStringProperty(annoLabel);
      this.annoKeyProp = new SimpleStringProperty(annoKey);
      this.annoInfoProp =
          new SimpleObjectProperty<DepanFxInfoRegistry.Contribution>(annoInfo);
    }

    public StringProperty annotationLabelProperty() {
      return annoLabelProp;
    }

    public StringProperty annotationKeyProperty() {
      return annoKeyProp;
    }

    public ObservableValue<DepanFxInfoRegistry.Contribution> annotationInfoProperty() {
      return annoInfoProp;
    }
  }
}
