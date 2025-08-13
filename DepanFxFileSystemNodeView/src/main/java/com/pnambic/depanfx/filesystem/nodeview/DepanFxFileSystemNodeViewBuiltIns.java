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
import com.pnambic.depanfx.filesystem.edgematchers.link.FileSystemNodeKindFilterBuiltIns;
import com.pnambic.depanfx.graph.context.ContextNodeKindId;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxNodeFilterSequenceData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxJoglColor;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxJoglShape;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewNodeDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewNodeDisplayData.NodeDisplayEntry;
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
public class DepanFxFileSystemNodeViewBuiltIns {

  private static final String FILE_SYSTEM_NODE_KINDS_DISPLAY_NAME =
      "File System Nodes Display by Node Kind";

  private static final String FILE_SYSTEM_NODE_KINDS_DISPLAY_DESCR =
      "File system nodes display, separated by node kind.";

  public static final Path FILE_SYSTEM_NODE_KIND_DISPLAY_DOC_PATH =
      DepanFxNodeViewNodeDisplayData.NODE_DISPLAY_TOOL_PATH
          .resolve(FileSystemContextModelId.FILE_SYSTEM_KEY)
          .resolve(DepanFxNodeViewNodeDisplayData.NODE_DISPLAY_CONTEXT_RESOURCE_NAME);

  private static final String FILE_SYSTEM_NODE_KINDS_VISIBILITY_NAME =
      "File System Nodes Visibility by Node Kind";

  private static final String FILE_SYSTEM_NODE_KINDS_VISIBILITY_DESCR =
      "File system nodes visibility, separated by node kind.";

  public static final Path FILE_SYSTEM_NODE_KIND_VISIBILITY_DOC_PATH =
      DepanFxBaseFilterData.NODE_FILTERS_TOOL_PATH
          .resolve(FileSystemContextModelId.FILE_SYSTEM_KEY)
          .resolve(DepanFxBaseFilterData.NODE_VSIBILITY_CONTEXT_RESOURCE_NAME);

  @Autowired
  public DepanFxFileSystemNodeViewBuiltIns() {
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeViewNodeDisplayData>
  fileSystemNodeKindDisplayDoc() {
    return new DepanFxBuiltInContribution.Dependent<>(
        FILE_SYSTEM_NODE_KIND_DISPLAY_DOC_PATH) {

      @Override
      protected DepanFxNodeViewNodeDisplayData
      buildDocument(DepanFxBuiltInProject project) {
        List<NodeDisplayEntry> kindDisplay = new ArrayList<>();

        addFileSystemDisplay(kindDisplay, project);

        return new DepanFxNodeViewNodeDisplayData(
            FILE_SYSTEM_NODE_KINDS_DISPLAY_NAME,
            FILE_SYSTEM_NODE_KINDS_DISPLAY_DESCR,
            FileSystemContextDefinition.MODEL_ID,
            kindDisplay);
      }
    };
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeFilterSequenceData>
  fileSystemNodeKinkAvailableDoc(
      @Qualifier("javaNodeKindDisplayDoc")
      DepanFxBuiltInContribution<DepanFxNodeViewNodeDisplayData> displayContrib) {
    return new DepanFxBuiltInContribution.Dependent<>(
        FILE_SYSTEM_NODE_KIND_VISIBILITY_DOC_PATH) {

          @Override
          protected DepanFxNodeFilterSequenceData buildDocument(
              DepanFxBuiltInProject project) {

            return new DepanFxNodeFilterSequenceData(
                FILE_SYSTEM_NODE_KINDS_VISIBILITY_NAME,
                FILE_SYSTEM_NODE_KINDS_VISIBILITY_DESCR,
                FileSystemContextDefinition.MODEL_ID,
                getDisplayFilters(displayContrib));
          }

          private List<DepanFxWorkspaceResource<DepanFxBaseFilterData>>
          getDisplayFilters(
              DepanFxBuiltInContribution<DepanFxNodeViewNodeDisplayData> displayContrib) {
            if (displayContrib.getDocument() == null) {
              throw new DepanFxBuiltInContribution.MissingDependencyException(
                  getPath(), displayContrib.getPath());
            }
            return displayContrib.getDocument().streamNodeDisplay()
                .map(r -> r.getFilterResource())
                .collect(Collectors.toList());
          }
    };
  }

  /**
   * Resusable by other display builders.
   */
  public static void addFileSystemDisplay(
      List<NodeDisplayEntry> kindDisplay, DepanFxBuiltInProject project) {

    kindDisplay.add(buildFileSystemDisplayEntry(project,
        FileSystemContextDefinition.DIRECTORY_NKID,
        DepanFxJoglShape.ROUNDED_RECTANGLE,
        DepanFxJoglColor.of(Color.ORANGE)));

    kindDisplay.add(buildFileSystemDisplayEntry(project,
        FileSystemContextDefinition.DOCUMENT_NKID,
        DepanFxJoglShape.RECTANGLE,
        DepanFxJoglColor.of(Color.GOLD)));
  }

  private static NodeDisplayEntry buildFileSystemDisplayEntry(
      DepanFxBuiltInProject project,
      ContextNodeKindId nodeKind,
      DepanFxJoglShape shape,
      DepanFxJoglColor color) {

    return buildDisplayEntry(
        project,
        FileSystemNodeKindFilterBuiltIns.FILE_SYSTEM_NODE_FILTERS_PATH,
        nodeKind, shape, color);
  }

  private static NodeDisplayEntry buildDisplayEntry(
      DepanFxBuiltInProject project,
      Path modelDirectoryPath,
      ContextNodeKindId nodeKind,
      DepanFxJoglShape shape,
      DepanFxJoglColor color) {

    String kindId = nodeKind.getNodeKindKey();
    Path kindFilterPath = modelDirectoryPath.resolve(kindId);

    Optional<DepanFxWorkspaceResource<DepanFxBaseFilterData>> optFilterRsrc =
        project.getResource(kindFilterPath);
    DepanFxWorkspaceResource<DepanFxBaseFilterData> nodeFilterRsrc =
        optFilterRsrc.orElseThrow(() ->
            new DepanFxBuiltInContribution.MissingDependencyException(
                FILE_SYSTEM_NODE_KIND_DISPLAY_DOC_PATH, kindFilterPath));

     return new NodeDisplayEntry(
            kindId, nodeFilterRsrc,
            DepanFxNodeDisplayData.buildSimpleNodeDisplayData(shape, color));
  }
}
