package com.pnambic.depanfx.graph.model;

import java.util.Collection;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.context.ContextNodeKindId;

public class BasicGraphContextModel implements GraphContextModel {

  private final ContextModelId modelId;

  private final Collection<GraphContextModel> getIncludedModels;

  private final Collection<ContextNodeKindId> nodeKindIds;

  private final Collection<GraphRelation> relations;

  public BasicGraphContextModel(
      ContextModelId modelId,
      Collection<GraphContextModel> getIncludedModels,
      Collection<ContextNodeKindId> nodeKindIds,
      Collection<GraphRelation> relations) {
    this.modelId = modelId;
    this.getIncludedModels = getIncludedModels;
    this.nodeKindIds = nodeKindIds;
    this.relations = relations;
  }

  @Override
  public ContextModelId getId() {
    return modelId;
  }

  @Override
  public Collection<GraphContextModel> getIncludedModels() {
    return getIncludedModels;
  }

  @Override
  public Collection<ContextNodeKindId> getNodeKindIds() {
    return nodeKindIds;
  }

  @Override
  public Collection<GraphRelation> getRelations() {
    return relations;
  }
}
