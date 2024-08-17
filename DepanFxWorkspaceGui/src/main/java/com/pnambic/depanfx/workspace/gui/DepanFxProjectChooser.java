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
package com.pnambic.depanfx.workspace.gui;

import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.projects.DepanFxFileSystemProject;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import javafx.stage.DirectoryChooser;

public class DepanFxProjectChooser {

  public static final String OPEN_PROJECT_CONTEXT_ITEM = "Open Project ..";

  public static Optional<DepanFxProjectTree> runProjectFinder() {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    directoryChooser.setTitle(OPEN_PROJECT_CONTEXT_ITEM);
    File selectedDirectory = directoryChooser.showDialog(null);

    if (selectedDirectory != null) {
      String projectName = selectedDirectory.getName();
      Path projectPath = selectedDirectory.toPath();
      DepanFxFileSystemProject projectSpi =
          new DepanFxFileSystemProject(projectName, projectPath);
      return Optional.of(
          DepanFxWorkspaceFactory.createDepanFxProjectTree(projectSpi));
    }
    return Optional.empty();
  }
}
