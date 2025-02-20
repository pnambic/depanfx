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

import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

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

  public static ExtensionFilter buildExtFilter(String label, String ext) {
    String matchGlob = buildMatchGlob(ext);
    return new ExtensionFilter(label + "(" + matchGlob + ")", matchGlob);
  }

  /**
   * Encapsulates the secret of taking a file extension (which may contain
   * glob patterns), and converting into a glob pattern for matching files
   * with the supplied extension.
   */
  public static String buildMatchGlob(String ext) {
    return "*." + ext;
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

  public static void updateBlankField(TextField updateField, String newValue) {
    if (isPresent(updateField.getText())) {
      updateField.setText(newValue);
    }
  }

  public static void handleDoubleClickOpenPath(
      MouseEvent event, Path path, Consumer<Path> onOpenPath) {
    if (event.getButton() == MouseButton.PRIMARY
        && event.getClickCount() == 2) {
      onOpenPath.accept(path);
    }
  }

  public static DirectoryChooser prepareDirectoryChooser(TextField dirField) {
    DirectoryChooser result = new DirectoryChooser();
    String dirName = dirField.getText();
    if (isPresent(dirName)) {
      File location = new File(dirName);
      result.setInitialDirectory(location.getParentFile());
    }
    return result;
  }

  public static FileChooser prepareFileChooser(TextField fileField) {
    FileChooser result = new FileChooser();
    String fileName = fileField.getText();
    if (isPresent(fileName)) {
      initializeFileChooser(result, new File(fileName));
    }
    return result;
  }

  public static FileChooser prepareFileChooser(File location) {
    FileChooser result = new FileChooser();
    initializeFileChooser(result, location);
    return result;
  }

  public static FileChooser prepareFileChooser(
      Path filePath, Supplier<File> onBlank) {
    String fileName = filePath != null ? filePath.toString() : "";
    return prepareFileChooser(fileName, onBlank);
  }

  public static FileChooser prepareFileChooser(
      TextField fileField, Supplier<File> onBlank) {
    return prepareFileChooser(fileField.getText(), onBlank);
  }

  public static FileChooser prepareFileChooser(
      String fileName, Supplier<File> onBlank) {
    FileChooser result = new FileChooser();
    initializeFileChooser(result, buildLocationFile(fileName, onBlank));
    return result;
  }

  private static void initializeFileChooser(
      FileChooser result, File location) {
    result.setInitialFileName(location.getName());
    result.setInitialDirectory(location.getParentFile());
  }

  private static File buildLocationFile(
      String fileName, Supplier<File> onBlank) {
    if (isPresent(fileName)) {
      return new File(fileName);
    }
    return onBlank.get();
  }

  /**
   * Partially to abbreviate a common check.
   * Partially to get a positive sense for the test.
   *
   * @return {@code true} of supplied text seems to have a non-blank body.
   */
  private static boolean isPresent(String text) {
    return !Strings.isNullOrEmpty(text);
  }
}
