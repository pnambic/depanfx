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
import com.pnambic.depanfx.nodelist.annos.DepanFxKeyAnnotationInfoStore;
import com.pnambic.depanfx.nodelist.annos.DepanFxKeyPropertyStore;
import com.pnambic.depanfx.persistence.BasePersistObjectConverter;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.modxstream.XstreamMarshalContext;
import com.pnambic.modxstream.XstreamUnmarshalContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Handle serialization for {@link DepanFxKeyPropertyStore}.
 */
public class DepanFxKeyPropertyStoreConverter
    extends BasePersistObjectConverter<DepanFxKeyPropertyStore> {

  public static final String KEY_PROPERTY_TAG = "key-property";

  public static final String KEY_INFO_TAG = "key-info";

  public static final String ANNO_DATA_TAG = "annotation-data";

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
      DepanFxKeyPropertyStore.class,
      DepanFxKeyPropertyStoreConverter.KeyInfo.class,
      DepanFxNodeAnnotationData.class
    };

  public static void installIn(PersistDocumentTransportBuilder builder) {
    builder.addConverter(new DepanFxKeyPropertyStoreConverter());
    builder.addAliasType(KEY_PROPERTY_TAG, DepanFxKeyPropertyStore.class);
    builder.addAliasType(ANNO_DATA_TAG, DepanFxNodeAnnotationData.class);
    builder.addAliasType(
        KEY_INFO_TAG, DepanFxKeyPropertyStoreConverter.KeyInfo.class);
  }

  @Override
  public Class<?> forType() {
    return DepanFxKeyPropertyStore.class;
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
    DepanFxKeyPropertyStore store = (DepanFxKeyPropertyStore) source;
    dstContext.convertAnother(byNodeKeyInfo(store));
  }

  @Override
  public DepanFxKeyPropertyStore unmarshal(
      XstreamUnmarshalContext srcContext) {
    @SuppressWarnings("unchecked")
    Map<GraphNode, KeyInfo> byNode =
        (Map<GraphNode, KeyInfo>) srcContext.convertAnother(null, Map.class);
    return forUnmarshal(byNode);
  }

  private Map<GraphNode, KeyInfo> byNodeKeyInfo(DepanFxKeyPropertyStore store) {
    Map<GraphNode, KeyInfo> byKey = new HashMap<>();
    store.streamPropertyStoreKeys()
        .forEach(k -> populateByKey(byKey, store, k));
    return byKey;
  }

  private void populateByKey(
      Map<GraphNode, KeyInfo> byKey,
      DepanFxKeyPropertyStore store,
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

  private DepanFxKeyPropertyStore forUnmarshal(
      Map<GraphNode, KeyInfo> byNode) {
    Map<String, DepanFxInfoRegistry.PropertyStore> infoStores =
        new HashMap<>();

    byNode.entrySet().forEach(
        e -> populateInfoStore(infoStores, e.getKey(), e.getValue()));
    return null;
  }

  private void populateInfoStore(
      Map<String, DepanFxInfoRegistry.PropertyStore> infoStores,
      GraphNode node, KeyInfo value) {
    DepanFxKeyAnnotationInfoStore nodeValueMap =
        (DepanFxKeyAnnotationInfoStore) infoStores.computeIfAbsent(
            value.infoKey, k -> new DepanFxKeyAnnotationInfoStore(k));
    nodeValueMap.setInfoValue(node, value.infoValue);
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
