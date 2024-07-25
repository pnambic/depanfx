package com.pnambic.depanfx.graph_doc.persistence;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.context.ContextNodeKindId;
import com.pnambic.depanfx.graph.model.GraphContextModel;
import com.pnambic.depanfx.graph_doc.model.GraphContextDocument;
import com.pnambic.depanfx.persistence.BasePersistObjectConverter;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.persistence.PersistMarshalContext;
import com.pnambic.depanfx.persistence.PersistUnmarshalContext;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.nio.file.Path;

public class ContextNodeKindIdConverter
    extends BasePersistObjectConverter<ContextNodeKindId> {

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
      ContextNodeKindId.class
  };

  public static final String NODE_KIND_KEY_TAG = "node-kind-key";

  public ContextNodeKindIdConverter() {
  }

  public static void installIn(PersistDocumentTransportBuilder builder) {
    builder.addConverter(new ContextNodeKindIdConverter());
    builder.addAliasType(NODE_KIND_KEY_TAG, ContextNodeKindId.class);
    builder.addAllowedType(ALLOW_TYPES);
  }

  @Override
  public Class<?> forType() {
    return ContextNodeKindId.class;
  }

  @Override
  public Class<?>[] getAllowTypes() {
    return ALLOW_TYPES;
  }

  @Override
  public String getTag() {
    return NODE_KIND_KEY_TAG;
  }

  @Override
  public void marshal(PersistMarshalContext dstContext, Object source) {
    ContextNodeKindId nodeKind = (ContextNodeKindId) source;
    marshalObject(dstContext, nodeKind.getContextModelId());
    marshalObject(dstContext, NODE_KIND_KEY_TAG, nodeKind.getNodeKindKey());
  }

  @Override
  public ContextNodeKindId unmarshal(PersistUnmarshalContext srcContext) {
    GraphContextModel contextModel = (GraphContextModel)
        unmarshalValue(srcContext, ContextModelId.class);

    String modelKey = srcContext.getValue();
    String nodeKindKey = modelKey;

    DepanFxWorkspace workspace =
        (DepanFxWorkspace) srcContext.getContextValue(DepanFxWorkspace.class);
    Path modelPath = GraphContextDocument.CONTEXT_MODEL_PATH
        .resolve(modelKey);

    GraphContextModel model = DepanFxProjects.getBuiltIn(
        workspace, GraphContextDocument.class, modelPath)
        .map(DepanFxWorkspaceResource::getResource)
        .map(d -> d.getGraphContext())
        .get();
    return model.getNodeKindIds().stream()
        .filter(k -> k.getNodeKindKey().equals(nodeKindKey))
        .findFirst()
        .get();
  }
}
