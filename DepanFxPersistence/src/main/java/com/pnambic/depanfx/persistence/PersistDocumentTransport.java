package com.pnambic.depanfx.persistence;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Define document transfer processes.
 */
public interface PersistDocumentTransport {

  /**
   * Install a well-known value as the context for persistence operations.
   *
   * @param key typically the value's {@code .class} constant
   * @param value useful data
   */
  void addContextValue(Object key, Object value);

  /**
   * Load an object from the provided source.
   *
   * @param src source of persistent object
   * @return object from location
   * @throws IOException
   */
  Object load(Reader src) throws IOException;

  /**
   * Save an object to the provided destination.
   *
   * @param dst destination for persistent representation
   * @param item object to persist
   * @throws IOException
   */
  void save(Writer dst, Object item) throws IOException;
}
