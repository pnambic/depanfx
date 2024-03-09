package com.pnambic.depanfx.java.gui;

import com.google.common.base.Strings;
import com.pnambic.depanfx.bytecode.AsmFactory;
import com.pnambic.depanfx.bytecode.ClassAnalysisStats;
import com.pnambic.depanfx.bytecode.ClassFileReader;
import com.pnambic.depanfx.bytecode.ClassTreeLoader;
import com.pnambic.depanfx.graph.model.GraphModel;
import com.pnambic.depanfx.graph_doc.builder.DepanFxGraphModelBuilder;
import com.pnambic.depanfx.graph_doc.builder.SimpleGraphModelBuilder;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.java.context.JavaContextDefinition;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

@Component
@FxmlView("new-java-dialog.fxml")
public class DepanFxNewJavaDialog {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNewJavaDialog.class.getName());

  private static final ExtensionFilter EXT_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Graph Info", GraphDocument.GRAPH_DOC_EXT);

  private final DepanFxWorkspace workspace;

  @Autowired
  public DepanFxNewJavaDialog(DepanFxWorkspace workspace) {
    this.workspace = workspace;
  }

  @FXML
  private TextField sourceField;

  @FXML
  private TextField graphNameField;

  @FXML
  private TextField graphDescriptionField;

  @FXML
  private TextField destinationField;

  private Optional<DepanFxWorkspaceResource> graphDocRsrc = Optional.empty();

  @FXML
  public void initialize() {
    sourceField.textProperty().addListener(
        (observable, oldValue, newValue) -> updateGraphMetaFromDir(newValue));
  }

  public void setDestination(DepanFxProjectDocument document) {
    destinationField.setText(document.getMemberPath().toString());
  }

  public Optional<DepanFxWorkspaceResource> getSavedResource() {
    return graphDocRsrc;
  }

  @FXML
  private void openDirectoryChooser() {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    File selectedDirectory =
        directoryChooser.showDialog(sourceField.getScene().getWindow());
    if (selectedDirectory != null) {
      sourceField.setText(selectedDirectory.getAbsolutePath());
    }
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
    LOG.info("cancelled request");
  }

  @FXML
  private void handleConfirm() {
    closeDialog();

    File dstFile = new File(destinationField.getText());
    DepanFxProjectDocument projDoc =
        workspace.toProjectDocument(dstFile.toURI()).get();

    try {
      GraphDocument graphDoc = buildGraphDoc();
      graphDocRsrc = workspace.saveDocument(projDoc, graphDoc);
    } catch (IOException errIo) {
      LOG.error("Unable to save {}", dstFile, errIo);
    }
  }

  private void updateGraphMetaFromDir(String newValue) {
    if (graphNameField.getText().isBlank()) {
      String graphName = new File(newValue).getName();
      if (!Strings.isNullOrEmpty(graphName)) {
        graphNameField.setText(graphName + " components");
      }
    }
    if (graphDescriptionField.getText().isBlank()) {
      if (!Strings.isNullOrEmpty(newValue)) {
        String descr = "Graph of Java components from " + newValue;
        graphDescriptionField.setText(descr);
      }
    }
  }

  private FileChooser prepareFileChooser() {
    String baseName = graphNameField.getText();

    FileChooser result =
        DepanFxSceneControls.prepareFileChooser(
            destinationField,
            () -> new File(
                getWorkspaceDestination(),
                buildTimestampName(baseName, GraphDocument.GRAPH_DOC_EXT)));
    result.getExtensionFilters().add(EXT_FILTER);
    result.setSelectedExtensionFilter(EXT_FILTER);

    return result;
  }

  private void closeDialog() {
    ((Stage) sourceField.getScene().getWindow()).close();
  }

  private GraphDocument buildGraphDoc() {
    DepanFxGraphModelBuilder modelBuilder = new SimpleGraphModelBuilder();
    analyzeTree(modelBuilder, sourceField.getText());

    GraphModel graphModel = modelBuilder.createGraphModel();
    String graphName = graphNameField.getText();
    String graphDescr = graphDescriptionField.getText();

    return new GraphDocument(graphName, graphDescr,
        JavaContextDefinition.MODEL_ID, graphModel);
  }

  /**
   * Ensure analysis failures don't propagate outside of the analysis request.
   */
  private void analyzeTree(
      DepanFxGraphModelBuilder modelBuilder, String treePath) {
    ClassAnalysisStats stats = new ClassAnalysisStats();
    ClassFileReader reader =
        new ClassFileReader(AsmFactory.ASM9_FACTORY, stats);
    ClassTreeLoader loader =
        new ClassTreeLoader(treePath, modelBuilder, reader);
    try {
      loader.analyzeTree(treePath);
    } catch (IOException errIo) {
      throw new RuntimeException(
          "Unable to analyze directory " + treePath, errIo);
    }
  }

  private File getWorkspaceDestination() {
    return DepanFxProjects.getCurrentGraphs(workspace);
  }

  private String buildTimestampName(String prefix, String ext) {
    return DepanFxWorkspaceFactory.buildDocumentTimestampName(prefix, ext);
  }
}
