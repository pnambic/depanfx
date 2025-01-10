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
package com.pnambic.depanfx.java.nodeview;

import com.pnambic.depanfx.filesystem.context.FileSystemContextDefinition;
import com.pnambic.depanfx.filesystem.nodelist.link.FileSystemNodeKindFilterBuiltIns;
import com.pnambic.depanfx.graph.context.ContextNodeKindId;
import com.pnambic.depanfx.java.context.JavaContextDefinition;
import com.pnambic.depanfx.java.context.JavaContextModelId;
import com.pnambic.depanfx.java.nodelist.link.JavaNodeKindFilterBuiltIns;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxJoglColor;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxJoglShape;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewNodeDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewNodeDisplayData.NodeDisplayEntry;
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
public class DepanFxJavaNodeViewBuiltIns {

  private static final String JAVA_NODE_KINDS_NAME = "Java Nodes by Node Kind";

  private static final String JAVA_NODE_KINDS_DESCR =
      "Java nodes, separated by Java node kind.";

  public static final Path JAVA_NODE_KIND_DISPLAY_DOC_PATH =
      DepanFxNodeViewNodeDisplayData.NODE_DISPLAY_PATH
        .resolve(JavaContextModelId.JAVA_KEY)
        .resolve(DepanFxNodeViewNodeDisplayData.NODE_DISPLAY_CONTEXT_RESOURCE_NAME);

  @Autowired
  public DepanFxJavaNodeViewBuiltIns() {
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeViewNodeDisplayData>
  javaNodeKindDisplayDoc() {
    return new DepanFxBuiltInContribution.Dependent<>(
        JAVA_NODE_KIND_DISPLAY_DOC_PATH) {

      @Override
      protected DepanFxNodeViewNodeDisplayData
      buildDocument(DepanFxBuiltInProject project) {
        List<NodeDisplayEntry> kindDisplay = new ArrayList<>();

        addFileSystemDisplay(kindDisplay, project);
        addNodeKindDisplay(kindDisplay, project);

        return new DepanFxNodeViewNodeDisplayData(
            JAVA_NODE_KINDS_NAME, JAVA_NODE_KINDS_DESCR,
            JavaContextDefinition.MODEL_ID,
            kindDisplay);
      }
    };
  }

  private static void addFileSystemDisplay(
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

  private static void addNodeKindDisplay(
      List<NodeDisplayEntry> kindDisplay, DepanFxBuiltInProject project) {

    kindDisplay.add(buildJavaDisplayEntry(project,
        JavaContextDefinition.CLASS_NKID,
        DepanFxJoglShape.ROUNDED_RECTANGLE,
        DepanFxJoglColor.of(Color.BLACK)));

    kindDisplay.add(buildJavaDisplayEntry(project,
        JavaContextDefinition.FIELD_NKID,
        DepanFxJoglShape.RECTANGLE,
        DepanFxJoglColor.of(Color.GREEN)));

    kindDisplay.add(buildJavaDisplayEntry(project,
        JavaContextDefinition.METHOD_NKID,
        DepanFxJoglShape.ELLIPSE,
        DepanFxJoglColor.of(Color.LIGHTSALMON)));

    kindDisplay.add(buildJavaDisplayEntry(project,
        JavaContextDefinition.MODULE_NKID,
        DepanFxJoglShape.HEXAGON,
        DepanFxJoglColor.of(Color.SKYBLUE)));

    kindDisplay.add(buildJavaDisplayEntry(project,
        JavaContextDefinition.PACKAGE_NKID,
        DepanFxJoglShape.CIRCLE,
        DepanFxJoglColor.of(Color.BLUE)));

    kindDisplay.add(buildJavaDisplayEntry(project,
        JavaContextDefinition.PARAMETER_NKID,
        DepanFxJoglShape.RECTANGLE,
        DepanFxJoglColor.of(Color.LIGHTPINK)));
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

  private static NodeDisplayEntry buildJavaDisplayEntry(
      DepanFxBuiltInProject project,
      ContextNodeKindId nodeKind,
      DepanFxJoglShape shape,
      DepanFxJoglColor color) {

    return buildDisplayEntry(
        project,
        JavaNodeKindFilterBuiltIns.JAVA_NODE_FILTERS_PATH,
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
                JAVA_NODE_KIND_DISPLAY_DOC_PATH, kindFilterPath));

     return new NodeDisplayEntry(
            kindId, nodeFilterRsrc,
            DepanFxNodeDisplayData.buildSimpleNodeDisplayData(shape, color));
  }
}
