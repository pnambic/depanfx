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

import com.pnambic.depanfx.base.tooldata.DepanFxBaseToolData;
import com.pnambic.depanfx.scene.DepanFxSceneViewer;

import java.util.List;

/**
 * Encapsulates the persisted data for a scene.
 */
public class DepanFxSceneConfig extends DepanFxBaseToolData {

  private final double top;

  private final double left;

  private final double width;

  private final double height;

  private final List<DepanFxSceneViewer> viewersInfo;

  public DepanFxSceneConfig(
      String toolName, String toolDescr,
      double top, double left, double width, double height,
      List<DepanFxSceneViewer> viewersInfo) {
    super(toolName, toolDescr);
    this.top = top;
    this.left = left;
    this.width = width;
    this.height = height;
    this.viewersInfo = viewersInfo;
  }

  public double getTop() {
    return top;
  }

  public double getLeft() {
    return left;
  }

  public double getWidth() {
    return width;
  }

  public double getHeight() {
    return height;
  }

  public List<DepanFxSceneViewer> getViewers() {
    return viewersInfo;
  }
}
