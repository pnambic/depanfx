package com.pnambic.depanfx.persistence.plugins;

import com.pnambic.depanfx.persistence.builder.DocumentXmlPersistBuilder;

public interface GraphNodePersistencePluginContribution {

  void extendPersist(DocumentXmlPersistBuilder builder, Class<?> withType);
}
