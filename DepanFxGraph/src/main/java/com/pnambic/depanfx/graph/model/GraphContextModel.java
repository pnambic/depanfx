package com.pnambic.depanfx.graph.model;

import java.util.Collection;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.context.ContextNodeKindId;

/**
 * Defines the components of a graph context model.
 */
public interface GraphContextModel {

  /** Id to use when referencing the context models. */
  ContextModelId getId();

  /**
   * Other context models that provides node kinds and relations
   * to this context model.
   */
  Collection<GraphContextModel> getIncludedModels();

  /** The node kinds introduces by this context model. */
  Collection<ContextNodeKindId> getNodeKindIds();

  /** The relations introduces by this context model. */
  Collection<GraphRelation> getRelations();
}
