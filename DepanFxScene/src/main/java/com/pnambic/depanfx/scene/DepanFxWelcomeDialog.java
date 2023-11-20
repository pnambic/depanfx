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
@FxmlView("welcome-dialog.fxml")
public class DepanFxWelcomeDialog {

  @FXML
  private Label welcomeLabel;

  @FXML
  private ImageView welcomeImage;

  @Autowired
  public DepanFxWelcomeDialog() {
  }

  @FXML
  public void initialize() {
    DepanFxAppIcons.loadDepanIcon(IconSize.ICON_256x256)
        .ifPresent(welcomeImage::setImage);
  }

  @FXML
  private void handleClose() {
    closeDialog();
  }

  private void closeDialog() {
    ((Stage) welcomeImage.getScene().getWindow()).close();
  }
}
