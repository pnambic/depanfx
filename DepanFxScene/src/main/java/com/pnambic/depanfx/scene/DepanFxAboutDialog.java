package com.pnambic.depanfx.scene;

import com.pnambic.depanfx.scene.DepanFxAppIcons.IconSize;
import com.pnambic.depanfx.scene.tooldata.DepanFxAboutData;

import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

@Component
@FxmlView("about-dialog.fxml")
public class DepanFxAboutDialog {

  public static final DateFormat FORMATTER =
      new SimpleDateFormat("yyyy-MM-dd HH:mm");

  private final DepanFxAboutData aboutInfo;

  @FXML
  private Label infoLabel;

  @FXML
  private ImageView depanImage;

  @Autowired
  public DepanFxAboutDialog(DepanFxAboutData aboutInfo) {
    this.aboutInfo = aboutInfo;
  }

  @FXML
  public void initialize() {
    DepanFxAppIcons.loadDepanIcon(IconSize.ICON_32x32)
        .ifPresent(depanImage::setImage);

    // Tweak welcome tab
    String javaFxVersion = System.getProperty("javafx.version");
    String javaVersion = System.getProperty("java.version");
    StringBuffer body = new StringBuffer("* Welcome to DepanFX *");

    body.append("\n");
    addInfo(body, "Built with JavaFX {0}", javaFxVersion);
    addInfo(body, "Running on Java {0}", javaVersion);

    body.append("\n");
    Date buildDate = aboutInfo.getBuildDate();
    if (buildDate != null) {
      body.append(MessageFormat.format(
          "\nBuilt on {0}", FORMATTER.format(buildDate)));
    }
    addInfo(body, "On commit {0}", aboutInfo.getBuildSha1());
    addInfo(body, "with {0}", "--clean--", aboutInfo.getBuildMods());

    body.append("\n");
    addInfo(body, "Build tag: {0}", aboutInfo.getBuildTag());
    addInfo(body, "Release: {0}", "unspecified", aboutInfo.getBuildRelease());

     body.append(
        "\n\n" + getMemoryStats());

    infoLabel.setText(body.toString());
  }

  private void addInfo(
      StringBuffer body, String message, String suppress, String value) {
    if (suppress.equals(value)) {
      return;
    }
    addInfo(body, message, value);
  }

  private void addInfo(StringBuffer body, String message, String value) {
    if (value != null) {
      body.append("\n");
      body.append(MessageFormat.format(message, value));
    }
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
