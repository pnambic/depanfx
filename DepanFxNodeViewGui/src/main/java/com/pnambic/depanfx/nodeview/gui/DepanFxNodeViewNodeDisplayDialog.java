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

import com.pnambic.depanfx.nodefilters.gui.DepanFxNodeFiltersChooser;
import com.pnambic.depanfx.nodefilters.gui.DepanFxNodeFiltersDialogRegistry;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodeview.jogl.JoglColors;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewNodeDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewNodeDisplayData.NodeDisplayEntry;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxSizerModel;
import com.pnambic.depanfx.perspective.DepanFxBaseToolDialog;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.scene.DepanFxTableColumnBinder;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

@Component
@FxmlView("node-view-node-display-dialog.fxml")
public class DepanFxNodeViewNodeDisplayDialog
    extends DepanFxBaseToolDialog<DepanFxNodeViewNodeDisplayData> {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeViewNodeDisplayDialog.class);

  public static final String EDIT_NODE_DISPLAY = "Edit Node Display...";

  public static final String NEW_NODE_DISPLAY = "New Node Display...";

  public static final ExtensionFilter NODE_VIEW_NODE_DISPLAY_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Node Display",
          DepanFxNodeViewNodeDisplayData.NODE_VIEW_NODE_DISPLAY_EXT);

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxNodeFiltersDialogRegistry filtersRegistry;

  @FXML
  private TableView<EditNodeDisplay> nodesDisplayTable;

  private ObservableList<EditNodeDisplay> nodesDisplayData;

  /**
   * Source of non-mutated data (e.g. context model)
   */
  private DepanFxNodeViewNodeDisplayData sourceDisplayData;

  /**
   * Where live changes happen.
   */

  private NodeDisplayController displayControl;

  public DepanFxNodeViewNodeDisplayDialog(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxNodeFiltersDialogRegistry filtersRegistry) {
    super(workspace, DepanFxNodeViewNodeDisplayData.class);
    this.dialogRunner = dialogRunner;
    this.filtersRegistry = filtersRegistry;
  }

  /**
   * Node Display Editor is a modeless dialog coupled to the graph view.
   */
  public static Stage runEditDialog(
      NodeDisplayController displayControl,
      DepanFxDialogRunner dialogRunner) {
    DepanFxProjectDocument projDoc =
        displayControl.getNodeDisplayResource().getDocument();

    Dialog<DepanFxNodeViewNodeDisplayDialog> dlg =
        DepanFxResourcePerspectives.prepareDialog(
            displayControl.getNodeDisplayInfo(), dialogRunner,
            DepanFxNodeViewNodeDisplayDialog.class);

    dlg.getController().setNodeDisplayController(displayControl);
    dlg.getController().setDestination(projDoc);
    return dlg.runModeless(EDIT_NODE_DISPLAY);
  }

  public static void setNodeViewNodeDisplayTooldataFilters(
      FileChooser chooser) {
    chooser.getExtensionFilters().add(NODE_VIEW_NODE_DISPLAY_FILTER);
    chooser.setSelectedExtensionFilter(NODE_VIEW_NODE_DISPLAY_FILTER);
  }

  @FXML
  @SuppressWarnings("unused")
  public void initialize() {
    DepanFxTableColumnBinder<EditNodeDisplay> columnBinder =
        new DepanFxTableColumnBinder<>(nodesDisplayTable);

    TableColumn<EditNodeDisplay, String> labelColumn =
        columnBinder.bind("nodeDisplayLabel");
    labelColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    labelColumn.setOnEditCommit(this::onUpdateLabelEvent);

    TableColumn<EditNodeDisplay, String> filterPathColumn =
        columnBinder.bind("displayFilterName");

    TableColumn<EditNodeDisplay, Number> countColumn =
        columnBinder.next();
    countColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
    countColumn.setCellValueFactory(
        r -> new SimpleIntegerProperty(
            displayControl.getDisplayFilterNodeCount(
                r.getValue().getFilterData())));

    TableColumn<EditNodeDisplay, Color> nodeColorColumn =
        columnBinder.bind("nodeColor");
    nodeColorColumn.setEditable(true);
    nodeColorColumn.setCellFactory(column -> new ColorCellFactory());

    TableColumn<EditNodeDisplay, String> rowActionColumn =
        columnBinder.next();
    rowActionColumn.setCellFactory(p -> new ActionCell());
  }

  /**
   * Both tooldata and display control  are required to populate the display table.
   */
  public void setNodeDisplayController(NodeDisplayController displayControl) {
    this.displayControl = displayControl;
    prepareDisplayTable();
  }

  /**
   * Both view panel and tooldata are required to populate the display table.
   */
  @Override // DepanFxBaseToolDialog
  public void setTooldata(DepanFxNodeViewNodeDisplayData nodeDisplayData) {
    super.setTooldata(nodeDisplayData);
    this.sourceDisplayData = nodeDisplayData;
    prepareDisplayTable();
  }

  private void prepareDisplayTable() {
    // Wait for both to be configured.
    if ((displayControl == null) || (sourceDisplayData == null)) {
      return;
    }
    List<EditNodeDisplay> editNodeDisplay =
        sourceDisplayData.streamNodeDisplay()
            .map(e -> new EditNodeDisplay(displayControl, e))
            .collect(Collectors.toList());

    nodesDisplayData = FXCollections.observableArrayList(editNodeDisplay);
    nodesDisplayTable.setItems(nodesDisplayData);
  }

  @FXML
  private void addNodeDisplayRow() {
    DepanFxNodeFiltersChooser
        .runNodeFiltersFinder(workspace, dialogRunner, getScene(), filtersRegistry)
        .map(this::buildDisplay)
        .map(d -> new EditNodeDisplay(displayControl, d))
        .ifPresent(nodesDisplayData::add);
  }

  private NodeDisplayEntry buildDisplay(
      DepanFxWorkspaceResource<DepanFxBaseFilterData> filterRsrc) {
    DepanFxNodeDisplayData nodeDisplay =
        DepanFxNodeDisplayData.buildSimpleNodeDisplayData();
    String entryName = filterRsrc.getResource().getToolName();
    return new NodeDisplayEntry(entryName, filterRsrc, nodeDisplay);
  }

  private void onUpdateLabelEvent(
      CellEditEvent<EditNodeDisplay, String> updateEvent) {
    getEventNodeDisplay(updateEvent)
        .nodeDisplayLabelProp.set(updateEvent.getNewValue());
  }

  /////////////////////////////////////
  // Tool Dialog protected overrides

  @Override
  protected DepanFxNodeViewNodeDisplayData prepareResult() {
    List<NodeDisplayEntry> displayEntries = nodesDisplayData.stream()
        .map(e -> toNodeDisplayEntry(e))
        .collect(Collectors.toList());

    return new DepanFxNodeViewNodeDisplayData(
            getToolName(), getToolDescription(),
            sourceDisplayData.getContextModelId(), displayEntries);
  }

  @Override
  protected File buildInitialDestinationFile() {
    return buildToolInitialDestination(
        DepanFxNodeViewNodeDisplayData.NODE_VIEW_NODE_DISPLAY_EXT,
        DepanFxNodeViewNodeDisplayData.NODE_DISPLAY_PATH);
  }

  @Override
  protected void setTooldataFilters(FileChooser result) {
    DepanFxNodeViewNodeDisplayDialog
      .setNodeViewNodeDisplayTooldataFilters(result);
  }

  @Override
  protected String getInputCheckFailureText() {
    return  "Node Display Save Confirmation Error";
  }

  private EditNodeDisplay getEventNodeDisplay(
      CellEditEvent<EditNodeDisplay, ?> updateEvent) {
    return updateEvent.getTableView().getItems().get(
        updateEvent.getTablePosition().getRow());
  }

  /////////////////////////////////////
  // FXML handlers.

  @FXML
  protected void handleRevert() {
    closeDialog();
    displayControl.revertNodeDisplay();
  }

  @FXML
  protected void handleApply() {
    DepanFxNodeViewNodeDisplayData toolData = prepareResult();
    displayControl.setNodeDisplayInfo(toolData);
  }

  @Override
  @FXML
  protected void handleConfirm() {
    super.handleConfirm();
    getWorkspaceResource().ifPresent(displayControl::setNodeDisplayResource);
  }

  /////////////////////////////////////
  // Internal Table Classes

  public class ActionCell
      extends TableCell<EditNodeDisplay, String> {

    @Override
    protected void updateItem(String item, boolean empty) {
      super.updateItem(item, empty);

      if (!empty) {
        setText("...");
        setGraphic(null);
        stylizeCell();
        return;
      }
      setText(null);
      setGraphic(null);
    }

    private void stylizeCell() {
      DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
      builder.appendActionItem("Select Filter...",
          e -> runFilterChooser(getIndex()));
      appendMoveOps(builder);
      builder.appendSeparator();
      builder.appendActionItem("Delete",
          e -> deleteFilter(getIndex()));
      setContextMenu(builder.build());
    }

    private void appendMoveOps(DepanFxContextMenuBuilder builder) {
      int index = getIndex();
      boolean hasUp = index > 0;
      boolean hasDown = index < nodesDisplayData.size() - 1;
      if (!hasUp && !hasDown ) {
        return;
      }
      builder.appendSeparator();
      if (hasUp) {
        builder.appendActionItem("Up", e -> moveFilter(getIndex(), -1));
      }
      if (hasDown) {
        builder.appendActionItem("Down", e -> moveFilter(getIndex(), 1));
      }
    }
  }

  private void runFilterChooser(int index) {
    DepanFxNodeFiltersChooser.runNodeFiltersFinder(
        workspace, dialogRunner, getScene(), filtersRegistry)
    .ifPresent(r -> updateFilter(index, r));
  }

  private void moveFilter(int srcIndex, int moveBy) {

    int dstIndex = srcIndex + moveBy;
    if (dstIndex < 0 || dstIndex >= nodesDisplayData.size()) {
      LOG.error("Bad filter move to {} from {} by {}",
          dstIndex, srcIndex, moveBy);
      return;
    }
    EditNodeDisplay moveItem = nodesDisplayData.remove(srcIndex);
    nodesDisplayData.add(dstIndex, moveItem);
  }

  private void deleteFilter(int index) {
    nodesDisplayData.remove(index);
  }

  private void updateFilter(
      int index, DepanFxWorkspaceResource<DepanFxBaseFilterData> filterRsrc) {
    nodesDisplayData.get(index).setDisplayFilterRsrc(filterRsrc);
  }

  private static class ColorCellFactory
      extends TableCell<EditNodeDisplay, Color> {

    private final ColorPicker colorPicker = new ColorPicker();

    @Override
    protected void updateItem(Color color, boolean empty) {
      super.updateItem(color, empty);

      if (empty) {
        setGraphic(null);
        return;
      }
      colorPicker.setValue(color != null ? color : Color.WHITE);
      setGraphic(colorPicker);
      colorPicker.setOnAction(event -> {
        EditNodeDisplay nodeDisplay = getTableRow().getItem();
        nodeDisplay.nodeColorProp.setValue(colorPicker.getValue());
      });
    }
  }

  private static class NodeDisplayUpdater implements ChangeListener<Object> {

    private final NodeDisplayController displayControl;

    private final EditNodeDisplay nodeDisplay;

    public NodeDisplayUpdater(
        NodeDisplayController displayControl, EditNodeDisplay nodeDisplay) {
      this.displayControl = displayControl;
      this.nodeDisplay = nodeDisplay;
    }

    @Override
    public void changed(
        ObservableValue<? extends Object> observable,
        Object oldValue, Object newValue) {
      LOG.info("update display for {} nodes",
          nodeDisplay.nodeDisplayLabelProp.getValue());
      NodeDisplayEntry display = toNodeDisplayEntry(nodeDisplay);
      displayControl.updateNodeDisplayByFilter(
          nodeDisplay.displayFilterRsrc.getResource(), display);
    }
  }

  private static NodeDisplayEntry toNodeDisplayEntry(EditNodeDisplay editData) {
    DepanFxNodeDisplayData nodeDisplayData = new DepanFxNodeDisplayData(
        true, DepanFxSizerModel.DEFAULT,
        JoglColors.of(editData.nodeColorProperty().getValue()));

    NodeDisplayEntry result = new NodeDisplayEntry(
        editData.nodeDisplayLabelProperty().getValue(),
        editData.displayFilterRsrc, nodeDisplayData);
    return result;
  }

  /**
   * Must be public for property lookup.
   *
   * Use xxxProp suffix for property fields to avoid colliding
   * with the required property getter method xxxProperty().
   */
  public static class EditNodeDisplay {

    private final NodeDisplayUpdater updater;

    public StringProperty nodeDisplayLabelProp;

    public StringProperty displayFilterNameProp;

    public DepanFxWorkspaceResource<DepanFxBaseFilterData> displayFilterRsrc;

    public ObjectProperty<Color> nodeColorProp;

    public EditNodeDisplay(
        NodeDisplayController displayControl, NodeDisplayEntry nodeDisplay) {
      updater = new NodeDisplayUpdater(displayControl, this);

      // Unpack data from source.
      nodeDisplayLabelProp =
          new SimpleStringProperty(nodeDisplay.getNodeLabel());
      nodeDisplayLabelProp.addListener(updater);

      displayFilterNameProp = new SimpleStringProperty();
      setDisplayFilterRsrc(nodeDisplay.getFilterResource());

      DepanFxNodeDisplayData displayInfo = nodeDisplay.getNodeDisplay();

      nodeColorProp =
          new SimpleObjectProperty<>(JoglColors.of(displayInfo.color));
      nodeColorProp.addListener(updater);

      // nodeDisplay.getNodeDisplay().nodeSizer;
    }

    public DepanFxBaseFilterData getFilterData() {
      return displayFilterRsrc.getResource();
    }

    public void setDisplayFilterRsrc(
        DepanFxWorkspaceResource<DepanFxBaseFilterData> displayFilterRsrc) {
      this.displayFilterRsrc = displayFilterRsrc;
      if (this.displayFilterRsrc != null) {
        displayFilterNameProp.setValue(
            displayFilterRsrc.getDocument().getMemberPath().toString());
        return;
      }

      displayFilterNameProp.setValue("");
    }

    ///////////////////////////////////
    // For the support of PropertyValueFactory

    public StringProperty nodeDisplayLabelProperty() {
      return nodeDisplayLabelProp;
    }

    public StringProperty displayFilterNameProperty() {
      return displayFilterNameProp;
    }

    public ObjectProperty<Color> nodeColorProperty() {
      return nodeColorProp;
    }
  }
}
