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

import com.pnambic.depanfx.session.viewdata.DepanFxBaseViewerData;
import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

import java.util.Collections;
import java.util.List;

/**
 * Encapsulates the persisted data for a scene.
 */
public class DepanFxSceneData extends DepanFxBaseToolData {

  public static final DepanFxSceneData EMPTY_SESSION_SCENE =
      new DepanFxSceneData(
          "Empty Session", "Empty",
          -1, -1, -1, -1, Collections.emptyList());

  private final int top;

  private final int left;

  private final int width;

  private final int height;

  private final List<DepanFxBaseViewerData> viewersInfo;

  public DepanFxSceneData(
      String toolName, String toolDescr,
      int top, int left, int width, int height,
      List<DepanFxBaseViewerData> viewersInfo) {
    super(toolName, toolDescr);
    this.top = top;
    this.left = left;
    this.width = width;
    this.height = height;
    this.viewersInfo = viewersInfo;
  }

  public int getTop() {
    return top;
  }

  public int getLeft() {
    return left;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public List<DepanFxBaseViewerData> getViewers() {
    return viewersInfo;
  }
}
