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

import com.pnambic.depanfx.graph.context.ContextNodeId;
import com.pnambic.depanfx.graph.info.GraphNodeInfo.Listener;
import com.pnambic.depanfx.graph.model.GraphNode;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
public class DepanFxNodeAnnotationInfoContribution
    extends DepanFxInfoRegistry.Basic {

  public static final String ANNOTATION_PROP_LABEL = "Annotation";

  public static final String ANNOTATION_PROP_DESCR =
      "Text value to annotate the node.";

  public static final String ANNOTATION_INFO_LABEL = "Annotation";

  public static final String ANNOTATION_INFO_DESCR =
      "A text property that annotates the node.";

  public static final String ANNOTATION_INFO_KEY = "Annotation";

  public static final DepanFxNodeInfoProperty NODE_ANNOTATION_PROPERTY =
      new DepanFxNodeInfoProperty(
          ANNOTATION_PROP_LABEL, ANNOTATION_PROP_DESCR,
          DepanFxNodeInfoProperty.PropertyKind.STRING, true);

  private static final DepanFxNodeInfoProperty[] PROPERTIES =
      new DepanFxNodeInfoProperty[] {
          NODE_ANNOTATION_PROPERTY
  };

  public DepanFxNodeAnnotationInfoContribution() {
    super(
        ContextNodeId.class.getName(),
        ANNOTATION_INFO_LABEL,
        ANNOTATION_INFO_DESCR,
        String.class,
        ANNOTATION_INFO_KEY,
        Arrays.asList(PROPERTIES));
  }

  @Override
  public Optional<?> getPropertyValue(
      DepanFxInfoRegistry.PropertyStore store,
      GraphNode graphNode, DepanFxNodeInfoProperty infoProperty) {
    return DepanFxNodeInfoStore.getValue(store, graphNode);
  }

  @Override
  public void setPropertyValue(
      DepanFxInfoRegistry.PropertyStore store, GraphNode graphNode,
      DepanFxNodeInfoProperty infoProperty, String input) {
    DepanFxNodeInfoStore.setInfoValue(store, graphNode, input);
  }

  @Override
  public void addInfoListener(
      DepanFxInfoRegistry.PropertyStore store, GraphNode node,
      DepanFxNodeInfoProperty infoProperty, Listener listener) {
    DepanFxNodeInfoStore.addListener(store, node, listener);
  }

  @Override
  public void removeInfoListener(
      DepanFxInfoRegistry.PropertyStore store, GraphNode node,
      DepanFxNodeInfoProperty infoProperty, Listener listener) {
    DepanFxNodeInfoStore.removeListener(store, node, listener);
  }

  @Override
  public DepanFxInfoRegistry.PropertyStore getInfoStore(Object storeContainer) {
    return (DepanFxInfoRegistry.PropertyStore) storeContainer;
  }
}
