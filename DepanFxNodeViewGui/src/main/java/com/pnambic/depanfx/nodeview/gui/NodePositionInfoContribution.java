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

import com.pnambic.depanfx.graph.info.GraphNodeInfo.Listener;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry.PropertyStore;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxNodeInfoProperty;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.gui.columns.infos.DepanFxInfoColumnStore;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A node position info requires a NodeViewPanel as a source
 * of location information.
 */
@Component
class NodePositionInfoContribution
    extends DepanFxInfoRegistry.Basic {

  public static final String NODE_POSITION_LABEL = "Position";

  public static final String NODE_POSITION_KEY = "Position";

  public static final String NODE_POSITION_DESCR =
      "Position of nodes in their rendered graph view.";

  public static final DepanFxNodeInfoProperty X_POS_PROPERTY =
      buildPosProperty(
          "X Pos", "X position of the node",
          p -> p.xPos, (l, d) -> updateX(l, d));

  public static final DepanFxNodeInfoProperty Y_POS_PROPERTY =
      buildPosProperty(
          "Y Pos", "Y position of the node",
          p -> p.yPos, (l, d) -> updateY(l, d));

  public static final DepanFxNodeInfoProperty Z_POS_PROPERTY =
      buildPosProperty(
          "Z Pos", "Z position of the node",
          p -> p.zPos, (l, d) -> updateZ(l, d));

  private static final DepanFxNodeInfoProperty[] PROPERTIES =
      new DepanFxNodeInfoProperty[] {
          X_POS_PROPERTY, Y_POS_PROPERTY, Z_POS_PROPERTY
  };

  public NodePositionInfoContribution() {
    super(
        DepanFxNodeLocationData.class.getName(),
        NODE_POSITION_LABEL,
        NODE_POSITION_DESCR,
        DepanFxNodeLocationData.class,
        NODE_POSITION_KEY,
        Arrays.asList(PROPERTIES));
  }

  @Override
  public DepanFxInfoRegistry.PropertyStore getInfoStore(Object storeContainer) {
    if (storeContainer instanceof DepanFxNodeListTableAdapter tableAdapter) {
      Optional<DepanFxInfoColumnStore> result =
          tableAdapter.getInfoStore(DepanFxNodeLocationData.class);
      // Since a basic PropertyStore is not a DepanFxInfoColumnStore,
      // a return with an .orElse runs into type problems.
      if (result.isPresent() ) {
        return result.get();
      }
    }
    return DepanFxInfoRegistry.NULL_STORE;
  }

  @Override
  public Optional<?> getPropertyValue(
      PropertyStore store, GraphNode graphNode,
      DepanFxNodeInfoProperty infoProperty) {
    if (store instanceof DepanFxInfoColumnStore infos) {
      NodePosInfoProperty posProp = (NodePosInfoProperty) infoProperty;
      return infos.getInfoValue(graphNode)
          .map(DepanFxNodeLocationData.class::cast)
          .map(posProp::extractValue);
    }

    return Optional.empty();
  }

  @Override
  public void setPropertyValue(
      PropertyStore store, GraphNode graphNode,
      DepanFxNodeInfoProperty infoProperty, String input) {
    if (store instanceof DepanFxInfoColumnStore infos) {
      NodePosInfoProperty posProp = (NodePosInfoProperty) infoProperty;
      double updatePos = Double.parseDouble(input);
      infos.getInfoValue(graphNode)
          .map(DepanFxNodeLocationData.class::cast)
          .map(p -> posProp.updateLocation(p, updatePos))
          .ifPresent(p -> infos.setInfoValue(graphNode, p));
    }
  }

  @Override
  public void addInfoListener(PropertyStore store, GraphNode node,
      DepanFxNodeInfoProperty infoProperty, Listener listener) {
    if (store instanceof DepanFxInfoColumnStore infos) {
      infos.addInfoListener(node, listener);
    }
  }

  @Override
  public void removeInfoListener(PropertyStore store, GraphNode node,
      DepanFxNodeInfoProperty infoProperty, Listener listener) {
    if (store instanceof DepanFxInfoColumnStore infos) {
      infos.removeInfoListener(node, listener);
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
}
