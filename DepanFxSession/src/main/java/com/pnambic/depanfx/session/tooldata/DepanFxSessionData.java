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
package com.pnambic.depanfx.session.tooldata;

import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DepanFxSessionData extends DepanFxBaseToolData {

  public static final DepanFxSessionData EMPTY_SESSION_DATA =
      new DepanFxSessionData(
          "Empty Session", "Empty DepanFx session",
          Collections.emptyList(), Collections.emptyList());

  private final List<DepanFxProjectData> projects;

  private final Collection<DepanFxSceneData> scenes;

  public DepanFxSessionData(
      String toolName, String toolDescription,
      List<DepanFxProjectData> projects,
      Collection<DepanFxSceneData> scenes) {
    super(toolName, toolDescription);
    this.projects = projects;
    this.scenes = scenes;
  }

  public List<DepanFxProjectData> getProjects() {
    return projects;
  }

  public Collection<DepanFxSceneData> getScenes() {
    return scenes;
  }
}
