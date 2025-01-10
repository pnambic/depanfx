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
package com.pnambic.depanfx.nodeview.builtins;

import com.pnambic.depanfx.graph.context.BaseContextDefinition;
import com.pnambic.depanfx.nodefilters.model.DepanFxNodeFiltersBuiltIns;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
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
import java.util.Collections;

@Configuration
public class DepanFxGraphNodeViewBuiltIns {

  private static final String ALL_NODES_DOC_NAME = "All Nodes Display";

  private static final String ALL_NODES_NAME = "All Nodes";

  private static final String ALL_NODES_DESCR = "All nodes.";

  public static final Path ALL_NODES_DISPLAY_DOC_PATH =
      DepanFxNodeViewNodeDisplayData.NODE_DISPLAY_TOOL_PATH
          .resolve(ALL_NODES_DOC_NAME);

  @Autowired
  public DepanFxGraphNodeViewBuiltIns() {
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeViewNodeDisplayData>
      allNodeDisplayDoc() {

    return new AllNodeDisplayContribution(ALL_NODES_DISPLAY_DOC_PATH);
  }

  private static class AllNodeDisplayContribution
      extends DepanFxBuiltInContribution.Dependent<DepanFxNodeViewNodeDisplayData> {

    private AllNodeDisplayContribution(Path path) {
      super(path);
    }

    @Override
    protected DepanFxNodeViewNodeDisplayData buildDocument(
        DepanFxBuiltInProject project) {

      DepanFxNodeDisplayData nodeDisplayData =
          DepanFxNodeDisplayData.buildSimpleNodeDisplayData();

      DepanFxWorkspaceResource<DepanFxBaseFilterData> allNodeFilter =
          getResource(project,
              DepanFxNodeFiltersBuiltIns.ALL_NODES_FILTER_DOC_PATH);

      NodeDisplayEntry nodeDisplayEntry =
          new NodeDisplayEntry(ALL_NODES_NAME, allNodeFilter, nodeDisplayData);

      DepanFxNodeViewNodeDisplayData result =
          new DepanFxNodeViewNodeDisplayData(
              ALL_NODES_NAME, ALL_NODES_DESCR,
              BaseContextDefinition.MODEL_ID,
              Collections.singletonList(nodeDisplayEntry));
      return result;
    };
  }
}
