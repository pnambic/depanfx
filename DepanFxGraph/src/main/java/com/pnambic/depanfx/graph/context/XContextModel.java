package com.pnambic.depanfx.graph.context;

import java.util.Collection;

public interface XContextModel<N extends ContextNodeKindId, R extends ContextRelationId> {

  ContextModelId getId();

  Collection<XContextModel<?, ?>> getIncludedModels();

  Collection<? extends N> getNodeKindIds();

  Collection<? extends R> getRelationIds();
}
