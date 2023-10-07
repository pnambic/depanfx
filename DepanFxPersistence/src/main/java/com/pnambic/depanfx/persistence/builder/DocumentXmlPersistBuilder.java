package com.pnambic.depanfx.persistence.builder;

import com.pnambic.depanfx.persistence.DocumentXmlPersist;
import com.pnambic.depanfx.persistence.ObjectXmlConverter;
import com.thoughtworks.xstream.XStream;

public class DocumentXmlPersistBuilder {

  private XStream xstream;

  public DocumentXmlPersistBuilder() {

  }

  public void setXStream() {
    xstream = new XStream();
  }

  public void setNoReferences() {
    xstream.setMode(XStream.NO_REFERENCES);
  }

  public void addAlias(String alias, Class<?> type) {
    xstream.alias(alias, type);
  }

  public void addConverter(ObjectXmlConverter<?> contrib) {
    addAlias(contrib.getTag(), contrib.forType());
    xstream.allowTypes(contrib.getAllowTypes());
    xstream.registerConverter(new DelegateObjectXmlConverter(contrib, xstream.getMapper()));
  }

  public DocumentXmlPersist buildDocumentXmlPersist() {
    return new DocumentXmlPersist(xstream);
  }
}
