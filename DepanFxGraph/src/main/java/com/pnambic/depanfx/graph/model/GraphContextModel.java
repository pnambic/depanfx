package com.pnambic.depanfx.graph.model;

import java.util.Collection;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.context.ContextNodeKindId;

public interface GraphContextModel {

  ContextModelId getId();

  Collection<GraphContextModel> getIncludedModels();

  Collection<ContextNodeKindId> getNodeKindIds();

  Collection<GraphRelation> getRelations();
}
