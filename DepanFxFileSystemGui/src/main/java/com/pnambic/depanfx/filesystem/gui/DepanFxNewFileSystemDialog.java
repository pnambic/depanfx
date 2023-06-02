package com.pnambic.depanfx.filesystem.gui;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pnambic.depanfx.filesystem.builder.FileSystemGraphDocBuilder;
import com.pnambic.depanfx.graph_doc.builder.DepanFxGraphModelBuilder;
import com.pnambic.depanfx.graph_doc.builder.SimpleGraphModelBuilder;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("new-file-dialog.fxml")
public class DepanFxNewFileSystemDialog {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNewFileSystemDialog.class.getName());

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
    FileChooser fileChooser = new FileChooser();
    File selectedFile = fileChooser.showSaveDialog(destinationField.getScene().getWindow());
    if (selectedFile != null) {
      destinationField.setText(selectedFile.getAbsolutePath());
    }
  }

  @FXML
  private void handleCancel() {
    closeDialog();
    System.out.println("cancelled request");
  }

  @FXML
  private void handleConfirm() {
    closeDialog();

    System.out.println("Source directory: " + sourceField.getText());
    System.out.println("Destination file: " + destinationField.getText());
    DepanFxGraphModelBuilder modelBuilder = new SimpleGraphModelBuilder();
    FileSystemGraphDocBuilder docBuilder = new FileSystemGraphDocBuilder(modelBuilder);
    docBuilder.analyzeTree(sourceField.getText());
    GraphDocument graphDoc = docBuilder.getGraphDocument();
    File dstFile = new File(destinationField.getText());

    try {
      workspace.saveDocument(dstFile.toURI(), graphDoc);
    } catch (IOException errIo) {
      LOG.error("Unable to save " + dstFile, errIo);
    }
  }

  private void closeDialog() {
    ((Stage) sourceField.getScene().getWindow()).close();
  }
}
