package com.pnambic.depanfx.filesystem.context;

import java.util.Collection;
import java.util.Collections;

import com.google.common.collect.ImmutableList;
import com.pnambic.depanfx.graph.context.ContextModel;
import com.pnambic.depanfx.graph.context.ContextModelId;

public class FileSystemContextModel
    implements ContextModel<FileSystemNodeKindId, FileSystemRelationId> {

  private final ContextModelId contextId;

  private final Collection<FileSystemNodeKindId> nodeKinds;

  private final Collection<FileSystemRelationId> relations;

  public FileSystemContextModel(
      ContextModelId contextId,
      Collection<FileSystemNodeKindId> nodeKinds,
      Collection<FileSystemRelationId> relations) {
    this.contextId = contextId;
    this.nodeKinds = nodeKinds;
    this.relations = relations;
  }

  @Override
  public ContextModelId getId() {
    return contextId;
  }

  @Override
  public Collection<ContextModel<?, ?>> getIncludedModels() {
    return Collections.emptyList();
  }

  @Override
  public Collection<FileSystemRelationId> getRelationIds() {
    return ImmutableList.copyOf(relations);
  }

  @Override
  public Collection<? extends FileSystemNodeKindId> getNodeKindIds() {
    return ImmutableList.copyOf(nodeKinds);
  }
}
