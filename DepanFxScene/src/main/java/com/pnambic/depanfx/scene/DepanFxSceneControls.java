/*
 * Copyright 2023 The Depan Project Authors
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Optional;

import javafx.scene.image.Image;
import javafx.scene.text.Text;

/**
 * Utility methods for common scene components and behaviors.
 */
public class DepanFxSceneControls {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxSceneControls.class);

  private DepanFxSceneControls() {
    // Prevent instantiation.
  }

  public static double layoutWidthMs(double scale) {
     Text render = new Text("m");
     return scale * render.getLayoutBounds().getWidth();
   }

  public static Optional<Image> loadResourceImage(
      Class<?> type, String rsrcName) {
    try {
      InputStream rsrcStream = type.getResourceAsStream(rsrcName);
      if (rsrcStream != null) {
        return Optional.of(new Image(rsrcStream));
      }
      LOG.warn(
          "Unable to load image resource {} for type {}",
          rsrcName, type.getName());
    } catch (Exception errAny) {
      LOG.warn(
          "Unable to load image {} for type {}",
          rsrcName, type.getName(), errAny);
    }
    return Optional.empty();
  }
}
