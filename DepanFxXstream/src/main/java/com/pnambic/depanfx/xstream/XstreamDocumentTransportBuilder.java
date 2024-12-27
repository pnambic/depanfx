package com.pnambic.depanfx.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class XstreamDocumentTransportBuilder {

  private XppDriver streamDriver;

  private XStream xstream;

  public XstreamDocumentTransportBuilder() {
    streamDriver = new XppDriver();
    xstream = new XStream(streamDriver);
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

  public void addConverter(XstreamObjectConverter<?> contrib) {
    addAlias(contrib.getTag(), contrib.forType());
    addAllowedType(contrib.getAllowTypes());
    xstream.registerConverter(
        new DelegateObjectXmlConverter(contrib, xstream.getMapper()));
  }

  public void addDefaultImplementation(Class<?> useType, Class<?> forType) {
    xstream.addDefaultImplementation(useType, forType);
  }

  public void addImplicitCollection(Class<?> type, String fieldName) {
    xstream.addImplicitCollection(type, fieldName);
  }

  public void processAnnotations(Class<?> type) {
    xstream.processAnnotations(type);
  }

  public XstreamDocumentTransport buildDocumentXmlPersist() {
    return new XstreamDocumentTransport(xstream, streamDriver);
  }
}
