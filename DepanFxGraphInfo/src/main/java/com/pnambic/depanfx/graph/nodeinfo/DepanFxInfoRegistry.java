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

import com.pnambic.depanfx.base.DepanFxOrderableContribution;
import com.pnambic.depanfx.graph.info.GraphNodeInfo;
import com.pnambic.depanfx.graph.model.GraphNode;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface DepanFxInfoRegistry {

  Stream<Contribution> streamByLabel(String label);

  Optional<Contribution> getById(String id);

  Stream<Contribution> streamContributions();

  /**
   * Marker interface for shared stores of property values.
   */
  public interface PropertyStore {
  }

  public final PropertyStore NULL_STORE = new PropertyStore() {};

  /**
   * A contribution represents one bundle of GraphNodeInfo.  Specific elements
   * from a node info can manipulated by specifying the desired
   * node info property.
   */
  public interface Contribution extends DepanFxOrderableContribution {

    /**
     * Used for serialization.  Should be universally unique.
     * A class name is good.
     *
     * Info id's starting "depan" are reserved by DepanFX.
     */
    String getInfoId();

    /**
     * Recognize an info content by type.
     */
    boolean acceptsInfo(Object info);

    /**
     * Label to show to humans.
     */
    String getInfoLabel();

    /**
     * Description to show to humans.
     */
    String getInfoDescription();

    /**
     * Supply the properties supported by this info.
     */
    Stream<DepanFxNodeInfoProperty> streamProperties();

    Optional<DepanFxNodeInfoProperty> getProperty(String label);

    PropertyStore getInfoStore(Object storeContainer);

    Optional<?> getPropertyValue(
        PropertyStore store, GraphNode graphNode,
        DepanFxNodeInfoProperty infoProperty);

    void setPropertyValue(
        PropertyStore store, GraphNode graphNode,
        DepanFxNodeInfoProperty infoProperty,
        String input);

    void addInfoListener(
        PropertyStore store, GraphNode node,
        DepanFxNodeInfoProperty infoProperty,
        GraphNodeInfo.Listener listener);

    void removeInfoListener(
        PropertyStore store, GraphNode node,
        DepanFxNodeInfoProperty infoProperty,
        GraphNodeInfo.Listener listener);
  }

  /**
   * Discourage, but works for {@code StringConverter} compliance.
   */
  public static Contribution fromLabel(
      DepanFxInfoRegistry infoRegistry, String label) {
    return infoRegistry.streamByLabel(label)
        .findFirst()
        .orElse(null);
  }

  public static abstract class Basic implements Contribution {

    private final String id;

    private final String label;

    private final String descr;

    private final Class<?> acceptType;

    private final String orderKey;

    private final List<DepanFxNodeInfoProperty> properties;

    public Basic(
        String id,
        String label,
        String descr,
        Class<?> acceptType,
        String orderKey,
        List<DepanFxNodeInfoProperty> properties) {
      this.id = id;
      this.label = label;
      this.descr = descr;
      this.acceptType = acceptType;
      this.orderKey = orderKey;
      this.properties = properties;
    }

    @Override
    public String getInfoId() {
      return id;
    }

    @Override
    public boolean acceptsInfo(Object info) {
      return acceptType.isAssignableFrom(info.getClass());
    }

    @Override
    public String getInfoLabel() {
      return label;
    }

    @Override
    public String getInfoDescription() {
      return descr;
    }

    @Override
    public String getOrderKey() {
      return orderKey;
    }

    @Override
    public Stream<DepanFxNodeInfoProperty> streamProperties() {
      return properties.stream();
    }

    @Override
    public Optional<DepanFxNodeInfoProperty> getProperty(String label) {
      return properties.stream()
          .filter(p -> label.equals(p.getToolName()))
          .findFirst();
    }
  }
}
