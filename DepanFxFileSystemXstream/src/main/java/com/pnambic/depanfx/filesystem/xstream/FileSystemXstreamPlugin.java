package com.pnambic.depanfx.filesystem.xstream;

import com.pnambic.depanfx.persistence.builder.DocumentXmlPersistBuilder;

import org.springframework.stereotype.Component;

import com.pnambic.depanfx.graph_doc.xstream.plugins.GraphDocPluginContribution;

@Component
public class FileSystemXstreamPlugin implements GraphDocPluginContribution {

  @Override
  public void extendPersist(DocumentXmlPersistBuilder builder) {
    builder.addConverter(new DirectoryNodeConverter());
    builder.addConverter(new DocumentNodeConverter());
  }
}
