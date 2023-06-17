package com.pnambic.depanfx.filesystem.gui;

import java.io.File;

import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("new-file-dialog.fxml")
public class DepanFxNewFileSystemDialog {

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
  private void handleConfirm() {
    System.out.println("Source directory: " + sourceField.getText());
    System.out.println("Destination file: " + destinationField.getText());
    // Handle the selected source directory and destination file here
  }

}
