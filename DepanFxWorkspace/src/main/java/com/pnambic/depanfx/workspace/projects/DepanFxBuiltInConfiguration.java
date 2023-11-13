package com.pnambic.depanfx.workspace.projects;

import com.pnambic.depanfx.workspace.DepanFxProjectSpi;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DepanFxBuiltInConfiguration {

  @Bean
  @Qualifier("BuiltIn Workspace")
  public DepanFxProjectSpi builtInProject(DepanFxBuiltInRegistry builtIns) {
    return new DepanFxBuiltInProject(builtIns);
  }
}
