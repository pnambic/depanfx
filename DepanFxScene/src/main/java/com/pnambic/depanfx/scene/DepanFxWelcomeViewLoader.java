/*
 * Copyright 2024 The Depan Project Authors
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
import com.pnambic.depanfx.scene.plugins.DepanFxSceneStarterContribution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Provides a welcome scene from atomic parts.
 *
 * Loads a simple VBox from FXML, installs an icon image, with the intent
 * of showing that content in the "Welcome" tab.
 */
@Component
public class DepanFxWelcomeViewLoader {

  private static final String WELCOME_VIEWER_FXML = "welcome-viewer.fxml";

  private final DepanFxWelcomeViewer welcomeViewer;

  @Autowired
  public DepanFxWelcomeViewLoader() {
    welcomeViewer = buildSceneViewer();
  }

  public DepanFxSceneViewer getWelcomeViewer() {
    return welcomeViewer;
  }

  private DepanFxWelcomeViewer buildSceneViewer() {
    VBox resource = getWelcomeContent();

    // Dynamically lookup the ImageView
    ImageView welcomeImage = (ImageView) resource.lookup("#welcomeImage");
    DepanFxAppIcons.loadDepanIcon(IconSize.ICON_256x256)
       .ifPresent(welcomeImage::setImage);

    return new DepanFxWelcomeViewer(resource);
  }

  /**
   * Encapsulate error handling for the welcome resource.
   */
  private <T> T getWelcomeContent() {
    FXMLLoader loader =
        new FXMLLoader(DepanFxWelcomeViewLoader.class
            .getResource(WELCOME_VIEWER_FXML));

    try {
      T result = loader.load();
      return result;
    } catch (IOException errIo) {
      throw new DepanFxSceneStarterContribution.LoadViewerException(errIo);
    }
  }
}
