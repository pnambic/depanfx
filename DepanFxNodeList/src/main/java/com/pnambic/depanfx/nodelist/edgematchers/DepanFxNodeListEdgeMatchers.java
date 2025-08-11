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
package com.pnambic.depanfx.nodelist.edgematchers;

import com.pnambic.depanfx.edgematchers.link.DepanFxLinkMatchers;
import com.pnambic.depanfx.edgematchers.link.DepanFxLinks;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLink;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcher;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListEdgeMatcherData;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class DepanFxNodeListEdgeMatchers {

  private DepanFxNodeListEdgeMatchers() {
    // Prevent instantiation.
  }

  public static DepanFxLinkMatcher createMatcher(
      DepanFxNodeListEdgeMatcherData nodesInfo) {

    Collection<GraphNode> headNodes = getResourceNodes(
        nodesInfo.getHeadNodesResource());
    Collection<GraphNode> tailNodes = getResourceNodes(
        nodesInfo.getTailNodesResource());

    if (headNodes.isEmpty() && tailNodes.isEmpty()) {
      return DepanFxLinkMatchers.EMPTY_MATCHER;
    }
    if (tailNodes.isEmpty()) {
      return new SourceListMatcher(headNodes);
    }
    if (headNodes.isEmpty()) {
      return new TargetListMatcher(tailNodes);
    }

    return new SourceListTargetListMatcher(headNodes, tailNodes);
  }

  private static Collection<GraphNode> getResourceNodes(
      DepanFxWorkspaceResource<DepanFxNodeList> nodesRsrc) {

    if (nodesRsrc == null) {
      return Collections.emptyList();
    }

    Collection<GraphNode> nodes = nodesRsrc.getResource().getNodes();
    if (nodes.isEmpty()) {
      return Collections.emptyList();
    }

    return nodes;
  }

  public static abstract class NodeListMatcher implements DepanFxLinkMatcher {

    protected final Collection<GraphNode> matcherNodes;

    public NodeListMatcher(Collection<GraphNode> matcherNodes) {
      this.matcherNodes = matcherNodes;
    }

    protected boolean inNodeList(GraphNode node) {
      return matcherNodes.contains(node);
    }
  }

  public static class SourceListMatcher extends NodeListMatcher {

    public SourceListMatcher(Collection<GraphNode> matcherNodes) {
      super(matcherNodes);
    }

    @Override
    public Optional<DepanFxLink> match(GraphEdge edge) {
      if (inNodeList(edge.getHead())) {
        return Optional.of(new DepanFxLinks.Forward(edge));
      }

      return Optional.empty();
    }
  }

  public static class TargetListMatcher extends NodeListMatcher {

    public TargetListMatcher(Collection<GraphNode> matcherNodes) {
      super(matcherNodes);
    }

    @Override
    public Optional<DepanFxLink> match(GraphEdge edge) {
      if (inNodeList(edge.getTail())) {
        return Optional.of(new DepanFxLinks.Forward(edge));
      }

      return Optional.empty();
    }
  }

  public static class SourceListTargetListMatcher
      implements DepanFxLinkMatcher {

    private final Collection<GraphNode> sourceNodes;

    private final Collection<GraphNode> targetNodes;

    private SourceListTargetListMatcher(
        Collection<GraphNode> sourceNodes, Collection<GraphNode> targetNodes) {
      this.sourceNodes = sourceNodes;
      this.targetNodes = targetNodes;
    }

    @Override
    public Optional<DepanFxLink> match(GraphEdge edge) {
      if (inSourceList(edge.getHead()) && inTargetList(edge.getTail())) {
        return Optional.of(new DepanFxLinks.Forward(edge));
      }

      return Optional.empty();
    }

    private boolean inSourceList(GraphNode node) {
      return sourceNodes.contains(node);
    }

    protected boolean inTargetList(GraphNode node) {
      return targetNodes.contains(node);
    }
  }
}
