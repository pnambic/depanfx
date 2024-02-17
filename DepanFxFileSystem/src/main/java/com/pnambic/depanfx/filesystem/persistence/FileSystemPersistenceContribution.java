package com.pnambic.depanfx.filesystem.persistence;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.persistence.plugins.GraphNodePersistencePluginContribution;

import org.springframework.stereotype.Component;

@Component
public class FileSystemPersistenceContribution
    implements GraphNodePersistencePluginContribution {

  @Override
  public void extendPersist(
      PersistDocumentTransportBuilder builder, Class<?> withType) {
    if (withType.isAssignableFrom(GraphNode.class)) {
      builder.addConverter(new DirectoryNodeConverter());
      builder.addConverter(new DocumentNodeConverter());
    }
  }
}
