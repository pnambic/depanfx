package com.pnambic.depanfx.graph_doc.persistence;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph_doc.model.GraphContextDocument;
import com.pnambic.depanfx.persistence.BasePersistObjectConverter;
import com.pnambic.depanfx.persistence.PersistMarshalContext;
import com.pnambic.depanfx.persistence.PersistUnmarshalContext;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.nio.file.Path;

public class ContextModelIdConverter
    extends BasePersistObjectConverter<ContextModelId> {

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
    ContextModelId.class
  };

  public static final String CONTEXT_KEY_TAG = "context-key";

  public ContextModelIdConverter() {
  }

  @Override
  public Class<?> forType() {
    return ContextModelId.class;
  }

  @Override
  public Class<?>[] getAllowTypes() {
    return ALLOW_TYPES;
  }

  @Override
  public String getTag() {
    return CONTEXT_KEY_TAG;
  }


  @Override
  public void marshal(PersistMarshalContext dstContext, Object source) {
    marshalValue(dstContext, ((ContextModelId) source).getContextModelKey());
  }

  @Override
  public ContextModelId unmarshal(PersistUnmarshalContext srcContext) {

    String modelKey = srcContext.getValue();

    DepanFxWorkspace workspace =
        (DepanFxWorkspace) srcContext.getContextValue(DepanFxWorkspace.class);
    Path modelPath = GraphContextDocument.CONTEXT_MODEL_PATH
        .resolve(modelKey);

     return DepanFxProjects.getBuiltIn(
        workspace, GraphContextDocument.class, modelPath)
        .map(DepanFxWorkspaceResource::getResource)
        .map(GraphContextDocument.class::cast)
        .map(d -> d.getGraphContext().getId())
        .get();
  }
}
