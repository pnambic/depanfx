/*
 * Copyright 2025 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pnambic.depanfx.scene;

import com.pnambic.depanfx.scene.DepanFxAppIcons.IconSize;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Desktop;
import java.net.URI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.text.TextFlow;

@DepanFxFxmlDialog
@FxmlView("welcome-panel.fxml")
public class DepanFxWelcomePanel {

  public static final String GETTING_STARTED_URL =
      "https://pnambic.github.io/depanfx/introduction/initial_project/GettingStarted/";

  public static final String USER_GUIDE_URL =
      "https://pnambic.github.io/depanfx/";

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxWelcomePanel.class);

  @FXML
  private ImageView welcomeImage;

  @FXML
  private TextFlow welcomeTextFlow;

  // Initialize method is called after the FXML is loaded.
  public void initialize() {
    DepanFxAppIcons.loadDepanIcon(IconSize.ICON_256x256)
       .ifPresent(welcomeImage::setImage);

    Region parentRegion = (Region) welcomeTextFlow.getParent();
    welcomeTextFlow.prefWidthProperty().bind(
        parentRegion.widthProperty().multiply(0.7));
  }

  @FXML
  public void openGettingStarted(ActionEvent event) {
    runBrowser(GETTING_STARTED_URL);
  }

  @FXML
  public void openUserGuide(ActionEvent event) {
    runBrowser(USER_GUIDE_URL);
  }

  private void runBrowser(String openUrl) {
    try {
      if(Desktop.isDesktopSupported()){
        Desktop.getDesktop().browse(new URI(openUrl));
        return;
      }
      LOG.info("No Desktop for {}", openUrl);
    } catch (Exception errAny) {
      LOG.warn("Unable to open browser for {}", openUrl, errAny);
    }
  }
}
