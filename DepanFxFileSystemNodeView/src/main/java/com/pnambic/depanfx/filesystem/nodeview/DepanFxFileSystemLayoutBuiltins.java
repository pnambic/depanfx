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
package com.pnambic.depanfx.filesystem.nodeview;

import com.pnambic.depanfx.filesystem.context.FileSystemContextModelId;
import com.pnambic.depanfx.filesystem.nodelist.link.FileSystemLinkMatcherBuiltIns;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLayoutData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLayouts;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxRadialLayoutData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxTreeLayoutData;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class DepanFxFileSystemLayoutBuiltins {

  public static final Path FILE_SYSTEM_LAYOUT_PATH =
      DepanFxNodeViewLayoutData.LAYOUT_TOOL_PATH.resolve(
          FileSystemContextModelId.FILE_SYSTEM_KEY);

  public static final String FILE_SYSTEM_MEMBERSHIP_RADIAL_LAYOUT_NAME =
      "Member Radial Layout";

  public static final Path FILE_SYSTEM_MEMBERSHIP_RADIAL_LAYOUT_PATH =
      FILE_SYSTEM_LAYOUT_PATH.resolve(FILE_SYSTEM_MEMBERSHIP_RADIAL_LAYOUT_NAME);

  public static final String PACKAGE_MEMBERSHIP_TREE_LAYOUT_NAME =
      "Member Tree Layout";

  public static final Path FILE_SYSTEM_MEMBERSHIP_TREE_LAYOUT_PATH =
      FILE_SYSTEM_LAYOUT_PATH.resolve(PACKAGE_MEMBERSHIP_TREE_LAYOUT_NAME);

  @Bean
  public DepanFxBuiltInContribution<DepanFxRadialLayoutData>
  buildFileSystemMembershipRadialLayout() {
    return DepanFxNodeViewLayouts.buildRadialLayoutContrib(
        FILE_SYSTEM_MEMBERSHIP_RADIAL_LAYOUT_PATH,
        "File System Member Radial Layout",
        "Layout selected nodes based on their File System member relations",
        FileSystemLinkMatcherBuiltIns.FILE_SYSTEM_MEMBER_MATCHER_PATH);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxTreeLayoutData>
  buildFileSystemMembershipTreeLayout() {
    return DepanFxNodeViewLayouts.buildTreeLayoutContrib(
        FILE_SYSTEM_MEMBERSHIP_TREE_LAYOUT_PATH,
        "File System Member Tree Layout",
        "Layout selected nodes based on their Java package member relations",
        FileSystemLinkMatcherBuiltIns.FILE_SYSTEM_MEMBER_MATCHER_PATH);
  }
}
