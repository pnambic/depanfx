package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.nodeview.tooldata.DepanFxJoglColor;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineArrow;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineDirection;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineForm;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineLabel;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineStyle;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData.LinkDisplayEntry;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

@Component
@FxmlView("node-view-link-display-dialog.fxml")
public class DepanFxNodeViewLinkDisplayDialog {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeViewLinkDisplayDialog.class);

  public static final String EDIT_LINK_DISPLAY = "Edit Link Display...";

  public static final ExtensionFilter NODE_VIEW_LINK_DISPLAY_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Link Display",
          DepanFxNodeViewLinkDisplayData.NODE_VIEW_LINK_DISPLAY_EXT);

  private final DepanFxWorkspace workspace;

  // Only valid after a successful handle confirm.
  private Optional<DepanFxWorkspaceResource> optLinkDisplayDocRsrc;

  @FXML
  private TableView<EditLinkDisplay> linksDisplayTable;

  @FXML
  private TextField toolNameField;

  @FXML
  private TextField toolDescriptionField;

  @FXML
  private TextField destinationField;

  private ObservableList<EditLinkDisplay> linksDiplayTableData;

  public Object propName;

  /**
   * Source of non-mutated data (e.g. context model)
   */
  private DepanFxNodeViewLinkDisplayData linkDisplayData;

  public DepanFxNodeViewLinkDisplayDialog(DepanFxWorkspace workspace) {
    this.workspace = workspace;
  }

  public static Dialog<DepanFxNodeViewLinkDisplayDialog> runEditDialog(
      DepanFxNodeViewLinkDisplayData viewLinkData,
      DepanFxDialogRunner dialogRunner) {
    Dialog<DepanFxNodeViewLinkDisplayDialog> dlg =
        dialogRunner.createDialogAndParent(
            DepanFxNodeViewLinkDisplayDialog.class);
    dlg.getController().setTooldata(viewLinkData);
    // dlg.getController().setDestination(projDoc);
    dlg.runDialog(EDIT_LINK_DISPLAY);
    return dlg;
  }

  public static void setNodeViewLinkDisplayTooldataFilters(FileChooser chooser) {
    chooser.getExtensionFilters().add(NODE_VIEW_LINK_DISPLAY_FILTER);
    chooser.setSelectedExtensionFilter(NODE_VIEW_LINK_DISPLAY_FILTER);
  }

  @FXML
  @SuppressWarnings("unchecked")
  public void initialize() {
    ColumnBinder<EditLinkDisplay> columnBinder =
        new ColumnBinder<>(linksDisplayTable);

    TableColumn<EditLinkDisplay, String> labelColumn =
        columnBinder.bind("linkDisplayLabel");
    labelColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    labelColumn.setOnEditCommit(this::onUpdateLabelEvent);

    TableColumn<EditLinkDisplay, String> filePathColumn =
        columnBinder.bind("linkDisplayName");

    TableColumn<EditLinkDisplay, DepanFxLineForm> lineFormColumn =
        columnBinder.bind("lineForm", DepanFxLineForm.class);

    TableColumn<EditLinkDisplay, DepanFxLineStyle> lineStyleColumn =
        columnBinder.bind("lineStyle", DepanFxLineStyle.class);

    TableColumn<EditLinkDisplay, Color> lineColorColumn =
        columnBinder.bind("lineColor");
    lineColorColumn.setEditable(true);
    lineColorColumn.setCellFactory(column -> new ColorCellFactory());

    TableColumn<EditLinkDisplay, Double> lineWidthColumn =
        columnBinder.bind("lineWidth");

    TableColumn<EditLinkDisplay, DepanFxLineLabel> lineLabelColumn =
        columnBinder.bind("lineLabel", DepanFxLineLabel.class);

    TableColumn<EditLinkDisplay, DepanFxLineArrow> sourceArrowColumn =
        columnBinder.bind("sourceArrow", DepanFxLineArrow.class);

    TableColumn<EditLinkDisplay, DepanFxLineArrow> targetArrowColumn =
        columnBinder.bind("targetArrow", DepanFxLineArrow.class);

    TableColumn<EditLinkDisplay, DepanFxLineDirection> lineDirectionColumn =
        columnBinder.bind("lineDirection", DepanFxLineDirection.class);

    TableColumn<EditLinkDisplay, LinkOrderOperation> linkOperationColumn =
        columnBinder.bind("linkOrderOperation", LinkOrderOperation.class);
    linkOperationColumn.setOnEditCommit(this::onLinkOperationEvent);
  }

  public void setTooldata(DepanFxNodeViewLinkDisplayData linkDisplayData) {
    this.linkDisplayData = linkDisplayData;
    toolNameField.setText(linkDisplayData.getToolName());
    toolDescriptionField.setText(linkDisplayData.getToolDescription());

    List<EditLinkDisplay> editLinkDisplay =
        linkDisplayData.streamLinkDisplay()
        .map(e -> new EditLinkDisplay(e))
        .collect(Collectors.toList());

    linksDiplayTableData = FXCollections.observableArrayList(editLinkDisplay);
    linksDisplayTable.setItems(linksDiplayTableData);
  }

  @FXML
  private void addLinkDisplayRow() {
    DepanFxLineDisplayData lineDisplay =
        DepanFxLineDisplayData.buildSimpleLineDisplayData();
    LinkDisplayEntry rowDisplay = new LinkDisplayEntry("", null, lineDisplay);
    linksDiplayTableData.add(new EditLinkDisplay(rowDisplay));
  }

  @FXML
  private void handleApply() {
  }

  @FXML
  private void handleCancel() {
    closeDialog();
    optLinkDisplayDocRsrc = Optional.empty();
  }

  @FXML
  private void handleConfirm() {
    closeDialog();

    List<LinkDisplayEntry> displayEntries = linksDiplayTableData.stream()
        .map(this::toLinkDisplayEntry)
        .collect(Collectors.toList());

    DepanFxNodeViewLinkDisplayData saveData =
        new DepanFxNodeViewLinkDisplayData(
            toolNameField.getText(), 
            toolDescriptionField.getText(),
            linkDisplayData.getContextModelId(), displayEntries);

    File dstDocFile = new File(destinationField.getText());
    optLinkDisplayDocRsrc = workspace.toProjectDocument(dstDocFile.toURI())
        .flatMap(d -> saveDocument(d, saveData));
  }

  @FXML
  private void openDestinationChooser() {
    FileChooser fileChooser = prepareDestinationFileChooser();
    File selectedFile =
        fileChooser.showSaveDialog(destinationField.getScene().getWindow());
    if (selectedFile != null) {
      destinationField.setText(selectedFile.getAbsolutePath());
    }
  }

  private void closeDialog() {
    ((Stage) destinationField.getScene().getWindow()).close();
  }

  private LinkDisplayEntry toLinkDisplayEntry(EditLinkDisplay editData) {
    DepanFxLineDisplayData lineDisplayData = new DepanFxLineDisplayData(
        editData.lineFormProperty().getValue(),
        editData.lineStyleProperty().getValue(),
        DepanFxJoglColor.of(editData.lineColorProperty().getValue()),
        editData.lineWidthProperty().getValue(),

        editData.lineLabelProperty().getValue(),
        editData.sourceArrowProperty().getValue(),
        editData.targetArrowProperty().getValue(),
        editData.lineDirectionProperty().getValue());

    LinkDisplayEntry result = new LinkDisplayEntry(
        editData.linkDisplayLabelProperty().getValue(),
        editData.linkDisplayRsrc,
        lineDisplayData );
    return result ;
  }

  private Optional<DepanFxWorkspaceResource> saveDocument(
      DepanFxProjectDocument projDoc, DepanFxNodeViewLinkDisplayData docData) {
    try {
      return workspace.saveDocument(projDoc, docData);
    } catch (IOException errIo) {
      LOG.error("Unable to save {}, type {}",
          projDoc.toString(), docData.getClass().getName(), errIo);
      throw new RuntimeException(
          "Unable to save " + projDoc.toString()
          + ", type " + docData.getClass().getName(),
          errIo);
    }
  }

  private FileChooser prepareDestinationFileChooser() {
    FileChooser result =
        DepanFxSceneControls.prepareFileChooser(
            destinationField, () -> buildInitialDestinationFile());
    setNodeViewLinkDisplayTooldataFilters(result);
    return result;
  }

  private File buildInitialDestinationFile() {
    return DepanFxWorkspaceFactory.bestDocumentFile(
        toolNameField.getText(),
        DepanFxNodeViewLinkDisplayData.NODE_VIEW_LINK_DISPLAY_EXT,
        workspace, DepanFxNodeViewLinkDisplayData.LINK_DISPLAY_PATH,
        DepanFxProjects.getCurrentTools(workspace));
  }

  private void onUpdateLabelEvent(
      CellEditEvent<EditLinkDisplay, String> updateEvent) {
    getEventLinkDisplay(updateEvent)
        .linkDisplayLabelProp.set(updateEvent.getNewValue());
  }

  private void onLinkOperationEvent(
      CellEditEvent<EditLinkDisplay, LinkOrderOperation> updateEvent) {
    EditLinkDisplay editRow = updateEvent.getRowValue();
    int rowIndex = updateEvent.getTablePosition().getRow();
    switch (updateEvent.getNewValue()) {
    case DOWN:
      if (rowIndex < linksDiplayTableData.size() - 1) {
        EditLinkDisplay moveRow = linksDiplayTableData.remove(rowIndex);
        linksDiplayTableData.add(rowIndex + 1, moveRow);
      }
      editRow.linkOrderOperationProp.set(LinkOrderOperation.NONE);
      return;
    case UP:
      if (rowIndex > 0) {
        EditLinkDisplay moveRow = linksDiplayTableData.remove(rowIndex);
        linksDiplayTableData.add(rowIndex - 1, moveRow);
      }
      editRow.linkOrderOperationProp.set(LinkOrderOperation.NONE);
      return;
    case DELETE:
      linksDiplayTableData.remove(rowIndex);
      return;
    case NONE:
      return;
    }
  }

  private EditLinkDisplay getEventLinkDisplay(
      CellEditEvent<EditLinkDisplay, String> updateEvent) {
    return updateEvent.getTableView().getItems().get(
        updateEvent.getTablePosition().getRow());
  }

  private static enum LinkOrderOperation {
    NONE, UP, DOWN, DELETE;
  }

  private static class ColumnBinder<S> {

    private final TableView<S> tableView;

    private int next = 0;

    public ColumnBinder(TableView<S> tableView) {
      this.tableView = tableView;
    }

    @SuppressWarnings("unchecked")
    public <T> TableColumn<S, T> bind(String propName) {
      TableColumn<S, T> result =
          (TableColumn<S, T>) tableView.getColumns().get(next);
      next ++;

      result.setCellValueFactory(new PropertyValueFactory<>(propName));
      return result;
    }

    @SuppressWarnings("unchecked")
    public <T> TableColumn<S, T> bind(String propName, Class<?> type) {
      TableColumn<S, T> result = bind(propName);

      T[] values = (T[]) type.getEnumConstants();
      result.setCellFactory(
          ComboBoxTableCell.forTableColumn(values));
      return result;
    }
  }

  public static class ColorCellFactory
      extends TableCell<EditLinkDisplay, Color> {

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
        EditLinkDisplay linkDisplay = getTableRow().getItem();
        linkDisplay.lineColorProp.setValue(colorPicker.getValue());
      });
    }
  }

  /**
   * Must be public for property lookup.
   *
   * Use xxxProp suffix for property fields to avoid colliding with the assumed
   * property getter method xxxProperty().
   */
  public static class EditLinkDisplay {

    public StringProperty linkDisplayLabelProp;

    public StringProperty linkDisplayNameProp;

    public DepanFxWorkspaceResource linkDisplayRsrc;

    public ObjectProperty<DepanFxLineForm> lineFormProp;

    public ObjectProperty<DepanFxLineStyle> lineStyleProp;

    public ObjectProperty<Color> lineColorProp;

    public SimpleDoubleProperty lineWidthProp;

    public ObjectProperty<DepanFxLineLabel> lineLabelProp;

    public ObjectProperty<DepanFxLineArrow> sourceArrowProp;

    public ObjectProperty<DepanFxLineArrow> targetArrowProp;

    public ObjectProperty<DepanFxLineDirection> lineDirectionProp;

    public ObjectProperty<LinkOrderOperation> linkOrderOperationProp;

    public EditLinkDisplay(LinkDisplayEntry linkDisplay) {
      // Unpack data from source.
      linkDisplayLabelProp =
          new SimpleStringProperty(linkDisplay.getLinkLabel());
      linkDisplayNameProp = new SimpleStringProperty();
      setLinkDisplayRsrc(linkDisplay.getLinkRsrc());

      DepanFxLineDisplayData lineDisplay = linkDisplay.getLineDisplay();
      lineFormProp = new SimpleObjectProperty<>(lineDisplay.lineForm);
      lineStyleProp = new SimpleObjectProperty<>(lineDisplay.lineStyle);
      lineColorProp =
          new SimpleObjectProperty<>(lineDisplay.lineColor.toFxColor());
      lineWidthProp = new SimpleDoubleProperty(lineDisplay.lineWidth);
      lineLabelProp = new SimpleObjectProperty<>(lineDisplay.lineLabel);
      sourceArrowProp = new SimpleObjectProperty<>(lineDisplay.sourceArrow);
      targetArrowProp = new SimpleObjectProperty<>(lineDisplay.targetArrow);
      lineDirectionProp = new SimpleObjectProperty<>(lineDisplay.lineDir);

      linkOrderOperationProp =
          new SimpleObjectProperty<>(LinkOrderOperation.NONE);
    }

    public void setLinkDisplayRsrc(DepanFxWorkspaceResource linkDisplayRsrc) {
      this.linkDisplayRsrc = linkDisplayRsrc;
      if (this.linkDisplayRsrc != null) {
        linkDisplayNameProp.setValue(
            linkDisplayRsrc.getDocument().getMemberName());
      }

      linkDisplayNameProp.setValue("");
    }

    ///////////////////////////////////
    // For the support of PropertyValueFactory

    public StringProperty linkDisplayLabelProperty() {
      return linkDisplayLabelProp;
    }

    public StringProperty linkDisplayNameProperty() {
      return linkDisplayNameProp;
    }

    public ObjectProperty<DepanFxLineForm> lineFormProperty() {
      return lineFormProp;
    }

    public ObjectProperty<DepanFxLineStyle> lineStyleProperty() {
      return lineStyleProp;
    }

    public ObjectProperty<Color> lineColorProperty() {
      return lineColorProp;
    }

    public SimpleDoubleProperty lineWidthProperty() {
      return lineWidthProp;
    }

    public ObjectProperty<DepanFxLineLabel> lineLabelProperty() {
      return lineLabelProp;
    }

    public ObjectProperty<DepanFxLineArrow> sourceArrowProperty() {
      return sourceArrowProp;
    }

    public ObjectProperty<DepanFxLineArrow> targetArrowProperty() {
      return targetArrowProp;
    }

    public ObjectProperty<DepanFxLineDirection> lineDirectionProperty() {
      return lineDirectionProp;
    }

    public ObjectProperty<LinkOrderOperation> linkOrderOperationProperty() {
      return linkOrderOperationProp;
    }
  }
}
