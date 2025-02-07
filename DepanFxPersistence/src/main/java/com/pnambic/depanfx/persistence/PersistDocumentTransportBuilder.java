package com.pnambic.depanfx.persistence;

import com.pnambic.depanfx.persistence.xstream.PersistXstreamObjectConverter;
import com.pnambic.modxstream.XstreamDocumentTransportBuilder;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Extend the transport build for the DepanFX context
 * <ul>
 * <li>Configure standard options in construct</li>
 * <li>Provide {@link #addConverter(PersistObjectConverter)} installing
 *   converter contributions.</li>
 * </ul>
 */
public class PersistDocumentTransportBuilder
    extends XstreamDocumentTransportBuilder {

  public PersistDocumentTransportBuilder() {
    setNoReferences();
    addDefaultImplementation(ArrayList.class, Collection.class);
  }

  public void addConverter(PersistObjectConverter<?> contrib) {
    addConverter(new PersistXstreamObjectConverter<>(contrib));
  }
}
