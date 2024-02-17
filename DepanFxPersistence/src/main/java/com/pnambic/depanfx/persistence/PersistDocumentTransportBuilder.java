package com.pnambic.depanfx.persistence;

import com.pnambic.depanfx.persistence.xstream.PersistXstreamObjectConverter;
import com.pnambic.depanfx.xstream.XstreamDocumentTransport;
import com.pnambic.depanfx.xstream.XstreamDocumentTransportBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class PersistDocumentTransportBuilder {

  private XstreamDocumentTransportBuilder builder =
      new XstreamDocumentTransportBuilder();

  public PersistDocumentTransportBuilder() {
    builder.setXStream();
    builder.setNoReferences();
  }

  public void addAlias(String alias, Class<?> type) {
    builder.addAlias(alias, type);
  }

  public void addAliasType(String alias, Class<?> type) {
    builder.addAliasType(alias, type);
  }

  public void addAliasField(
      String alias, Class<?> fieldType, String fieldName) {
    builder.addAliasField(alias, fieldType, fieldName);
  }

  public void addAllowedType(Class<?>[] allowedTypes) {
    builder.addAllowedType(allowedTypes);
  }

  public void addConverter(PersistObjectConverter<?> contrib) {
    builder.addConverter(new PersistXstreamObjectConverter<>(contrib));
  }

  public void addImplicitCollection(Class<?> type, String fieldName) {
    builder.addImplicitCollection(type, fieldName);
  }

  public void processAnnotations(Class<?> type) {
    builder.processAnnotations(type);
  }

  public PersistDocumentTransport buildDocumentXmlPersist() {
    return new XstreamPersistDocumentTransport(
        builder.buildDocumentXmlPersist());
  }

  private static class XstreamPersistDocumentTransport
      implements PersistDocumentTransport {

    private final XstreamDocumentTransport transport;

    public XstreamPersistDocumentTransport(XstreamDocumentTransport transport) {
      this.transport = transport;
    }

    @Override
    public void addContextValue(Object key, Object value) {
      transport.addContextValue(key, value);
    }

    @Override
    public Object load(Reader src) throws IOException {
      return transport.load(src);
    }

    @Override
    public void save(Writer dst, Object item) throws IOException {
      transport.save(dst, item);
    }
  }
}
