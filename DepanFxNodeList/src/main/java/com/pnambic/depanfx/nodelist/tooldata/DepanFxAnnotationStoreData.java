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
package com.pnambic.depanfx.nodelist.tooldata;

import com.pnambic.depanfx.base.tooldata.DepanFxBaseToolData;
import com.pnambic.depanfx.graph.nodeanno.DepanFxAnnotationIndexData;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

public class DepanFxAnnotationStoreData extends DepanFxBaseToolData {

  public static final String ANNOTATION_STORE_TOOL_EXT = "dasti";

  private final DepanFxWorkspaceResource<GraphDocument> graphDocRsrc;

  private final DepanFxWorkspaceResource<DepanFxAnnotationIndexData> annoIndexRsrc;

  public DepanFxAnnotationStoreData(
      String toolName, String toolDescription,
      DepanFxWorkspaceResource<GraphDocument> graphDocRsrc,
      DepanFxWorkspaceResource<DepanFxAnnotationIndexData> annoIndexRsrc) {
    super(toolName, toolDescription);
    this.graphDocRsrc = graphDocRsrc;
    this.annoIndexRsrc = annoIndexRsrc;
  }

  public DepanFxAnnotationStoreData buildUpdate(
      String toolName, String toolDescription,
      DepanFxWorkspaceResource<DepanFxAnnotationIndexData> annoIndexRsrc) {
    return new DepanFxAnnotationStoreData(
        toolName, toolDescription, graphDocRsrc, annoIndexRsrc);
  }

  public GraphDocument getGraphDoc() {
    return graphDocRsrc.getResource();
  }

  public DepanFxAnnotationIndexData getAnnotationIndex() {
    return annoIndexRsrc.getResource();
  }
}
