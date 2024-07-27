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

import com.pnambic.depanfx.filesystem.nodelist.link.FileSystemLinkMatcherBuiltIns;
import com.pnambic.depanfx.graph.context.BaseContextDefinition;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxJoglColor;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineArrow;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineDirection;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineForm;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineLabel;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineStyle;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData.LinkDisplayEntry;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInProject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.scene.paint.Color;

@Configuration
public class DepanFxFileSystemLinkViewBuiltIns {

  private static final String FILE_SYSTEM_EDGES_NAME =
      "File System Edge Display";

  private static final String FILE_SYSTEM_EDGE_RELATION_NAME =
      "Files System Edges by Relation Type.";

  private static final String FILE_SYSTEM_EDGE_RELATION_DESCR =
      "Files system edges, separated by relation type.";

  public static final Path FILE_SYSTEM_EDGE_RELATION_DISPLAY_DOC_PATH =
      DepanFxNodeViewData.NODE_VIEW_TOOL_PATH
          .resolve(FILE_SYSTEM_EDGES_NAME);

  @Autowired
  public DepanFxFileSystemLinkViewBuiltIns() {
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeViewLinkDisplayData>
      fileSystemMembersEdgeLinkDisplayDoc() {
    return new DepanFxBuiltInContribution.Dependent<>(
        FILE_SYSTEM_EDGE_RELATION_DISPLAY_DOC_PATH) {

      @Override
      protected DepanFxNodeViewLinkDisplayData
          buildDocument(DepanFxBuiltInProject project) {
        return buildFileSystemLinkDisplayData(project);
      }
    };
  }

  /**
   * Now that we have an established project for built ins,
   * build the all edges display built in using the all edges matcher built in.
   */
  private static DepanFxNodeViewLinkDisplayData buildFileSystemLinkDisplayData(
      DepanFxBuiltInProject project) {

    DepanFxLineDisplayData directoryLine = new DepanFxLineDisplayData(
        DepanFxLineForm.STRAIGHT,
        DepanFxLineStyle.SOLID,
        DepanFxJoglColor.of(Color.GREEN),
        1.5d,  // width
        DepanFxLineLabel.DEFAULT,
        DepanFxLineArrow.NONE,
        DepanFxLineArrow.OPEN,
        DepanFxLineDirection.FORWARD);
    LinkDisplayEntry directoryDisplayEntry =
        buildDisplayEntry(project,
            FILE_SYSTEM_EDGE_RELATION_DISPLAY_DOC_PATH,
            FileSystemLinkMatcherBuiltIns.DIRECTORY_NAME,
            FileSystemLinkMatcherBuiltIns.FILE_SYSTEM_DIRECTORY_MATCHER_PATH,
            directoryLine);

    DepanFxLineDisplayData fileLine = new DepanFxLineDisplayData(
        DepanFxLineForm.STRAIGHT,
        DepanFxLineStyle.SOLID,
        DepanFxJoglColor.of(Color.YELLOW),
        1.0d,  // width
        DepanFxLineLabel.DEFAULT,
        DepanFxLineArrow.NONE,
        DepanFxLineArrow.OPEN,
        DepanFxLineDirection.FORWARD);
    LinkDisplayEntry fileDisplayEntry =
        buildDisplayEntry(project,
            FILE_SYSTEM_EDGE_RELATION_DISPLAY_DOC_PATH,
            FileSystemLinkMatcherBuiltIns.FILE_NAME,
            FileSystemLinkMatcherBuiltIns.FILE_SYSTEM_FILE_MATCHER_PATH,
            fileLine);

    List<LinkDisplayEntry> displayInfo = new ArrayList<>();
    displayInfo.add(fileDisplayEntry);
    displayInfo.add(directoryDisplayEntry);

    DepanFxNodeViewLinkDisplayData result =
        new DepanFxNodeViewLinkDisplayData(
            FILE_SYSTEM_EDGE_RELATION_NAME,
            FILE_SYSTEM_EDGE_RELATION_DESCR,
            BaseContextDefinition.MODEL_ID, displayInfo);
    return result;
  };

  private static LinkDisplayEntry buildDisplayEntry(
      DepanFxBuiltInProject project, Path displayDataPath,
      String entryName, Path matcherPath, DepanFxLineDisplayData lineDisplay) {

    Optional<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
        matcherRsrc = project.getResource(matcherPath);
    DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> directoryMatcher =
        matcherRsrc.orElseThrow(() ->
            new DepanFxBuiltInContribution.MissingDependencyException(
                displayDataPath, matcherPath));

    return new LinkDisplayEntry(
        entryName, directoryMatcher, lineDisplay);
  }
}
