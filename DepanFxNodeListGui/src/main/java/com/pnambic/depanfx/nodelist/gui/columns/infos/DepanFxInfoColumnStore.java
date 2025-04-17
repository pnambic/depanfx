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
 */
public interface DepanFxInfoColumnStore extends PropertyStore {

  Optional<?> getInfoProperty(GraphNode graphNode);

  /**
   * Same type as returned from {@link #getInfoProperty(GraphNode)}.
   */
  void setPropertyValue(GraphNode graphNode, Object value);

  void addInfoListener(GraphNode graphNode, Listener listener);

  void removeInfoListener(GraphNode graphNode, Listener listener);
}
