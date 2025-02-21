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
package com.pnambic.depanfx.session.core;

import com.pnambic.depanfx.session.tooldata.DepanFxSceneData;
import com.pnambic.depanfx.session.tooldata.DepanFxSessionData;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DepanFxSessionConfig {

  public static final DepanFxSessionConfig EMPTY_SESSION_DATA =
      new DepanFxSessionConfig(
          DepanFxSessionData.BLANK_CURRENT_PROJECT,
          Collections.emptyList(), Collections.emptyList());

  private final String currentProjectName;

  private final List<DepanFxProjectTree> projectTrees;

  private final Collection<DepanFxSceneData> sceneConfigs;

  public DepanFxSessionConfig(
      String currentProjectName,
      List<DepanFxProjectTree> projectTrees,
      Collection<DepanFxSceneData> sceneConfigs) {
    this.currentProjectName = currentProjectName;
    this.projectTrees = projectTrees;
    this.sceneConfigs = sceneConfigs;
  }

  public String getCurrentProjectName() {
    return currentProjectName;
  }

  public List<DepanFxProjectTree> getProjectTrees() {
    return projectTrees;
  }

  public Collection<DepanFxSceneData> getSceneConfigs() {
    return sceneConfigs;
  }
}
