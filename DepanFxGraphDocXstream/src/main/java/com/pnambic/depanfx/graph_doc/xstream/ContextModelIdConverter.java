package com.pnambic.depanfx.graph_doc.xstream;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph_doc.model.GraphContextDocument;
import com.pnambic.depanfx.persistence.AbstractObjectXmlConverter;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import java.nio.file.Path;

public class ContextModelIdConverter
    extends AbstractObjectXmlConverter<ContextModelId> {

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
  public void marshal(Object source,
      HierarchicalStreamWriter writer, MarshallingContext context, Mapper mapper) {

    marshalValue(((ContextModelId) source).getContextModelKey(), context);
  }

  @Override
  public ContextModelId unmarshal(HierarchicalStreamReader reader,
      UnmarshallingContext context, Mapper mapper) {

    String modelKey = reader.getValue();

    DepanFxWorkspace workspace =
        (DepanFxWorkspace) context.get(DepanFxWorkspace.class);
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
