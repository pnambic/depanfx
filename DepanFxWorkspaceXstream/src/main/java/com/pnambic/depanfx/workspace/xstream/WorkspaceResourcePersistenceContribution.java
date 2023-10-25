package com.pnambic.depanfx.workspace.xstream;

import com.pnambic.depanfx.persistence.builder.DocumentXmlPersistBuilder;
import com.pnambic.depanfx.persistence.plugins.GraphNodePersistencePluginContribution;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

/**
 * Added to the persistence registry by hand, to avoid circular dependencies
 * during load time.
 */
public class WorkspaceResourcePersistenceContribution
    implements GraphNodePersistencePluginContribution {

  private final DepanFxWorkspace workspace;

  public WorkspaceResourcePersistenceContribution(
      DepanFxWorkspace workspace) {
    this.workspace = workspace;
  }

  @Override
  public void extendPersist(
      DocumentXmlPersistBuilder builder, Class<?> withType) {
    if (DepanFxWorkspaceResource.class.isAssignableFrom(withType)) {
      builder.addConverter(new WorkspaceResourceConverter(workspace));
    }
  }
}
