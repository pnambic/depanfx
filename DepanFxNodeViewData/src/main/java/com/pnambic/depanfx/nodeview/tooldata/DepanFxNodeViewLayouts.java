/*
 * Copyright 2025 The Depan Project Authors
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
package com.pnambic.depanfx.nodeview.tooldata;

import com.pnambic.depanfx.nodeview.tooldata.DepanFxTreeLayoutData.Direction;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInProject;

import java.nio.file.Path;

/**
 * Utilities for working with node view layouts.
 */
public class DepanFxNodeViewLayouts {

  public static DepanFxBuiltInContribution.Dependent<DepanFxRadialLayoutData>
  buildRadialLayoutContrib(
      Path layoutInfoPath,
      String layoutName,
      String layoutDescr,
      Path layoutMatcherPath) {

    return new DepanFxBuiltInContribution.Dependent<>(
        layoutInfoPath) {

      @Override
      protected DepanFxRadialLayoutData buildDocument(
          DepanFxBuiltInProject project) {

        return new DepanFxRadialLayoutData(
            layoutName, layoutDescr, getResource(project, layoutMatcherPath)) ;
      }
    };
  }

  public static DepanFxBuiltInContribution.Dependent<DepanFxTreeLayoutData>
  buildTreeLayoutContrib(
      Path layoutInfoPath,
      String layoutName,
      String layoutDescr,
      Direction direction,
      Path layoutMatcherPath) {

    return new DepanFxBuiltInContribution.Dependent<DepanFxTreeLayoutData>(
        layoutInfoPath) {

      @Override
      protected DepanFxTreeLayoutData buildDocument(
          DepanFxBuiltInProject project) {

        return new DepanFxTreeLayoutData(
            layoutName, layoutDescr,
            direction, getResource(project, layoutMatcherPath));
      }
    };
  }
}
