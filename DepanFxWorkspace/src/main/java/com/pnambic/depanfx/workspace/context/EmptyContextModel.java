package com.pnambic.depanfx.workspace.context;

import java.util.Collection;
import java.util.Collections;

import com.pnambic.depanfx.graph.context.ContextModel;
import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.context.ContextNodeKindId;
import com.pnambic.depanfx.graph.context.ContextRelationId;

public class EmptyContextModel implements ContextModel<ContextNodeKindId, ContextRelationId> {

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
  public Collection<ContextModel<?, ?>> getIncludedModels() {
    return Collections.emptyList();
  }

  @Override
  public Collection<ContextNodeKindId> getNodeKindIds() {
    return Collections.emptyList();
  }

  @Override
  public Collection<ContextRelationId> getRelationIds() {
    return Collections.emptyList();
  }
}
