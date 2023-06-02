package com.pnambic.depanfx.graph.context;

import java.util.Collection;

import com.pnambic.depanfx.graph.api.Node;
import com.pnambic.depanfx.graph.api.Relation;

public interface ContextModel<N, R> {

  ContextModelId getId();

  Collection<ContextModel<?, ?>> getIncludedModels();

  Collection<Relation<? extends R>> getRelations();

  Collection<Class<? extends Node<? extends N>>> getNodeKinds();
}
