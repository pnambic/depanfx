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

import com.pnambic.depanfx.scene.DepanFxSceneViewer;
import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

import java.util.List;

/**
 * Encapsulates the persisted data for a scene.
 */
public class DepanFxSceneConfig extends DepanFxBaseToolData {

  private final List<DepanFxSceneViewer> viewersInfo;

  public DepanFxSceneConfig(String toolName, String toolDescr,
      List<DepanFxSceneViewer> viewersInfo) {
    super(toolName, toolDescr);
    this.viewersInfo = viewersInfo;
  }

  public List<DepanFxSceneViewer> getViewers() {
    return viewersInfo;
  }
}
