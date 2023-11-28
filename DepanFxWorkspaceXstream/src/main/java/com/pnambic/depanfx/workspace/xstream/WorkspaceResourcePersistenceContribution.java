package com.pnambic.depanfx.workspace.xstream;

import com.pnambic.depanfx.persistence.builder.DocumentXmlPersistBuilder;
import com.pnambic.depanfx.persistence.plugins.GraphNodePersistencePluginContribution;
import com.pnambic.depanfx.workspace.DepanFxProjectResource;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Cannot have any inject dependencies from a workspace, or there will be
 * dependency loops at application startup.
 */
@Component
public class WorkspaceResourcePersistenceContribution
    implements GraphNodePersistencePluginContribution {

  public static final String WORKSPACE_RESOURCE_TAG = "workspace-resource";

  public static final String PROJECT_NAME_TAG = "project-name";

  public static final String RESOURCE_PATH_TAG = "resource-path";

  private static final Class<?>[] ALLOWED_TYPES = new Class[] {
      XstreamWorkspaceResource.class
  };

  @Autowired
  public WorkspaceResourcePersistenceContribution() {
  }

  @Override
  public void extendPersist(
      DocumentXmlPersistBuilder builder, Class<?> withType) {
    if (DepanFxWorkspaceResource.class.isAssignableFrom(withType)) {
      builder.addAlias(WORKSPACE_RESOURCE_TAG, XstreamWorkspaceResource.class);
      builder.addAliasField(
          PROJECT_NAME_TAG, XstreamWorkspaceResource.class, "projectName");
      builder.addAliasField(
          RESOURCE_PATH_TAG, XstreamWorkspaceResource.class, "resourcePath");
      builder.addAllowedType(ALLOWED_TYPES);
    }
    if (DepanFxProjectResource.class.isAssignableFrom(withType)) {
      builder.addConverter(new DepanFxBuiltInProjectResourceConverter());
    }
  }
}
