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
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A node position info requires a NodeViewPanel as a source
 * of location information.
 */
class NodePositionInfoContribution
    extends DepanFxInfoRegistry.Basic {

  public static final String NODE_POSITION_LABEL = "Position";

  public static final String NODE_POSITION_KEY = "Position";

  public static final String NODE_POSITION_DESCR =
      "Position of nodes in their rendered graph view.";

  private static final DepanFxNodeInfoProperty[] PROPERTIES =
      new DepanFxNodeInfoProperty[] {
          buildPosProperty("X Pos", "X position of the node",
              p -> p.xPos, (l, d) -> updateX(l, d)),
          buildPosProperty("Y Pos", "Y position of the node",
              p -> p.yPos, (l, d) -> updateY(l, d)),
          buildPosProperty("Z Pos", "Z position of the node",
              p -> p.zPos, (l, d) -> updateZ(l, d)),
  };

  private final DepanFxNodeViewPanel panel;

  public NodePositionInfoContribution(DepanFxNodeViewPanel panel) {
    super(
        DepanFxNodeLocationData.class.getName(),
        NODE_POSITION_LABEL,
        NODE_POSITION_DESCR,
        DepanFxNodeLocationData.class,
        NODE_POSITION_KEY,
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
      Function<DepanFxNodeLocationData, Double> extractValue,
      BiFunction<DepanFxNodeLocationData, Double, DepanFxNodeLocationData> updateLocation) {
    return new NodePosInfoProperty(
        toolName, toolDescription,
        DepanFxNodeInfoProperty.PropertyKind.POS, true,
        extractValue, updateLocation);
  }

  private static class NodePosInfoProperty extends DepanFxNodeInfoProperty {

    private Function<DepanFxNodeLocationData, Double> extractValue;

    private BiFunction<DepanFxNodeLocationData, Double, DepanFxNodeLocationData> updateLocation;

    public NodePosInfoProperty(
        String toolName, String toolDescription,
        PropertyKind propertyKind, boolean isEditable,
        Function<DepanFxNodeLocationData, Double> extractValue,
        BiFunction<DepanFxNodeLocationData, Double, DepanFxNodeLocationData> updateLocation) {
      super(toolName, toolDescription, propertyKind, isEditable);
      this.extractValue = extractValue;
      this.updateLocation = updateLocation;
    }

    public Double extractValue(DepanFxNodeLocationData nodeLocationData) {
      return extractValue.apply(nodeLocationData);
    }

    public DepanFxNodeLocationData updateLocation(
        DepanFxNodeLocationData nodePos, Double value) {
      return updateLocation.apply(nodePos, value);
    }
  }

  @Override
  public void setPropertyValue(GraphNode graphNode,
      DepanFxNodeInfoProperty infoProperty, String input) {
    DepanFxNodeLocationData nodePos = panel.getNodeLocation(graphNode);
    double updatePos = Double.parseDouble(input);
    if (infoProperty instanceof NodePosInfoProperty posProperty) {
      DepanFxNodeLocationData updateLoc =
          posProperty.updateLocation(nodePos, updatePos);
      panel.updateNodeLocation(graphNode, updateLoc);
    }
  }

  private static DepanFxNodeLocationData updateX(
      DepanFxNodeLocationData init, Double newX) {
    return new DepanFxNodeLocationData(newX, init.yPos, init.zPos);
  }

  private static DepanFxNodeLocationData updateY(
      DepanFxNodeLocationData init, Double newY) {
    return new DepanFxNodeLocationData(init.xPos, newY, init.zPos);
  }

  private static DepanFxNodeLocationData updateZ(
      DepanFxNodeLocationData init, Double newZ) {
    return new DepanFxNodeLocationData(init.xPos, init.yPos, newZ);
  }
}
