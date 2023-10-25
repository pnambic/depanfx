package com.pnambic.depanfx.workspace.xstream;

import com.pnambic.depanfx.persistence.plugins.GraphNodePersistencePluginRegistry;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkspaceResourcePersistenceInstaller {

  @Bean
  public WorkspaceResourcePersistenceInstaller workspaceResourceConverter(
      GraphNodePersistencePluginRegistry pluginRegistry,
      DepanFxWorkspace workspace) {
    WorkspaceResourcePersistenceContribution result =
        new WorkspaceResourcePersistenceContribution(workspace);
    pluginRegistry.installContribution(result);
    return this;
  }
}
