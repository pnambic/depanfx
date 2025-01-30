package com.pnambic.depanfx.workspace.persistence;

import com.pnambic.depanfx.persistence.BasePersistObjectConverter;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.modxstream.XstreamMarshalContext;
import com.pnambic.modxstream.XstreamUnmarshalContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class WorkspaceResourceConverter
    extends BasePersistObjectConverter<DepanFxWorkspaceResource<?>> {

  public static final String WORKSPACE_RESOURCE_TAG = "workspace-resource";

  private static final Class<?>[] ALLOWED_TYPES = new Class[] {
  };

  private static final Logger LOG =
      LoggerFactory.getLogger(WorkspaceResourceConverter.class);

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
  public void marshal(XstreamMarshalContext dstContext, Object source) {
    DepanFxWorkspaceResource<?> wkspRsrc = (DepanFxWorkspaceResource<?>) source;

    // Don't save scratch resources.
    DepanFxWorkspace workspace =
        (DepanFxWorkspace) dstContext.getContextValue(DepanFxWorkspace.class);
    if (workspace.getScratchProjectTree() == wkspRsrc.getDocument().getProject()) {
      return;
    }

    PersistWorkspaceResource persistWkspRsrc =
        PersistWorkspaceResource.of(wkspRsrc);
    marshalValue(dstContext, persistWkspRsrc);
  }

  @Override
  public DepanFxWorkspaceResource<?> unmarshal(
      XstreamUnmarshalContext srcContext) {

    // Scratch resources were stored as a null elements.
    if (!srcContext.hasMoreChildren()) {
      return null;
    }

    DepanFxWorkspace workspace =
        (DepanFxWorkspace) srcContext.getContextValue(DepanFxWorkspace.class);
    PersistWorkspaceResource persistWkspRsrc =
        (PersistWorkspaceResource) unmarshalValue(
            srcContext, PersistWorkspaceResource.class);
    Optional<DepanFxWorkspaceResource<Object>> optResult =
        PersistWorkspaceResource.forWksp(workspace, persistWkspRsrc);

    if (optResult.isEmpty()) {
      LOG.error("Unable to find workspace resource {}:{}",
      persistWkspRsrc.projectName, persistWkspRsrc.resourcePath);
    }
    return optResult.get();
  }
}
