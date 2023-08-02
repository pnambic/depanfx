package com.pnambic.depanfx.persistence.plugins;

import com.pnambic.depanfx.persistence.DocumentXmlPersist;

public interface DocumentPersistenceContribution {
  /**
   * Is this contribution suitable for the supplied document?
   */
  boolean acceptsDocument(Object document);

  DocumentXmlPersist getDocumentPersist(Object document);
}
