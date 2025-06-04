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

import com.pnambic.depanfx.graph.info.GraphNodeInfo;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry;
import com.pnambic.depanfx.graph_doc.model.GraphModel;
import com.pnambic.depanfx.graph_doc.model.GraphModels;
import com.pnambic.depanfx.nodelist.annos.DepanFxKeyAnnotationInfoStore;
import com.pnambic.depanfx.nodelist.annos.DepanFxSimpleKeyPropertyStore;
import com.pnambic.depanfx.persistence.BasePersistObjectConverter;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.modxstream.XstreamMarshalContext;
import com.pnambic.modxstream.XstreamUnmarshalContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Handle serialization for {@link DepanFxKeyPropertyStoreX}.
 */
public class DepanFxSimpleKeyPropertyStoreConverter
    extends BasePersistObjectConverter<DepanFxSimpleKeyPropertyStore> {

  public static final String KEY_PROPERTY_TAG = "key-property";

  public static final String KEY_INFO_TAG = "key-info";

  public static final String ANNO_DATA_TAG = "annotation-data";

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
      DepanFxSimpleKeyPropertyStore.class,
      DepanFxSimpleKeyPropertyStoreConverter.KeyInfo.class,
      DepanFxNodeAnnotationData.class
    };

  public static void installIn(PersistDocumentTransportBuilder builder) {
    builder.addConverter(new DepanFxSimpleKeyPropertyStoreConverter());
    builder.addAliasType(KEY_PROPERTY_TAG, DepanFxSimpleKeyPropertyStore.class);
    builder.addAliasType(ANNO_DATA_TAG, DepanFxNodeAnnotationData.class);
    builder.addAliasType(
        KEY_INFO_TAG, DepanFxSimpleKeyPropertyStoreConverter.KeyInfo.class);
  }

  @Override
  public Class<?> forType() {
    return DepanFxSimpleKeyPropertyStore.class;
  }

  @Override
  public Class<?>[] getAllowTypes() {
    return ALLOW_TYPES;
  }

  @Override
  public String getTag() {
    return KEY_PROPERTY_TAG;
  }

  @Override
  public void marshal(XstreamMarshalContext dstContext, Object source) {
    DepanFxSimpleKeyPropertyStore store =
        (DepanFxSimpleKeyPropertyStore) source;
    dstContext.convertAnother(byNodeKeyInfo(store));
  }

  @Override
  public DepanFxSimpleKeyPropertyStore unmarshal(
      XstreamUnmarshalContext srcContext) {
    @SuppressWarnings("unchecked")
    Map<GraphNode, KeyInfo> byNode =
        (Map<GraphNode, KeyInfo>) srcContext.convertAnother(null, Map.class);
    GraphModel graphModel =
        (GraphModel) srcContext.getContextValue(GraphModel.class);
    return forUnmarshal(byNode, graphModel);
  }

  private Map<GraphNode, KeyInfo> byNodeKeyInfo(
      DepanFxSimpleKeyPropertyStore store) {
    Map<GraphNode, KeyInfo> byKey = new HashMap<>();
    store.streamPropertyStoreKeys()
        .forEach(k -> populateByKey(byKey, store, k));
    return byKey;
  }

  private void populateByKey(
      Map<GraphNode, KeyInfo> byKey,
      DepanFxSimpleKeyPropertyStore store,
      String infoKey) {
    DepanFxKeyAnnotationInfoStore saveStore =
        (DepanFxKeyAnnotationInfoStore) store.getPropertyStore(infoKey);

    saveStore.streamNodes()
        .forEach(n -> populateNodeKeyValue(byKey, n, saveStore));
  }

  private void populateNodeKeyValue(
      Map<GraphNode, KeyInfo> byKey,
      GraphNode node,
      DepanFxKeyAnnotationInfoStore saveStore) {

    saveStore.getInfoValue(node)
      .map(GraphNodeInfo.class::cast)
      .ifPresent(v -> byKey.put(node, new KeyInfo(saveStore.getInfoKey(), v)));
  }

  private DepanFxSimpleKeyPropertyStore forUnmarshal(
      Map<GraphNode, KeyInfo> byNode, GraphModel graphModel) {
    Map<String, DepanFxInfoRegistry.PropertyStore> infoStores =
        new HashMap<>();

    byNode.entrySet().forEach(e ->
        populateInfoStore(infoStores, graphModel, e.getKey(), e.getValue()));
    return DepanFxSimpleKeyPropertyStore.fromStores(infoStores);
  }

  private void populateInfoStore(
      Map<String, DepanFxInfoRegistry.PropertyStore> infoStores,
      GraphModel graphModel, GraphNode node, KeyInfo value) {
    GraphNode infoNode = GraphModels.mapGraphNode(node, graphModel);
    DepanFxKeyAnnotationInfoStore nodeValueMap =
        (DepanFxKeyAnnotationInfoStore) infoStores.computeIfAbsent(
            value.infoKey, k -> new DepanFxKeyAnnotationInfoStore(k));
    nodeValueMap.setInfoValue(infoNode, value.infoValue);
  }

  private class KeyInfo {

    private String infoKey;

    private GraphNodeInfo infoValue;

    public KeyInfo(String infoKey, GraphNodeInfo infoValue) {
      this.infoKey = infoKey;
      this.infoValue = infoValue;
    }
  }
}
