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
package com.pnambic.depanfx.nodeview.tooldata;

import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Merge multiple node views into one.
 */
public class DepanFxNodeViewMerger {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeViewMerger.class);

  private final DepanFxWorkspaceResource<GraphDocument> graphRsrc;

  private Set<GraphNode> knownNodes = new HashSet<>();

  private Map<GraphNode, DepanFxNodeLocationData> nodeLocations =
      new HashMap<>();

  private Map<GraphNode, DepanFxNodeDisplayData> nodeDisplay =
      new HashMap<>();

  private Map<GraphEdge, DepanFxLineDisplayData> edgeDisplay =
      new HashMap<>();

  public DepanFxNodeViewMerger(
      DepanFxWorkspaceResource<GraphDocument> graphRsrc) {
    this.graphRsrc = graphRsrc;
  }

  public void merge(DepanFxNodeViewData sourceData) {
    if (!validGraphDoc(sourceData)) {
      return;
    }
    knownNodes.addAll(sourceData.getViewNodes());
    sourceData.getNodeLocations().entrySet().stream()
        .filter(this::isNodeMergable)
        .forEach(this::mergeNodeLocation);
    sourceData.getNodeDisplay().entrySet().stream()
        .filter(this::isNodeMergable)
        .forEach(this::mergeNodeDisplay);
    sourceData.getEdgeDisplay().entrySet().stream()
        .filter(this::isEdgeMergable)
        .forEach(this::mergeEdgeDisplay);
  }

  private boolean validGraphDoc(DepanFxNodeViewData sourceData) {
    if (sourceData.getGraphDocRsrc().getResource() == graphRsrc.getResource()) {
      return true;
    }
    LOG.warn(
        "Dropping node view {} from merge since it is"
            + " part of graph {}"
            + " not graph {}",
            sourceData.getToolName(),
            sourceData.getGraphDocRsrc().getDocument().getMemberName(),
            graphRsrc.getDocument().getMemberName());
    return false;
  }

  public DepanFxNodeViewData buildResult(
      String viewName, String viewDescr,
      DepanFxNodeViewData baseView) {
    return new DepanFxNodeViewData(
        viewName, viewDescr, baseView.getGraphDocRsrc(),

        new ArrayList<>(knownNodes), nodeLocations, nodeDisplay, edgeDisplay,

        baseView.getSceneData(),

        baseView.getAvailableNodeResource(),
        baseView.getVisibleNodeResource(),
        baseView.getNodeFoldResources(),
        baseView.getNodeDisplayDocRsrc(),
        baseView.getRemainderNodesVisible(),
        baseView.getRemainderNodesDisplay(),

        baseView.getAvailableEdgeResource(),
        baseView.getVisibleEdgeResource(),
        baseView.getEdgeFiltersResource(),
        baseView.getLinkDisplayResource(),
        baseView.getRemainderEdgesVisible(),
        baseView.getRemainderEdgesLabel(),
        baseView.getRemainderEdgeDisplay()
        );
  }

  private boolean isNodeMergable(Map.Entry<GraphNode, ?> entry) {
    return isMergableNode(entry.getKey());
  }

  private boolean isEdgeMergable(Map.Entry<GraphEdge, ?> entry) {
    GraphEdge edge = entry.getKey();
    if (!isMergableNode(edge.getHead())) {
      return false;
    }
    return isMergableNode(edge.getTail());
  }

  private boolean isMergableNode(GraphNode node) {
    return knownNodes.contains(node);
  }

  private void mergeNodeLocation(
      Map.Entry<GraphNode, DepanFxNodeLocationData> entry) {
    nodeLocations.computeIfAbsent(entry.getKey(), k -> entry.getValue());
  }

  private void mergeNodeDisplay(
      Map.Entry<GraphNode, DepanFxNodeDisplayData> entry) {
    nodeDisplay.computeIfAbsent(entry.getKey(), k -> entry.getValue());
  }

  private void mergeEdgeDisplay(
      Map.Entry<GraphEdge, DepanFxLineDisplayData> entry) {
    edgeDisplay.computeIfAbsent(entry.getKey(), k -> entry.getValue());
  }
}
