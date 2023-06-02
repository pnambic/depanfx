package com.pnambic.depanfx.graph.context;

import java.util.Collection;

public interface ContextModel<N extends ContextNodeKindId, R extends ContextRelationId> {

  ContextModelId getId();

  Collection<ContextModel<?, ?>> getIncludedModels();

  Collection<? extends N> getNodeKindIds();

  Collection<? extends R> getRelationIds();
}
