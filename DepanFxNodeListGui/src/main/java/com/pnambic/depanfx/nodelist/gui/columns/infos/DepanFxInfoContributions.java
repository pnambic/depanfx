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

import com.pnambic.depanfx.graph.context.ContextNodeId;
import com.pnambic.depanfx.graph.context.GraphContextKeys;
import com.pnambic.depanfx.graph.info.GraphNodeInfo.Listener;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxNodeInfoProperty;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

@Configuration
public class DepanFxInfoContributions {

  public static final String NODE_ID_LABEL = "Node Id";

  public static final String NODE_ID_DESCR = "Component of node id.";

  public static final String NODE_ID_KEY = "Node Id";

  @Bean
  public DepanFxInfoRegistry.Contribution nodeKeyInfoContribution() {
    return new NodeKeyInfoContribution();
  }

  private static final DepanFxNodeInfoProperty[] PROPERTIES =
      new DepanFxNodeInfoProperty[] {
          buildStringProperty(
              "Model", "Node id graph model property",
              n -> n.getId()
                  .getContextNodeKindId()
                  .getContextModelId()
                  .getContextModelKey()),
          buildStringProperty(
              "Kind", "Node id node kind property",
              n -> n.getId()
                  .getContextNodeKindId()
                  .getNodeKindKey()),
          buildStringProperty(
              "Key", "Node id node key property",
              n -> n.getId().getNodeKey()),
          buildStringProperty(
              "Complete", "Complete node id",
              DepanFxInfoContributions::toFullNodeId)
  };

  public static DepanFxNodeInfoProperty buildStringProperty(
      String toolName, String toolDescription,
      Function<GraphNode, String> nodeTransform) {
    return new NodeKeyInfoProperty(
        toolName, toolDescription,
        DepanFxNodeInfoProperty.PropertyKind.STRING, false,
        nodeTransform);
  }

  private static class NodeKeyInfoContribution
      extends DepanFxInfoRegistry.Basic {

    public NodeKeyInfoContribution() {
      super(
          ContextNodeId.class.getName(),
          NODE_ID_LABEL,
          NODE_ID_DESCR,
          ContextNodeId.class,
          NODE_ID_KEY,
          Arrays.asList(PROPERTIES));
    }

    @Override
    public Optional<?> getPropertyValue(
        DepanFxInfoRegistry.PropertyStore store,
        GraphNode graphNode, DepanFxNodeInfoProperty infoProperty) {
      NodeKeyInfoProperty nodeKeyProp = (NodeKeyInfoProperty) infoProperty;
      return Optional.of(nodeKeyProp.forNode(graphNode));
    }

    @Override
    public void setPropertyValue(
        DepanFxInfoRegistry.PropertyStore store, GraphNode graphNode,
        DepanFxNodeInfoProperty infoProperty, String input) {
      // Do nothing: Node key cannot changed.
    }

    @Override
    public void addInfoListener(
        DepanFxInfoRegistry.PropertyStore store, GraphNode node,
        DepanFxNodeInfoProperty infoProperty, Listener listener) {
      // Do nothing: Node key cannot changed.
    }

    @Override
    public void removeInfoListener(
        DepanFxInfoRegistry.PropertyStore store, GraphNode node,
        DepanFxNodeInfoProperty infoProperty, Listener listener) {
      // Do nothing: Node key cannot changed.
    }

    @Override
    public DepanFxInfoRegistry.PropertyStore getInfoStore(
        Object storeContainer) {
      // No store used, so a dummy entity.
      return DepanFxInfoRegistry.NULL_STORE;
    }
  }

  private static class NodeKeyInfoProperty extends DepanFxNodeInfoProperty {

    private final Function<GraphNode, String> nodeTransform;

    public NodeKeyInfoProperty(
        String toolName, String toolDescription,
        PropertyKind propertyKind, boolean isEditable,
        Function<GraphNode, String> getter) {
      super(toolName, toolDescription, propertyKind, isEditable);
      this.nodeTransform = getter;
    }

    public String forNode(GraphNode graphNode) {
      return nodeTransform.apply(graphNode);
    }
  }

  private static String toFullNodeId(GraphNode graphnode) {
    return GraphContextKeys.toNodeKey(graphnode.getId());
  }
}
