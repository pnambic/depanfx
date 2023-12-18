package com.pnambic.depanfx.workspace.xstream;

import com.pnambic.depanfx.persistence.AbstractObjectXmlConverter;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class WorkspaceResourceConverter
    extends AbstractObjectXmlConverter<DepanFxWorkspaceResource> {

  public static final String WORKSPACE_RESOURCE_TAG = "workspace-resource";

  private static final Class<?>[] ALLOWED_TYPES = new Class[] {
  };

  @Override
  public Class<?> forType() {
    return DepanFxWorkspaceResource.class;
  }

  @Override
  public Class<?>[] getAllowTypes() {
    return ALLOWED_TYPES;
  }

  @Override
  public String getTag() {
    return WORKSPACE_RESOURCE_TAG;
  }

  @Override
  public void marshal(Object source, HierarchicalStreamWriter writer,
      MarshallingContext context, Mapper mapper) {
    DepanFxWorkspaceResource wkspRsrc = (DepanFxWorkspaceResource) source;

    XstreamWorkspaceResource xstreamWkspRsrc =
        XstreamWorkspaceResource.of(wkspRsrc);
    marshalValue(xstreamWkspRsrc, context);
  }

  @Override
  public DepanFxWorkspaceResource unmarshal(
      HierarchicalStreamReader reader,
      UnmarshallingContext context,
      Mapper mapper) {
    XstreamWorkspaceResource xstreamWkspRsrc =
        (XstreamWorkspaceResource) unmarshalValue(
            context, XstreamWorkspaceResource.class);

    DepanFxWorkspace workspace =
        (DepanFxWorkspace) context.get(DepanFxWorkspace.class);
    DepanFxWorkspaceResource result =
        XstreamWorkspaceResource.forWksp(workspace, xstreamWkspRsrc)
        .get();
    return result;
  }
}
