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
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.graph_doc.model.GraphModel;
import com.pnambic.depanfx.nodelist.annos.DepanFxKeyPropertyStore;
import com.pnambic.depanfx.nodelist.annos.DepanFxSimpleKeyPropertyStore;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxAnnotationStoreData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxSimpleKeyPropertyStoreConverter;
import com.pnambic.depanfx.persistence.BasePersistObjectConverter;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.persistence.PersistTagDataLoader;
import com.pnambic.depanfx.persistence.PersistTagDataResult;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.modxstream.XstreamMarshalContext;
import com.pnambic.modxstream.XstreamUnmarshalContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Handle serialization for {@link DepanFxAnnotationStoreData}.
 */
public class AnnotationStoreDataConverter
    extends BasePersistObjectConverter<DepanFxAnnotationStoreData> {

  private static final Logger LOG =
      LoggerFactory.getLogger(AnnotationStoreDataConverter.class);

  public static final String ANNOTATION_STORE_INFO_TAG =
      "annotation-store-info";

  public static final String TOOL_NAME_TAG = "tool-name";

  public static final String TOOL_DESCRIPTION_TAG = "tool-description";

  public static final String ANNOTATION_INDEX_TAG = "annotation-index";

  public static final String GRAPH_DOC_TAG = "graph-doc";

  public static final String INFO_STORE_TAG = "info-store";

  private static final PersistTagDataLoader.TagDescriptor[] TAG_DATA_DESCR =
      new PersistTagDataLoader.TagDescriptor[] {
          new PersistTagDataLoader.TagDescriptor(
              TOOL_NAME_TAG, String.class),
          new PersistTagDataLoader.TagDescriptor(
              TOOL_DESCRIPTION_TAG, String.class),
          new PersistTagDataLoader.TagDescriptor(
              ANNOTATION_INDEX_TAG, DepanFxWorkspaceResource.class),
          new PersistTagDataLoader.TagDescriptor(
              GRAPH_DOC_TAG, DepanFxWorkspaceResource.class)
      };

  private static final Map<String, String> TAGS_ALIAS =
      new HashMap<>();
  static {
  }

  private static final PersistTagDataLoader TAG_LOADER =
      new PersistTagDataLoader(TAG_DATA_DESCR, TAGS_ALIAS);

  private static final String[] META_TAGS = new String[] {
      TOOL_NAME_TAG, TOOL_DESCRIPTION_TAG,
      ANNOTATION_INDEX_TAG, GRAPH_DOC_TAG
  };

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
      DepanFxAnnotationStoreData.class
    };

  public static void installIn(PersistDocumentTransportBuilder builder) {
    builder.addConverter(new AnnotationStoreDataConverter());

    DepanFxSimpleKeyPropertyStoreConverter.installIn(builder);
  }

  @Override
  public Class<?> forType() {
    return DepanFxAnnotationStoreData.class;
  }

  @Override
  public Class<?>[] getAllowTypes() {
    return ALLOW_TYPES;
  }

  @Override
  public String getTag() {
    return ANNOTATION_STORE_INFO_TAG;
  }

  @Override
  public void marshal(XstreamMarshalContext dstContext, Object source) {
    DepanFxAnnotationStoreData store = (DepanFxAnnotationStoreData) source;
    marshalObject(dstContext, TOOL_NAME_TAG, store.getToolName());
    marshalObject(dstContext, TOOL_DESCRIPTION_TAG, store.getToolDescription());
    marshalObject(dstContext, ANNOTATION_INDEX_TAG, store.getAnnotationResource());
    marshalObject(dstContext, GRAPH_DOC_TAG, store.getGraphDocResource());

    marshalObject(dstContext, INFO_STORE_TAG, store.getInfoStore());
  }

  @Override
  public DepanFxAnnotationStoreData unmarshal(
      XstreamUnmarshalContext srcContext) {

    PersistTagDataResult metaData =
        new PersistTagDataResult(TAG_LOADER.loadData(META_TAGS, srcContext));

    String toolName = metaData.getString(TOOL_NAME_TAG);
    String toolDescr = metaData.getString(TOOL_DESCRIPTION_TAG);

    @SuppressWarnings("unchecked")
    DepanFxWorkspaceResource<GraphDocument> graphDocRsrc =
        metaData.getObject(GRAPH_DOC_TAG, DepanFxWorkspaceResource.class);

    @SuppressWarnings("unchecked")
    DepanFxWorkspaceResource<DepanFxAnnotationIndexData> annoIndexRsrc =
        metaData.getObject(ANNOTATION_INDEX_TAG, DepanFxWorkspaceResource.class);

    // Install the basis for model mapping before loading nodes and infos.
    GraphModel graphModel = graphDocRsrc.getResource().getGraph();
    srcContext.putContextValue(GraphModel.class, graphModel);

    DepanFxKeyPropertyStore infoStore =
        unmarshallInfoStore(srcContext, graphModel);

    return new DepanFxAnnotationStoreData(
        toolName, toolDescr, graphDocRsrc, annoIndexRsrc, infoStore);
  }

  private DepanFxKeyPropertyStore unmarshallInfoStore(
      XstreamUnmarshalContext srcContext, GraphModel graphModel) {
    srcContext.putContextValue(GraphModel.class, graphModel);
    DepanFxKeyPropertyStore result = null;

    // finally seems heavyweight.
    srcContext.moveDown();
    if (srcContext.getNodeName().equals(INFO_STORE_TAG)) {
      result = (DepanFxKeyPropertyStore) srcContext.convertAnother(
          null, DepanFxSimpleKeyPropertyStore.class);
    }
    srcContext.moveUp();
    if (result == null) {
      LOG.info("Unable to unmarshal an info store");
    }
    return result;
  }
}
