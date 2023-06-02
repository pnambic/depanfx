package com.pnambic.depanfx.graph.filesystem.graph;

import java.util.Collection;
import java.util.Collections;

import com.pnambic.depanfx.graph.api.Relation;
import com.pnambic.depanfx.graph.context.GraphModel;
import com.pnambic.depanfx.graph.context.GraphModelId;
import com.pnambic.depanfx.graph.context.NodeKind;

public class FileSystemGraphModel<N, R> implements GraphModel<N, R> {

  // Just need one.
  private static final FileSystemGraphModelId graphModelId = new FileSystemGraphModelId();

  @Override
  public GraphModelId getId() {
    return graphModelId;
  }

  @Override
  public Collection<GraphModel<?, ?>> getIncludedModels() {
    return Collections.emptyList();
  }

  @Override
  public Collection<Relation<? extends R>> getRelations() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getRelations'");
  }

  @Override
  public Collection<NodeKind> getNodeKinds() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getNodeKinds'");
  }
  
}
