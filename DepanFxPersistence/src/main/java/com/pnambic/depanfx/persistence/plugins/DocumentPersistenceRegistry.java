package com.pnambic.depanfx.persistence.plugins;

import com.pnambic.depanfx.persistence.DocumentXmlPersist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URI;
import java.util.List;

@Component
public class DocumentPersistenceRegistry {

  @SuppressWarnings("serial")
  public class UnknownExtensionException extends RuntimeException {

    private String extText;

    public UnknownExtensionException(String extText) {
      this.extText = extText;
    }

    @Override
    public String getMessage() {
      return "No contribution for extension " + extText;
    }
  }

  @SuppressWarnings("serial")
  public class UnknownDocumentException extends RuntimeException {

    private Object document;

    public UnknownDocumentException(Object document) {
      this.document = document;
    }

    @Override
    public String getMessage() {
      return "No contribution for document " + document.getClass().getName();
    }
  }

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
        .orElseThrow(() -> new UnknownExtensionException(extText));
  }

  private DocumentPersistenceContribution findContribution(Object document) {
    return persistModels.stream()
        .filter(c -> c.acceptsDocument(document))
        .findFirst()
        .orElseThrow(() -> new UnknownDocumentException(document));
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
