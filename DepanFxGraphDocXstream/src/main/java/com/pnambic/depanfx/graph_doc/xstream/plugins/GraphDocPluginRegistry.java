package com.pnambic.depanfx.graph_doc.xstream.plugins;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pnambic.depanfx.persistence.builder.DocumentXmlPersistBuilder;

@Component
public class GraphDocPluginRegistry {
  private final List<GraphDocPluginContribution> graphDocPlugins;

  @Autowired
  public GraphDocPluginRegistry(List<GraphDocPluginContribution> graphDocPlugins) {
    this.graphDocPlugins = graphDocPlugins;
  }

  public void applyExtensions(DocumentXmlPersistBuilder builder) {
    graphDocPlugins.forEach(p -> p.extendPersist(builder));
  }
}
