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

import com.pnambic.depanfx.filesystem.context.FileSystemContextDefinition;
import com.pnambic.depanfx.filesystem.context.FileSystemContextModelId;
import com.pnambic.depanfx.filesystem.nodelist.link.FileSystemLinkMatcherBuiltIns;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcherSequenceDocument;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxJoglColor;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineArrow;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineDirection;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineForm;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineLabel;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineStyle;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData.LinkDisplayEntry;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInProject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.scene.paint.Color;

@Configuration
public class DepanFxFileSystemLinkViewBuiltIns {

  private static final String FILE_SYSTEM_EDGE_RELATION_DISPLAY_NAME =
      "Files System Edges Display by Relation Type.";

  private static final String FILE_SYSTEM_EDGE_RELATION_DISPLAY_DESCR =
      "Files system edges display, separated by relation type.";

  public static final Path FILE_SYSTEM_EDGE_RELATION_DISPLAY_DOC_PATH =
      DepanFxNodeViewLinkDisplayData.EDGE_DISPLAY_TOOL_PATH
          .resolve(FileSystemContextModelId.FILE_SYSTEM_KEY)
          .resolve(DepanFxNodeViewLinkDisplayData.EDGE_DISPLAY_CONTEXT_RESOURCE_NAME);

  private static final String FILE_SYSTEM_EDGE_RELATION_VISIBILITY_NAME =
      "Files System Edges Visibility by Relation Type.";

  private static final String FILE_SYSTEM_EDGE_RELATION_VISIBILITY_DESCR =
      "Files system edges visibility, separated by relation type.";

  public static final Path FILE_SYSTEM_EDGE_RELATION_VISIBILITY_DOC_PATH =
      DepanFxLinkMatcherSequenceDocument.LINK_MATCHER_SEQUENCE_TOOL_PATH
          .resolve(FileSystemContextModelId.FILE_SYSTEM_KEY)
          .resolve(DepanFxLinkMatcherSequenceDocument.EDGE_VISIBILITY_CONTEXT_RESOURCE_NAME);

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

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherSequenceDocument>
  fileSystemMembersEdgeLinkVisibleDoc(
      @Qualifier("fileSystemMembersEdgeLinkDisplayDoc")
      DepanFxBuiltInContribution<DepanFxNodeViewLinkDisplayData> displayContrib) {

    return new DepanFxBuiltInContribution.Dependent<>(
        FILE_SYSTEM_EDGE_RELATION_VISIBILITY_DOC_PATH) {

      @Override
      protected DepanFxLinkMatcherSequenceDocument
          buildDocument(DepanFxBuiltInProject project) {

        return new DepanFxLinkMatcherSequenceDocument(
            FILE_SYSTEM_EDGE_RELATION_VISIBILITY_NAME,
            FILE_SYSTEM_EDGE_RELATION_VISIBILITY_DESCR,
            FileSystemContextDefinition.MODEL_ID,
            getDisplayFilters(displayContrib));
      }

      private List<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
      getDisplayFilters(
          DepanFxBuiltInContribution<DepanFxNodeViewLinkDisplayData> displayContrib) {
        if (displayContrib.getDocument() == null) {
          throw new DepanFxBuiltInContribution.MissingDependencyException(
              getPath(), displayContrib.getPath());
        }
       return displayContrib.getDocument().streamLinkDisplay()
              .map(r -> r.getLinkRsrc())
              .collect(Collectors.toList());
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
            FILE_SYSTEM_EDGE_RELATION_DISPLAY_NAME,
            FILE_SYSTEM_EDGE_RELATION_DISPLAY_DESCR,
            FileSystemContextDefinition.MODEL_ID, displayInfo);
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
