package com.pnambic.depanfx.workspace.persistence;

import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
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

  public static final String PROJECT_NAME_TAG = "project-name";

  public static final String RESOURCE_PATH_TAG = "resource-path";

  @Autowired
  public WorkspaceResourcePersistenceContribution() {
  }

  @Override
  public void extendPersist(
      PersistDocumentTransportBuilder builder, Class<?> withType) {
    if (DepanFxWorkspaceResource.class.isAssignableFrom(withType)) {
      builder.addAliasField(
          PROJECT_NAME_TAG, PersistWorkspaceResource.class, "projectName");
      builder.addAliasField(
          RESOURCE_PATH_TAG, PersistWorkspaceResource.class, "resourcePath");

      WorkspaceResourceConverter converter = new WorkspaceResourceConverter();
      builder.addAliasType(converter.getTag(), converter.forType());
      builder.addConverter(converter);
    }
    if (DepanFxProjectResource.class.isAssignableFrom(withType)) {
      builder.addConverter(new DepanFxBuiltInProjectResourceConverter());
    }
  }
}
