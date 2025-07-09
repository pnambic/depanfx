/*
 * Copyright 2023 The Depan Project Authors
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
package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSection;

import java.util.Objects;
import java.util.stream.Stream;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/**
 * Encapsulates the basic details of a node that is rendered within a section.
 */
public abstract class DepanFxNodeListGraphNode
    implements DepanFxNodeListMember {

  private final GraphNode node;

  private final DepanFxNodeListSection section;

  public DepanFxNodeListGraphNode(
      GraphNode node, DepanFxNodeListSection section) {
    this.node = node;
    this.section = section;
  }

  @Override
  public String getDisplayName() {
    return section.getDisplayName(node);
  }

  public String getSortKey() {
    return section.getSortKey(node);
  }

  public GraphNode getGraphNode() {
    return node;
  }

  public DepanFxNodeListSection getSection() {
    return section;
  }

  /**
   * Convert a list of choice items to a stream of nodes.
   */
  public static Stream<GraphNode> streamMultiNodes(
      ObservableList<TreeItem<DepanFxNodeListMember>> choices) {
    return choices.stream()
            .filter(Objects::nonNull)
            .map(TreeItem::getValue)
            .filter(DepanFxNodeListGraphNode.class::isInstance)
            .map(DepanFxNodeListGraphNode.class::cast)
            .map(DepanFxNodeListGraphNode::getGraphNode);
  }
}
