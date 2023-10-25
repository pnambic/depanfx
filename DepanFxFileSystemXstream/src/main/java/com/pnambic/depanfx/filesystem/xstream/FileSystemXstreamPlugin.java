package com.pnambic.depanfx.filesystem.xstream;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.persistence.builder.DocumentXmlPersistBuilder;
import com.pnambic.depanfx.persistence.plugins.GraphNodePersistencePluginContribution;

import org.springframework.stereotype.Component;

@Component
public class FileSystemXstreamPlugin
    implements GraphNodePersistencePluginContribution {

  @Override
  public void extendPersist(
      DocumentXmlPersistBuilder builder, Class<?> withType) {
    if (withType.isAssignableFrom(GraphNode.class)) {
      builder.addConverter(new DirectoryNodeConverter());
      builder.addConverter(new DocumentNodeConverter());
    }
  }
}
