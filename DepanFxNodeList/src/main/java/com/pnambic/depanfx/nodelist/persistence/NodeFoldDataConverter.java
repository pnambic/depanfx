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

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.graph_doc.model.GraphModel;
import com.pnambic.depanfx.graph_doc.model.GraphModels;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeFoldData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeFoldData.NodeNest;
import com.pnambic.depanfx.persistence.BasePersistObjectConverter;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.persistence.PersistTagDataLoader;
import com.pnambic.depanfx.persistence.PersistTagDataResult;
import com.pnambic.depanfx.persistence.plugins.GraphNodePersistencePluginRegistry;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.modxstream.XstreamMarshalContext;
import com.pnambic.modxstream.XstreamUnmarshalContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handle serialization for {@link DepanFxNodeFoldData}.
 */
public class NodeFoldDataConverter
    extends BasePersistObjectConverter<DepanFxNodeFoldData> {

  @SuppressWarnings("unused")
  private static final Logger LOG =
      LoggerFactory.getLogger(NodeFoldDataConverter.class);

  public static final String NODE_FOLD_INFO_TAG =
      "node-fold-info";

  public static final String TOOL_NAME_TAG = "tool-name";

  public static final String TOOL_DESCRIPTION_TAG = "tool-description";

  public static final String GRAPH_DOC_TAG = "graph-doc";

  private static final PersistTagDataLoader.TagDescriptor[] TAG_DATA_DESCR =
      new PersistTagDataLoader.TagDescriptor[] {
          new PersistTagDataLoader.TagDescriptor(
              TOOL_NAME_TAG, String.class),
          new PersistTagDataLoader.TagDescriptor(
              TOOL_DESCRIPTION_TAG, String.class),
          new PersistTagDataLoader.TagDescriptor(
              GRAPH_DOC_TAG, DepanFxWorkspaceResource.class)
      };

  private static final Map<String, String> TAGS_ALIAS =
      new HashMap<>();

  private static final PersistTagDataLoader TAG_LOADER =
      new PersistTagDataLoader(TAG_DATA_DESCR, TAGS_ALIAS);

  private static final String[] META_TAGS = new String[] {
      TOOL_NAME_TAG, TOOL_DESCRIPTION_TAG, GRAPH_DOC_TAG
  };

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
      DepanFxNodeFoldData.class,
      DepanFxNodeFoldData.NodeNest.class
    };

  public static void installIn(
      PersistDocumentTransportBuilder builder,
      GraphNodePersistencePluginRegistry graphNodeRegistry) {
    builder.addConverter(new NodeFoldDataConverter());
    builder.addAlias("node-fold", DepanFxNodeFoldData.NodeNest.class);

    graphNodeRegistry.applyExtensions(builder, GraphNode.class);
    graphNodeRegistry.applyExtensions(
        builder, DepanFxWorkspaceResource.class);
  }

  @Override
  public Class<?> forType() {
    return DepanFxNodeFoldData.class;
  }

  @Override
  public Class<?>[] getAllowTypes() {
    return ALLOW_TYPES;
  }

  @Override
  public String getTag() {
    return NODE_FOLD_INFO_TAG;
  }

  @Override
  public void marshal(XstreamMarshalContext dstContext, Object source) {
    DepanFxNodeFoldData foldInfo = (DepanFxNodeFoldData) source;
    marshalObject(dstContext, TOOL_NAME_TAG, foldInfo.getToolName());
    marshalObject(dstContext, TOOL_DESCRIPTION_TAG, foldInfo.getToolDescription());
    marshalObject(dstContext, GRAPH_DOC_TAG, foldInfo.getGraphDocResource());

    foldInfo.streamNodeNests()
        .forEach(nest -> marshalObject(dstContext, nest));
  }

  @Override
  public DepanFxNodeFoldData unmarshal(
      XstreamUnmarshalContext srcContext) {

    PersistTagDataResult metaData =
        new PersistTagDataResult(TAG_LOADER.loadData(META_TAGS, srcContext));

    String toolName = metaData.getString(TOOL_NAME_TAG);
    String toolDescr = metaData.getString(TOOL_DESCRIPTION_TAG);

    @SuppressWarnings("unchecked")
    DepanFxWorkspaceResource<GraphDocument> graphDocRsrc =
        metaData.getObject(GRAPH_DOC_TAG, DepanFxWorkspaceResource.class);

    // Install the basis for model mapping before loading nodes and infos.
    GraphModel graphModel = graphDocRsrc.getResource().getGraph();
    srcContext.putContextValue(GraphModel.class, graphModel);

    List<DepanFxNodeFoldData.NodeNest> foldNests = new ArrayList<>();
    while (srcContext.hasMoreChildren()) {
      if (unmarshalOne(srcContext) instanceof DepanFxNodeFoldData.NodeNest nest) {
        foldNests.add(forUnmarshal(nest, graphModel));
      }
    };

    return new DepanFxNodeFoldData(
        toolName, toolDescr, graphDocRsrc, foldNests);
  }

  private NodeNest forUnmarshal(NodeNest nest, GraphModel graphModel) {
    return new NodeNest(
        GraphModels.mapGraphNode(nest.getMemberNode(), graphModel),
        GraphModels.mapGraphNode(nest.getNestNode(), graphModel)
    );
  }
}
