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
package com.pnambic.depanfx.nodelist.persistence;

import com.pnambic.depanfx.graph.nodeanno.DepanFxAnnotationIndexData;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxNodeKeyInfoContribution;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxInfoStoreData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeInfoData;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInProject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class AnnotationStoreDataConfiguration {

  private static final String NODE_ID_ANNOTATION_SPEC_NAME = "Node Id properties";

  private static final String NODE_ID_ANNOTATION_SPEC_DESCR = "Node id properties.";

  private static final String NODE_ID_ANNOTATION_INDEX_NAME = "Node Id Info";

  private static final String NODE_ID_ANNOTATION_INDEX_DESCR = "Node id info";

  private static final Path NODE_ID_ANNOTATION_INDEX_PATH = 
      DepanFxNodeInfoData.NODE_INFO_TOOL_PATH.resolve(
          NODE_ID_ANNOTATION_INDEX_NAME);

  private static final String NODE_ID_STORE_NAME = "Node Id Store";

  private static final String NODE_ID_STORE_DESCR = "Node id store.";

  public static final Path NODE_ID_STORE_PATH =
      DepanFxNodeInfoData.NODE_INFO_TOOL_PATH.resolve(NODE_ID_STORE_NAME);

  @Bean DepanFxBuiltInContribution<DepanFxAnnotationIndexData>
  nodeIdInfoAnnotationIndex(DepanFxNodeKeyInfoContribution nodeKeyInfo) {
    DepanFxAnnotationIndexData.AnnotationSpecification nodeKeyProps =
        new DepanFxAnnotationIndexData.AnnotationSpecification(
            NODE_ID_ANNOTATION_SPEC_NAME,
            NODE_ID_ANNOTATION_SPEC_DESCR,
            nodeKeyInfo);

    List<DepanFxAnnotationIndexData.AnnotationSpecification> nodeKeyInfos =
        new ArrayList<>();
    nodeKeyInfos.add(nodeKeyProps);

    DepanFxAnnotationIndexData annoIndex = new DepanFxAnnotationIndexData(
        NODE_ID_ANNOTATION_INDEX_NAME, NODE_ID_ANNOTATION_INDEX_DESCR,
        nodeKeyInfos);

    return new DepanFxBuiltInContribution.Simple<>(
        NODE_ID_ANNOTATION_INDEX_PATH, annoIndex);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxInfoStoreData>
  nodeIdInfoAnnotationStore() {
    return new DepanFxBuiltInContribution.Dependent<DepanFxInfoStoreData>(
        NODE_ID_STORE_PATH) {

      @Override
      protected DepanFxInfoStoreData buildDocument(
          DepanFxBuiltInProject project) {
        DepanFxWorkspaceResource<DepanFxAnnotationIndexData> annoIndexRsc =
            getResource(project, NODE_ID_ANNOTATION_INDEX_PATH);
        return new DepanFxInfoStoreData(
            NODE_ID_STORE_NAME, NODE_ID_STORE_DESCR, annoIndexRsc);
      }
    };
  }
}
