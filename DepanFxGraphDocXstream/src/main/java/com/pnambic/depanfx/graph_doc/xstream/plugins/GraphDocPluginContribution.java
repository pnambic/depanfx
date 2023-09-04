package com.pnambic.depanfx.graph_doc.xstream.plugins;

import com.pnambic.depanfx.persistence.builder.DocumentXmlPersistBuilder;

public interface GraphDocPluginContribution {

  void extendPersist(DocumentXmlPersistBuilder builder);
}
