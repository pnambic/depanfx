package com.pnambic.depanfx.filesystem.gui;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pnambic.depanfx.filesystem.builder.FileSystemGraphDocBuilder;
import com.pnambic.depanfx.graph_doc.builder.DepanFxGraphModelBuilder;
import com.pnambic.depanfx.graph_doc.builder.SimpleGraphModelBuilder;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("new-file-dialog.fxml")
public class DepanFxNewFileSystemDialog {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNewFileSystemDialog.class.getName());

  private static final String PREFIX = "tree_";

  private static final String EXT = "dgi";

  private static final ExtensionFilter EXT_FILTER =
      new ExtensionFilter("Graph Info (*.dgi)", "*." + EXT);

  private final DepanFxWorkspace workspace;

  @Autowired
  public DepanFxNewFileSystemDialog(DepanFxWorkspace workspace) {
    this.workspace = workspace;
  }

  @FXML
  private TextField sourceField;

  @FXML
  private TextField destinationField;

  @FXML
  private void openDirectoryChooser() {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    File selectedDirectory = directoryChooser.showDialog(sourceField.getScene().getWindow());
    if (selectedDirectory != null) {
      sourceField.setText(selectedDirectory.getAbsolutePath());
    }
  }

  @FXML
  private void openFileChooser() {
    FileChooser fileChooser = prepareFileChooser();
    File selectedFile = fileChooser.showSaveDialog(destinationField.getScene().getWindow());
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

    System.out.println("Source directory: " + sourceField.getText());
    System.out.println("Destination file: " + destinationField.getText());
    DepanFxGraphModelBuilder modelBuilder = new SimpleGraphModelBuilder();
    FileSystemGraphDocBuilder docBuilder = new FileSystemGraphDocBuilder(modelBuilder);
    analyzeTree(docBuilder, sourceField.getText());
    GraphDocument graphDoc = docBuilder.getGraphDocument();
    File dstFile = new File(destinationField.getText());

    try {
      workspace.saveDocument(dstFile.toURI(), graphDoc);
    } catch (IOException errIo) {
      LOG.error("Unable to save " + dstFile, errIo);
    }
  }

  private FileChooser prepareFileChooser() {
    FileChooser result = new FileChooser();
    result.getExtensionFilters().add(EXT_FILTER);
    result.setSelectedExtensionFilter(EXT_FILTER);

    String destFileName = destinationField.getText();
    if (destFileName.isBlank()) {
      result.setInitialFileName(buildTimestampName(PREFIX, EXT));
      result.setInitialDirectory(getWorkspaceDestination());
      return result;
    }

    File location = new File(destFileName);
    result.setInitialFileName(location.getName());
    result.setInitialDirectory(location.getParentFile());
    return result;
  }

  /**
   * Ensure analysis failures don't propogate outside of the analysis request.
   */
  private void analyzeTree(FileSystemGraphDocBuilder docBuilder, String treeName) {
    try {
      docBuilder.analyzeTree(treeName);
    } catch (RuntimeException errBuild) {
      LOG.error("unable to build tree from {}", treeName, errBuild);
    }
  }

  private void closeDialog() {
    ((Stage) sourceField.getScene().getWindow()).close();
  }

  private File getWorkspaceDestination() {
    return DepanFxProjects.getCurrentGraphs(workspace);
  }

  private String buildTimestampName(String prefix, String ext) {
    return DepanFxWorkspaceFactory.buildDocumentTimestampName(prefix, ext);
  }
}
