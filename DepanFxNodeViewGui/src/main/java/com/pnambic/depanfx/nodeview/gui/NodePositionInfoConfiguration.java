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
package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.graph.nodeanno.DepanFxAnnotationIndexData;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxNodeInfoProperty;
import com.pnambic.depanfx.nodelist.gui.columns.infos.DepanFxNodeInfoColumnData;
import com.pnambic.depanfx.nodelist.gui.columns.infos.DepanFxNodeKeyColumnBuiltIns;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSectionBuiltIns;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxBaseColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxBaseSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxInfoStoreData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeInfoData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInProject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Provide the built-in columns for node position.
 */
@Configuration
public class NodePositionInfoConfiguration {

  public static final Path NODE_VIEW_COLUMNS_PATH =
      DepanFxNodeViewData.NODE_VIEW_TOOL_PATH.resolve("Columns");

  public static final Path NODE_VIEW_TABLE_VIEWS_PATH =
      DepanFxNodeViewData.NODE_VIEW_TOOL_PATH.resolve(
          DepanFxNodeListTableViewData.TABLE_VIEWS_TOOL_DIR);

  public static final Path NODE_VIEW_LOCATION_TABLE_VIEW_PATH =
      NODE_VIEW_TABLE_VIEWS_PATH.resolve(
          DepanFxNodeListTableViewData.TABLE_VIEW_CONTEXT_RESOURCE_NAME);

  private static final String X_POS_LABEL = "X Pos";

  private static final String X_POS_DESCR = "Node's x position in graph.";

  public static final Path X_POS_COLUMNS_PATH =
      NODE_VIEW_COLUMNS_PATH.resolve(X_POS_LABEL);

  private static final String Y_POS_LABEL = "Y Pos";

  private static final String Y_POS_DESCR = "Node's y position in graph.";

  public static final Path Y_POS_COLUMNS_PATH =
      NODE_VIEW_COLUMNS_PATH.resolve(Y_POS_LABEL);

  private static final String Z_POS_LABEL = "Z Pos";

  private static final String Z_POS_DESCR = "Node's z position in graph.";

  public static final Path Z_POS_COLUMNS_PATH =
      NODE_VIEW_COLUMNS_PATH.resolve(Z_POS_LABEL);

  private static final String NODE_POSITION_ANNOTATION_SPEC_NAME =
      "Node Position Properties";

  private static final String NODE_POSITION_ANNOTATION_SPEC_KEY =
      DepanFxNodeLocationData.class.getName();

  private static final String NODE_POSITION_ANNOTATION_INDEX_NAME =
      "Node Position Info";

  private static final String NODE_POSITION_ANNOTATION_INDEX_DESCR =
      "Node position info.";

  public static final Path NODE_POSITION_ANNOTATION_INDEX_PATH =
      DepanFxNodeInfoData.NODE_INFO_TOOL_PATH.resolve(
          NODE_POSITION_ANNOTATION_INDEX_NAME);

  protected static final String NODE_POSITION_STORE_NAME =
      "Node Position Store";

  protected static final String NODE_POSITION_STORE_DESCR =
      "Node position store.";

  public static final Path NODE_POSITION_STORE_PATH =
      DepanFxNodeInfoData.NODE_INFO_TOOL_PATH.resolve(
          NODE_POSITION_STORE_NAME);

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeInfoColumnData>
  xNodePositionInfoColumnData(NodePositionInfoContribution infoContrib) {

    return buildNodePositionContrib(
        NODE_VIEW_COLUMNS_PATH.resolve(X_POS_LABEL),
        X_POS_LABEL, X_POS_DESCR, infoContrib,
        NodePositionInfoContribution.X_POS_PROPERTY);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeInfoColumnData>
  yNodePositionInfoColumnData(NodePositionInfoContribution infoContrib) {

    return buildNodePositionContrib(
        NODE_VIEW_COLUMNS_PATH.resolve(Y_POS_LABEL),
        Y_POS_LABEL, Y_POS_DESCR, infoContrib,
        NodePositionInfoContribution.Y_POS_PROPERTY);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeInfoColumnData>
  zNodePositionInfoColumnData(NodePositionInfoContribution infoContrib) {

    return buildNodePositionContrib(
        NODE_VIEW_COLUMNS_PATH.resolve(Z_POS_LABEL),
        Z_POS_LABEL, Z_POS_DESCR, infoContrib,
        NodePositionInfoContribution.Z_POS_PROPERTY);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeListTableViewData>
  nodeViewLocation( ) {
    return new TableViewBuiltin(NODE_VIEW_LOCATION_TABLE_VIEW_PATH);
  }

  @Bean DepanFxBuiltInContribution<DepanFxAnnotationIndexData>
  nodePositionInfoAnnotationIndex(NodePositionInfoContribution nodePosInfo) {
    DepanFxAnnotationIndexData.AnnotationSpecification nodePosProps =
        new DepanFxAnnotationIndexData.AnnotationSpecification(
            NODE_POSITION_ANNOTATION_SPEC_NAME,
            NODE_POSITION_ANNOTATION_SPEC_KEY,
            nodePosInfo);

    List<DepanFxAnnotationIndexData.AnnotationSpecification> nodePosInfos =
        new ArrayList<>();
    nodePosInfos.add(nodePosProps);

    DepanFxAnnotationIndexData annoIndex = new DepanFxAnnotationIndexData(
        NODE_POSITION_ANNOTATION_INDEX_NAME,
        NODE_POSITION_ANNOTATION_INDEX_DESCR,
        nodePosInfos);

    return new DepanFxBuiltInContribution.Simple<>(
        NODE_POSITION_ANNOTATION_INDEX_PATH, annoIndex);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxInfoStoreData>
  nodePositionInfoStore() {
    return new DepanFxBuiltInContribution.Dependent<DepanFxInfoStoreData>(
        NODE_POSITION_STORE_PATH) {

      @Override
      protected DepanFxInfoStoreData buildDocument(
          DepanFxBuiltInProject project) {
        DepanFxWorkspaceResource<DepanFxAnnotationIndexData> annoIndexRsc =
            getResource(project, NODE_POSITION_ANNOTATION_INDEX_PATH);
        return new DepanFxInfoStoreData(
            NODE_POSITION_STORE_NAME, NODE_POSITION_STORE_DESCR, annoIndexRsc);
      }
    };
  }

  private DepanFxBuiltInContribution<DepanFxNodeInfoColumnData>
  buildNodePositionContrib(
      Path columnPath,
      String axisLabel, String axisDescr,
      NodePositionInfoContribution infoContrib,
      DepanFxNodeInfoProperty axisProperty) {

    return new DepanFxBuiltInContribution.Dependent<DepanFxNodeInfoColumnData>(columnPath) {

      @Override
      protected DepanFxNodeInfoColumnData buildDocument(
          DepanFxBuiltInProject project) {
        DepanFxWorkspaceResource<DepanFxInfoStoreData> nodeIdStore =
            getResource(project, NODE_POSITION_STORE_PATH);

        return buildNodePositionColumn(
            X_POS_LABEL, X_POS_DESCR,
            nodeIdStore, infoContrib,
            NodePositionInfoContribution.X_POS_PROPERTY);
      }
    };
  }

  private DepanFxNodeInfoColumnData buildNodePositionColumn(
      String axisLabel, String axisDescr,
      DepanFxWorkspaceResource<DepanFxInfoStoreData> infoStoreRsrc,
      NodePositionInfoContribution infoContrib,
      DepanFxNodeInfoProperty axisProperty) {
    return new DepanFxNodeInfoColumnData(
        axisLabel, axisDescr, axisLabel, 6,
        infoStoreRsrc, NODE_POSITION_ANNOTATION_SPEC_KEY, axisProperty);
  }

  private final class TableViewBuiltin extends
      DepanFxBuiltInContribution.Dependent<DepanFxNodeListTableViewData> {

    public TableViewBuiltin(Path path) {
      super(path);
    }

    @Override
    protected DepanFxNodeListTableViewData buildDocument(
        DepanFxBuiltInProject project) {

      // Flat and Members Sections
      List<DepanFxWorkspaceResource<? extends DepanFxBaseSectionData>>
          sectionRsrcs = new ArrayList<>();
      sectionRsrcs.add(getResource(project,
          DepanFxNodeListSectionBuiltIns.MEMBER_TREE_SECTION_PATH));
      sectionRsrcs.add(getResource(project,
          DepanFxNodeListSectionBuiltIns.SIMPLE_SECTION_TOOL_PATH));

      // Node Kind Column
      List<DepanFxWorkspaceResource<? extends DepanFxBaseColumnData>>
          columnRsrcs = new ArrayList<>();
      columnRsrcs.add(getResource(project,
          DepanFxNodeKeyColumnBuiltIns.KIND_KEY_COLUMN_TOOL_PATH));

      // Position Columns
      columnRsrcs.add(getResource(project, X_POS_COLUMNS_PATH));
      columnRsrcs.add(getResource(project, Y_POS_COLUMNS_PATH));
      columnRsrcs.add(getResource(project, Z_POS_COLUMNS_PATH));

      return new DepanFxNodeListTableViewData(
          "Node View Table View",
          "Node list view for node view by member relations"
              + " with node positions.",
          sectionRsrcs, columnRsrcs);
    }
  }
}
