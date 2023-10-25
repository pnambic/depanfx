package com.pnambic.depanfx.workspace.xstream;

import com.pnambic.depanfx.persistence.AbstractObjectXmlConverter;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import java.nio.file.Path;

public class WorkspaceResourceConverter
    extends AbstractObjectXmlConverter<DepanFxWorkspaceResource> {

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
      DepanFxWorkspaceResource.class
  };

  public static final String WORKSPACE_RESOURCE_TAG = "workspace-resource";

  private static final String PROJECT_NAME_TAG = "project-name";

  private static final String RESOURCE_PATH_TAG = "resource-path";

  private final DepanFxWorkspace workspace;

  public WorkspaceResourceConverter(DepanFxWorkspace workspace) {
    this.workspace = workspace;
  }

  @Override
  public Class<?> forType() {
    return DepanFxWorkspaceResource.class;
  }

  @Override
  public Class<?>[] getAllowTypes() {
    return ALLOW_TYPES;
  }

  @Override
  public String getTag() {
    return WORKSPACE_RESOURCE_TAG;
  }

  @Override
  public void marshal(Object source, HierarchicalStreamWriter writer,
      MarshallingContext context, Mapper mapper) {
    DepanFxWorkspaceResource wkspRsrc = (DepanFxWorkspaceResource) source;
    DepanFxProjectDocument projectDoc = wkspRsrc.getDocument();
    Path rsrcPath = projectDoc.getMemberPath();

    DepanFxProjectMember rsrcProj = projectDoc.getProject();
    Path projPath = rsrcProj.getMemberPath();
    Path pathValue = projPath.relativize(rsrcPath);
    String projectName = rsrcProj.getMemberName();
    String resourcePath = pathValue.toString();
    marshalProperty(PROJECT_NAME_TAG, projectName, writer, context, mapper);
    marshalProperty(RESOURCE_PATH_TAG, resourcePath, writer, context, mapper);
  }

  @Override
  public DepanFxWorkspaceResource unmarshal(HierarchicalStreamReader reader,
      UnmarshallingContext context, Mapper mapper) {
    String projectName = (String) unmarshalOne(reader, context, mapper);
    String resourcePath = (String) unmarshalOne(reader, context, mapper);
    return workspace.toProjectDocument(projectName, resourcePath)
        .flatMap(workspace::getWorkspaceResource)
        .get();
  }
}
