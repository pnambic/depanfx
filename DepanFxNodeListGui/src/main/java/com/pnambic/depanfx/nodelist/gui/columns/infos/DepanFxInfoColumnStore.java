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
package com.pnambic.depanfx.nodelist.gui.columns.infos;

import com.pnambic.depanfx.graph.info.GraphNodeInfo.Listener;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry.PropertyStore;

import java.util.Optional;

/**
 * Define the interactions with a property store.
 *
 * Instances from derived types typically have an underlying info source
 * and an info key that further define the info retrieval results.
 */
public interface DepanFxInfoColumnStore extends PropertyStore {

  Optional<?> getInfoValue(GraphNode graphNode);

  /**
   * Same type as returned from {@link #getInfoValue(GraphNode)}.
   */
  void setInfoValue(GraphNode graphNode, Object value);

  void addInfoListener(GraphNode graphNode, Listener listener);

  void removeInfoListener(GraphNode graphNode, Listener listener);
}
