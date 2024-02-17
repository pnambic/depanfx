package com.pnambic.depanfx.persistence.plugins;

import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;

public interface DocumentPersistenceContribution {

  /**
   * Is this contribution suitable for the supplied document?
   */
  boolean acceptsDocument(Object document);

  /**
   * Is this contribution suitable for the supplied filename extension?
   */
  boolean acceptsExt(String extText);

  void prepareTransport(PersistDocumentTransportBuilder builder);
}
