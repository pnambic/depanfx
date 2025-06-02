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
package com.pnambic.depanfx.graph.nodeinfo;

import com.pnambic.depanfx.graph.info.GraphNodeInfo.Listener;
import com.pnambic.depanfx.graph.model.GraphNode;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Define the interactions with a property store.
 *
 * Instances from derived types typically have an underlying info source
 * and an info key that further define the info retrieval results.
 */
public interface DepanFxNodeInfoStore extends DepanFxInfoRegistry.PropertyStore {

  Stream<GraphNode> streamNodes();

  Optional<?> getInfoValue(GraphNode graphNode);

  /**
   * Same type as returned from {@link #getInfoValue(GraphNode)}.
   */
  void setInfoValue(GraphNode graphNode, Object value);

  void addInfoListener(GraphNode graphNode, Listener listener);

  void removeInfoListener(GraphNode graphNode, Listener listener);

  public static Optional<?> getValue(
      DepanFxInfoRegistry.PropertyStore store,
      GraphNode node) {
    if (store instanceof DepanFxNodeInfoStore infos) {
      return infos.getInfoValue(node);
    }
    return Optional.empty();
  }

  public static void setInfoValue(
      DepanFxInfoRegistry.PropertyStore store,
      GraphNode node,
      Object value) {
    if (store instanceof DepanFxNodeInfoStore infos) {
      infos.setInfoValue(node, value);
    }
  }

  public static void addListener(
      DepanFxInfoRegistry.PropertyStore store,
      GraphNode node,
      Listener listener) {
    if (store instanceof DepanFxNodeInfoStore infos) {
      infos.addInfoListener(node, listener);
    }
  }

  public static void removeListener(
      DepanFxInfoRegistry.PropertyStore store,
      GraphNode node,
      Listener listener) {
    if (store instanceof DepanFxNodeInfoStore infos) {
      infos.removeInfoListener(node, listener);
    }
  }

  /**
   * Starting point for read-only stores.
   * An {@code getInfoValue(GraphNode)} implementation is still required.
   */
  public static abstract class AbstractReadOnly implements DepanFxNodeInfoStore {

    @Override
    public void setInfoValue(GraphNode graphNode, Object value) {
      // Do nothing, nothing changes.
    }

    @Override
    public void addInfoListener(GraphNode graphNode, Listener listener) {
      // Do nothing, nothing changes.
    }

    @Override
    public void removeInfoListener(GraphNode graphNode, Listener listener) {
      // Do nothing, nothing changes.
    }
  }
}
