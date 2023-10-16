package com.pnambic.depanfx.nodelist.model;

import java.util.Collection;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;

public class DepanFxNodeList {

  private final DepanFxProjectDocument docRef;

  private final Collection<GraphNode> nodes;

  public DepanFxNodeList(
      DepanFxProjectDocument docRef,
      Collection<GraphNode> nodes) {
    this.docRef = docRef;
    this.nodes = nodes;
  }

  public DepanFxProjectDocument getDocRef() {
    return docRef;
  }

  public Collection<GraphNode> getNodes() {
    return nodes;
  }

}
