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
package com.pnambic.depanfx.nodelist.annos;

import com.pnambic.depanfx.graph.info.GraphNodeInfo;
import com.pnambic.depanfx.graph.info.GraphNodeInfo.Listener;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxNodeInfoStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Connect an info key to a selected annotation in an annotation store.
 */
public class DepanFxKeyAnnotationInfoStore implements DepanFxNodeInfoStore {

  private final String infoKey;

  private final Map<GraphNode, GraphNodeInfo> infoStore = new HashMap<>();

  private Map<GraphNode, Collection<Listener>> nodeListeners =
      new HashMap<>();

  public DepanFxKeyAnnotationInfoStore(String infoKey) {
    this.infoKey = infoKey;
  }

  @Override
  public Optional<?> getInfoValue(GraphNode graphNode) {
    return Optional.ofNullable(infoStore.get(graphNode));
  }

  @Override
  public void setInfoValue(GraphNode graphNode, Object value) {
    if (value instanceof GraphNodeInfo nodeInfo) {
      infoStore.put(graphNode, nodeInfo);
      fireUpdateEvent(graphNode, nodeInfo);
      return;
    }
  }

  @Override
  public void addInfoListener(GraphNode graphNode, Listener listener) {
    nodeListeners
        .computeIfAbsent(graphNode, n -> new ArrayList<Listener>())
        .add(listener);
  }

  @Override
  public void removeInfoListener(GraphNode graphNode, Listener listener) {
    nodeListeners
        .getOrDefault(graphNode, Collections.emptyList())
        .remove(listener);
  }

  public String getInfoKey() {
    return infoKey;
  }

  private void fireUpdateEvent(GraphNode graphNode, GraphNodeInfo nodeInfo) {
    nodeListeners
        .getOrDefault(graphNode, Collections.emptyList())
        .forEach(l -> l.updateInfo(graphNode, nodeInfo));
  }
}
