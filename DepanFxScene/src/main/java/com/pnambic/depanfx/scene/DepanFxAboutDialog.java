package com.pnambic.depanfx.scene;

import com.pnambic.depanfx.scene.DepanFxAppIcons.IconSize;

import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

@Component
@FxmlView("about-dialog.fxml")
public class DepanFxAboutDialog {

  @FXML
  private Label infoLabel;

  @FXML
  private ImageView depanImage;

  @Autowired
  public DepanFxAboutDialog() {
  }

  @FXML
  public void initialize() {
    DepanFxAppIcons.loadDepanIcon(IconSize.ICON_32x32)
        .ifPresent(depanImage::setImage);

    // Tweak welcome tab
    String javaVersion = System.getProperty("java.version");
    String javafxVersion = System.getProperty("javafx.version");
    infoLabel.setText(
        "* Welcome to DepanFX *"
        + "\nBuilt with JavaFX " + javafxVersion
        + "\nRunning on Java " + javaVersion
        + "\n"
        + "\n" + getMemoryStats());
  }

  @FXML
  private void handleClose() {
    closeDialog();
  }

  private void closeDialog() {
    ((Stage) depanImage.getScene().getWindow()).close();
  }

  private String getMemoryStats() {
    Runtime runtime = Runtime.getRuntime();

    long maxMemory = runtime.maxMemory();
    long allocatedMemory = runtime.totalMemory();
    long freeMemory = runtime.freeMemory();
    long usedMemory = allocatedMemory - freeMemory;

    return
        "----- Memory Stats -----"
        + "\nMax Memory: " + maxMemory / (1024 * 1024) + " MB"
        + "\nAllocated Memory: " + allocatedMemory / (1024 * 1024) + " MB"
        + "\nFree Memory: " + freeMemory / (1024 * 1024) + " MB"
        + "\nUsed Memory: " + usedMemory / (1024 * 1024) + " MB";
  }
}
