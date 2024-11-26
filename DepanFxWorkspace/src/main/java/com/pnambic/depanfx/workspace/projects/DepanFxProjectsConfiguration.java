package com.pnambic.depanfx.workspace.projects;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DepanFxProjectsConfiguration {

  @Bean
  public DepanFxBuiltInProject builtInProject(DepanFxBuiltInRegistry builtIns) {
    return new DepanFxBuiltInProject(builtIns);
  }

  @Bean
  public DepanFxScratchProject scratchProject() {
    return new DepanFxScratchProject();
  }
}
