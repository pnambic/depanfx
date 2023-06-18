package com.pnambic.depanfx.filesystem.gui;

import java.io.File;

import org.springframework.stereotype.Component;

import com.pnambic.depanfx.filesystem.builder.FileSystemGraphDocBuilder;
import com.pnambic.depanfx.graph_doc.builder.DepanFxGraphModelBuilder;
import com.pnambic.depanfx.graph_doc.builder.SimpleGraphModelBuilder;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("new-file-dialog.fxml")
public class DepanFxNewFileSystemDialog {
  AnchorPane pane;

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
  }

  private void closeDialog() {
    ((Stage) sourceField.getScene().getWindow()).close();
  }
}
