package com.pnambic.depanfx.persistence.plugins;

import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;

public interface GraphNodePersistencePluginContribution {

  void extendPersist(PersistDocumentTransportBuilder builder, Class<?> withType);
}
