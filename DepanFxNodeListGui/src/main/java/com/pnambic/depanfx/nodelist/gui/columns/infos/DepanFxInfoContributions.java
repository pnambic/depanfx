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
import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxNodeInfoProperty;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

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
          DepanFxNodeInfoProperty.buildStringProperty(
              "Model", "Node id graph model property"),
          DepanFxNodeInfoProperty.buildStringProperty(
              "Kind", "Node id node kind property"),
          DepanFxNodeInfoProperty.buildStringProperty(
              "Key", "Node id node key property"),
          DepanFxNodeInfoProperty.buildStringProperty(
              "Complete", "Complete node id")
  };

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
  }
}
