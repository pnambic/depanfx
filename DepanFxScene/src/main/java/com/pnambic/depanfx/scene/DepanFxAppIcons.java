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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.scene.image.Image;

/**
 * A kludgy singleton to hold all the icon images for wide reuse.
 */
public class DepanFxAppIcons {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxAppIcons.class);

  private static Map<IconSize, Image> appIconImages = loadDepanIcons();

  private DepanFxAppIcons() {
    // Prevent instantiation.
  }

  public static enum IconSize {
      ICON_16x16("16x16"), ICON_32x32("32x32"), ICON_48x48("48x48"),
      ICON_64x64("64x64"), ICON_128x128("128x128"), ICON_250x250("250x250"),
      ICON_256x256("256x256");

    private final String sizeSfx;

    private IconSize(String sizeSfx) {
      this.sizeSfx = sizeSfx;
    }
    public String getSizeSfx() {
      return sizeSfx;
    }

    public String getIconName() {
      return "branding/depan" + sizeSfx + ".png";
    }
  }

  public static void installDepanIcons(ObservableList<Image> appIcons) {
    appIconImages.values().forEach(appIcons::add);
  }

  public static Optional<Image> loadDepanIcon(IconSize size) {
    return Optional.ofNullable(appIconImages.get(size));
  }

  private static Map<IconSize, Image> loadDepanIcons() {
    Map<IconSize, Image> result = new HashMap<>();
    for (IconSize size : IconSize.values()) {
      DepanFxSceneControls.loadResourceImage(
          DepanFxAppIcons.class, size.getIconName())
          .ifPresentOrElse(
              i -> result.put(size, i),
              () -> LOG.warn("Unable to load icon for size {}",
                      size.getSizeSfx()));
    }
    return result;
  }
}
