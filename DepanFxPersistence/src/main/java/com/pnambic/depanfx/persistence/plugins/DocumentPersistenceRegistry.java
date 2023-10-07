package com.pnambic.depanfx.persistence.plugins;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pnambic.depanfx.persistence.DocumentXmlPersist;

@Component
public class DocumentPersistenceRegistry {

  private static final String EXT_PERIOD = ".";

  private final List<DocumentPersistenceContribution> persistModels;

  @Autowired
  public DocumentPersistenceRegistry(List<DocumentPersistenceContribution> persistModels) {
    this.persistModels = persistModels;
  }

  public DocumentXmlPersist getDocumentPersist(Object document) {
    return findContribution(document).getDocumentPersist();
  }

  public DocumentXmlPersist getDocumentPersist(URI uri) {
    String extText = getExtText(uri);
    return findContribution(extText).getDocumentPersist();
  }

  private DocumentPersistenceContribution findContribution(String extText) {
    return persistModels.stream()
        .filter(c -> c.acceptsExt(extText))
        .findFirst()
        .get();
  }

  private DocumentPersistenceContribution findContribution(Object document) {
    return persistModels.stream()
        .filter(c -> c.acceptsDocument(document))
        .findFirst()
        .get();
  }

  private String getExtText(URI uri) {
    File extFile = new File(uri.getPath());
    String srcName = extFile.getName();
    int extIndex = srcName.lastIndexOf(EXT_PERIOD);
    if (extIndex < 0) {
      return "";
    }
    return srcName.substring(extIndex + 1);
  }
}
