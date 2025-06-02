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

import com.pnambic.depanfx.graph.nodeanno.DepanFxAnnotationIndexData;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.annos.DepanFxKeyPropertyStore;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

/**
 * An info store retaining infos for nodes of a specific graph.
 */
public class DepanFxAnnotationStoreData extends DepanFxInfoStoreData {

  private final DepanFxWorkspaceResource<GraphDocument> graphDocRsrc;

  private final DepanFxKeyPropertyStore infoStore;

  private DepanFxAnnotationStoreData(
      String toolName, String toolDescription,
      DepanFxWorkspaceResource<GraphDocument> graphDocRsrc,
      DepanFxWorkspaceResource<DepanFxAnnotationIndexData> annoIndexRsrc,
      DepanFxKeyPropertyStore infoStore) {
    super(toolName, toolDescription, annoIndexRsrc);
    this.graphDocRsrc = graphDocRsrc;
    this.infoStore = infoStore;
  }

  public static DepanFxAnnotationStoreData buildAnnotationStore(
    String toolName, String toolDescription,
    DepanFxWorkspaceResource<GraphDocument> graphDocRsrc) {
    return new DepanFxAnnotationStoreData(
        toolName, toolDescription,
        graphDocRsrc, null, DepanFxKeyPropertyStore.forNew());
  }

  public static DepanFxAnnotationStoreData forUnmarshal(
      DepanFxAnnotationStoreData result) {
    if (result.infoStore == null) {
      result = new DepanFxAnnotationStoreData(
          result.getToolName(), result.getToolDescription(),
          result.graphDocRsrc, result.getAnnotationResource(),
          DepanFxKeyPropertyStore.forNew());
    }
    return result;
  }

  public GraphDocument getGraphDoc() {
    return graphDocRsrc.getResource();
  }

  public DepanFxAnnotationStoreData buildUpdate(
      String toolName, String toolDescription,
      DepanFxWorkspaceResource<DepanFxAnnotationIndexData> annoIndexRsrc) {
    return new DepanFxAnnotationStoreData(
        toolName, toolDescription, graphDocRsrc, annoIndexRsrc, infoStore);
  }

  public DepanFxInfoRegistry.PropertyStore getPropertyStore(String infoKey) {
    return infoStore.getPropertyStore(infoKey);
  }

  @Override
  public DepanFxInfoStoreData forUpdate() {
    return new DepanFxAnnotationStoreData(
        getToolName(), getToolDescription(),
        graphDocRsrc, annoIndexRsrc, infoStore);
  }

  private Object readResolve() {
    return DepanFxAnnotationStoreData.forUnmarshal(this);
  }
}
