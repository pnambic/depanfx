package com.pnambic.depanfx.persistence.builder;

import com.pnambic.depanfx.persistence.DocumentXmlPersist;
import com.pnambic.depanfx.persistence.ObjectXmlConverter;
import com.thoughtworks.xstream.XStream;

import java.util.ArrayList;
import java.util.Collection;

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

  public void addAliasType(String alias, Class<?> type) {
    xstream.aliasType(alias, type);
  }

  public void addAliasField(
      String alias, Class<?> fieldType, String fieldName) {
    xstream.aliasField(alias, fieldType, fieldName);
  }

  public void addAllowedType(Class<?>[] allowedTypes) {
    xstream.allowTypes(allowedTypes);
  }

  public void addConverter(ObjectXmlConverter<?> contrib) {
    addAlias(contrib.getTag(), contrib.forType());
    addAllowedType(contrib.getAllowTypes());
    xstream.registerConverter(
        new DelegateObjectXmlConverter(contrib, xstream.getMapper()));
  }

  public void addImplicitCollection(Class<?> type, String fieldName) {
    xstream.addImplicitCollection(type, fieldName);
    xstream.addDefaultImplementation(ArrayList.class, Collection.class);
  }

  public void processAnnotations(Class<?> type) {
    xstream.processAnnotations(type);
  }

  public DocumentXmlPersist buildDocumentXmlPersist() {
    return new DocumentXmlPersist(xstream);
  }
}
