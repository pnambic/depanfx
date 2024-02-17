package com.pnambic.depanfx.persistence.plugins;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;

@Component
public class GraphNodePersistencePluginRegistry {

  private final List<GraphNodePersistencePluginContribution> graphNodePlugins;

  @Autowired
  public GraphNodePersistencePluginRegistry(
      List<GraphNodePersistencePluginContribution> graphNodePlugins) {
    this.graphNodePlugins = graphNodePlugins;
  }

  public void installContribution(
      GraphNodePersistencePluginContribution contrib) {
    graphNodePlugins.add(contrib);
  }

  public void applyExtensions(
      PersistDocumentTransportBuilder builder, Class<?> withType) {

    graphNodePlugins.forEach(p -> p.extendPersist(builder, withType));
  }
}
