package com.pnambic.depanfx.graph.context.plugins;

import java.util.Collection;
import java.util.Collections;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.context.ContextNodeKindId;
import com.pnambic.depanfx.graph.model.GraphContextModel;
import com.pnambic.depanfx.graph.model.GraphRelation;

public class EmptyContextModel implements GraphContextModel {

  private static final String EMPTY_CONTEXT_MODEL = "Empty context model";

  public static final ContextModelId ID = new ContextModelId() {

    @Override
    public String getContextModelKey() {
      return EMPTY_CONTEXT_MODEL;
    }
  };

  @Override
  public ContextModelId getId() {
    return ID;
  }

  @Override
  public Collection<GraphContextModel> getIncludedModels() {
    return Collections.emptyList();
  }

  @Override
  public Collection<ContextNodeKindId> getNodeKindIds() {
    return Collections.emptyList();
  }

  @Override
  public Collection<GraphRelation> getRelations() {
    return Collections.emptyList();
  }
}
