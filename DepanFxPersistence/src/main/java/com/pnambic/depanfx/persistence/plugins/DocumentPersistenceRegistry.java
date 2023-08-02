package com.pnambic.depanfx.persistence.plugins;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pnambic.depanfx.persistence.DocumentXmlPersist;

@Component
public class DocumentPersistenceRegistry {

  private final List<DocumentPersistenceContribution> persistModels;

  @Autowired
  public DocumentPersistenceRegistry(List<DocumentPersistenceContribution> persistModels) {
    this.persistModels = persistModels;
  }

  public DocumentXmlPersist getDocumentPersist(Object document) {
    return findContribution(document).getDocumentPersist(document);
  }

  private DocumentPersistenceContribution findContribution(Object document) {
    return persistModels.stream()
        .filter(c -> c.acceptsDocument(document))
        .findFirst()
        .get();
  }
}
