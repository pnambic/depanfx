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
package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxNodeInfoProperty;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

class NodePositionInfoContribution
    extends DepanFxInfoRegistry.Basic {

  private static final DepanFxNodeInfoProperty[] PROPERTIES =
      new DepanFxNodeInfoProperty[] {
          buildPosProperty("X Pos", "X position of the node", p -> p.xPos),
          buildPosProperty("Y Pos", "Y position of the node", p -> p.yPos),
          buildPosProperty("Z Pos", "Z position of the node", p -> p.zPos)
  };

  private final DepanFxNodeViewPanel panel;

  public NodePositionInfoContribution(DepanFxNodeViewPanel panel) {
    super(
        DepanFxNodeLocationData.class.getName(),
        DepanFxNodeViewPanelConfiguration.NODE_POSITION_LABEL,
        DepanFxNodeViewPanelConfiguration.NODE_POSITION_DESCR,
        DepanFxNodeLocationData.class,
        DepanFxNodeViewPanelConfiguration.NODE_POSITION_KEY,
        Arrays.asList(PROPERTIES));
    this.panel = panel;
  }

  @Override
  public Optional<?> getPropertyValue(
      GraphNode graphNode, DepanFxNodeInfoProperty infoProperty) {
    DepanFxNodeLocationData nodePos = panel.getNodeLocation(graphNode);
    NodePosInfoProperty posProp = (NodePosInfoProperty) infoProperty;
    return Optional.of(posProp.extractValue(nodePos));
  }

  private static DepanFxNodeInfoProperty buildPosProperty(
      String toolName, String toolDescription,
      Function<DepanFxNodeLocationData, Double> extractValue) {
    return new DepanFxNodeInfoProperty(
        toolName, toolDescription,
        DepanFxNodeInfoProperty.PropertyKind.POS, true);
  }

  private static class NodePosInfoProperty extends DepanFxNodeInfoProperty {

    private Function<DepanFxNodeLocationData, Double> extractValue;

    public NodePosInfoProperty(
        String toolName, String toolDescription,
        PropertyKind propertyKind, boolean isEditable,
        Function<DepanFxNodeLocationData, Double> extractValue) {
      super(toolName, toolDescription, propertyKind, isEditable);
      this.extractValue = extractValue;
    }

    public Double extractValue(DepanFxNodeLocationData nodeLocationData) {
      return extractValue.apply(nodeLocationData);
    }
  }
  // @Bean
  // public DepanFxInfoRegistry.Contribution nodePositionsInfoContribution() {
  //  return new NodePositionInfoContribution();
  // }
}
