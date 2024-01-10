package com.pnambic.depanfx.nodelist.gui;

import com.google.common.base.Strings;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.export.AbstractCsvExporter;
import com.pnambic.depanfx.nodelist.export.ExportColumn;
import com.pnambic.depanfx.nodelist.export.ExportData;
import com.pnambic.depanfx.nodelist.export.ExportData.NodeIdHandling;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumn;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * In the ..nodelist.gui package (vs. ..nodelist.export) to be visible to
 * JavaFx reflection.
 */
public abstract class DepanFxBaseExportSectionDialog {

  private static final String CSV_EXT = "csv";

  private static final ExtensionFilter EXT_FILTER = DepanFxSceneControls.buildExtFilter("Spreadsheet/CSV", CSV_EXT);

  private final DepanFxWorkspace workspace;

  private DepanFxNodeListSection section;

  @FXML
  private Label exportDetailsLabel;

  @FXML
  protected ChoiceBox<NodeIdHandling> nodeIdExportChoiceField;

  @FXML
  private CheckBox nodeIdWithListField;

  @FXML
  protected TableView<ExportData> exportTable;

  @FXML
  private TextField destinationField;

  public DepanFxBaseExportSectionDialog(DepanFxWorkspace workspace) {
    this.workspace = workspace;
  }

  @FXML
  public void initialize() {
    nodeIdExportChoiceField.getItems().addAll(NodeIdHandling.values());
    nodeIdExportChoiceField.setValue(NodeIdHandling.PARTS);
  
    ExportData.prepareExportTable(exportTable);
  }

  /**
   * Rarely export into the analysis project.  Discourage it, in fact.
   */
  public void setDestination(File destFile) {
    destinationField.setText(destFile.toString());
  }

  @FXML
  private void openFileChooser() {
    FileChooser fileChooser = prepareFileChooser();
    File selectedFile =
        fileChooser.showSaveDialog(destinationField.getScene().getWindow());
    if (selectedFile != null) {
      destinationField.setText(selectedFile.getAbsolutePath());
    }
  }

  @FXML
  private void handleCancel() {
    closeDialog();
  }

  @FXML
  private void handleConfirm() {
    String dstName = destinationField.getText();
    File dstFile = new File(dstName);
    if (Strings.isNullOrEmpty(dstName)) {
      Alert alert = new Alert(AlertType.ERROR);
      alert.setContentText("Destination field is not usable");
      alert.setHeaderText("Node List Save Confirmation Error");
      alert.setTitle("Blank value for destination field");
      alert.showAndWait();
      return;
    }
    closeDialog();

    List<ExportColumn> exportColumns = ExportColumn.fromExportData(
        nodeIdExportChoiceField.getValue(), exportTable.getItems().stream());
    AbstractCsvExporter exporter = getCsvExporter(exportColumns);
    Collection<GraphNode> roots = getExportRoots();

    try (Writer writer = new FileWriter(dstFile)) {
      exporter.export(writer, roots);
    } catch (Exception errAny) {
      throw new RuntimeException(
          "Unable to export section " + section.getSectionLabel(),
          errAny);
    }
  }

  protected abstract Collection<GraphNode> getExportRoots();

  protected abstract Stream<DepanFxNodeListColumn> streamColumns();

  protected abstract AbstractCsvExporter getCsvExporter(
      List<ExportColumn> exportColumns);

  protected abstract GraphDocument getGraphDoc();

  protected void setSectionDoc(DepanFxNodeListSection section) {
    this.section = section;
    exportDetailsLabel.setText(buildDetailsLabel());
    ExportData.populateExportTable(exportTable, streamColumns());
  }

  protected String buildDetailsLabel() {
    int nodeCount = section.getSectionNodes().getNodes().size();
    return MessageFormat.format(
        "Exporting {0} section {1} with {2} nodes.",
        getGraphDoc().getGraphName(), section.getSectionLabel(), nodeCount);
  }

  protected DepanFxNodeListSection getSection() {
    return section;
  }

  private FileChooser prepareFileChooser() {
    String graphName = getGraphDoc().getGraphName();
    String baseName = graphName + " export";
    FileChooser result =
        DepanFxSceneControls.prepareFileChooser(
            destinationField,
            () -> new File(
                buildTimestampName(baseName, CSV_EXT)));
    result.getExtensionFilters().add(EXT_FILTER);
    result.setSelectedExtensionFilter(EXT_FILTER);
  
    return result;
  }

  private void closeDialog() {
    ((Stage) destinationField.getScene().getWindow()).close();
  }

  private String buildTimestampName(String prefix, String ext) {
    return DepanFxWorkspaceFactory.buildDocumentTimestampName(prefix, ext);
  }
}
