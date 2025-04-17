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

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

@Component
public class DepanFxNodeKeyInfoContribution
    extends DepanFxInfoRegistry.Basic {

  public static final String NODE_ID_LABEL = "Node Id";

  public static final String NODE_ID_DESCR = "Component of node id.";

  public static final String NODE_ID_KEY = "Node Id";

  public static final DepanFxNodeInfoProperty GRAPH_MODEL_PROPERTY =
      buildNodeKeyInfoProperty(
          "Model", "Node id graph model property.",
          n -> n.getId()
              .getContextNodeKindId()
              .getContextModelId()
              .getContextModelKey());

  public static final DepanFxNodeInfoProperty NODE_KIND_PROPERTY =
      buildNodeKeyInfoProperty(
          "Kind", "Node id node kind property.",
          n -> n.getId()
              .getContextNodeKindId()
              .getNodeKindKey());

  public static final DepanFxNodeInfoProperty NODE_KEY_PROPERTY =
      buildNodeKeyInfoProperty(
          "Key", "Node id node key property.",
          n -> n.getId().getNodeKey());

  public static final DepanFxNodeInfoProperty SIMPLE_NAME_PROPERTY =
      buildNodeKeyInfoProperty(
          "Simple Name", "Simple name.",
          n -> n.getId().getSimpleName());

  public static final DepanFxNodeInfoProperty FULL_KEY_PROPERTY =
      buildNodeKeyInfoProperty(
          "Complete", "Complete node id.",
          DepanFxNodeKeyInfoContribution::toFullNodeId);

  private static final DepanFxNodeInfoProperty[] PROPERTIES =
      new DepanFxNodeInfoProperty[] {
          GRAPH_MODEL_PROPERTY, NODE_KIND_PROPERTY, NODE_KEY_PROPERTY,
          SIMPLE_NAME_PROPERTY, FULL_KEY_PROPERTY
  };

  private static final DepanFxInfoColumnStore NODE_KEY_INFO__STORE =
      new NodeKeyInfoStore();

  public DepanFxNodeKeyInfoContribution() {
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
  public DepanFxInfoColumnStore getInfoStore(Object storeContainer) {
    return NODE_KEY_INFO__STORE;
  }

  private static String toFullNodeId(GraphNode graphnode) {
    return GraphContextKeys.toNodeKey(graphnode.getId());
  }

  private static NodeKeyInfoProperty buildNodeKeyInfoProperty(
      String toolName, String toolDescription,
      Function<GraphNode, String> nodeTransform) {
    return new NodeKeyInfoProperty(
        toolName, toolDescription,
        DepanFxNodeInfoProperty.PropertyKind.STRING,
        nodeTransform);
  }

  private static class NodeKeyInfoProperty extends DepanFxNodeInfoProperty {

    private final Function<GraphNode, String> nodeTransform;

    public NodeKeyInfoProperty(
        String toolName, String toolDescription, PropertyKind propertyKind,
        Function<GraphNode, String> getter) {
      super(toolName, toolDescription, propertyKind, false);
      this.nodeTransform = getter;
    }

    public String forNode(GraphNode graphNode) {
      return nodeTransform.apply(graphNode);
    }
  }

  /**
   * Necessary to pass type check in {@code DepanFxInfoColumn constructor}.
   *
   * Not used internally, since it just adds additional indirections.
   */
  private static class NodeKeyInfoStore implements DepanFxInfoColumnStore {

    @Override
    public Optional<?> getInfoValue(GraphNode graphNode) {
      return Optional.of(graphNode);
    }

    @Override
    public void setInfoValue(GraphNode graphNode, Object value) {
      // Do nothing: Node key cannot changed.
    }

    @Override
    public void addInfoListener(GraphNode graphNode, Listener listener) {
      // Do nothing: Node key cannot changed.
    }

    @Override
    public void removeInfoListener(GraphNode graphNode, Listener listener) {
      // Do nothing: Node key cannot changed.
    }
  }
}
