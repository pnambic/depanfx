package com.pnambic.depanfx.persistence;

import java.util.Map;

/**
 * Encapsulate common access patterns for serialized data.
 */
public class PersistTagDataResult {

  private final Map<String, Object> source;

  public PersistTagDataResult(Map<String, Object> source) {
    this.source = source;
  }

  /**
   * Mandatory string.
   */
  public String getString(String key) {
    return (String) source.get(key);
  }

  /**
   * Mandatory object.
   */
  public <T> T getObject(String key, Class<T> type) {
    return type.cast(source.get(key));
  }

  /**
   * For optional or added boolean.
   */
  public boolean getBoolean(String key, boolean onAbsent) {
    Boolean result = (Boolean) source.get(key);
    if (result != null) {
      return result.booleanValue();
    }
    return onAbsent;
  }

  /**
   * For optional or added string.
   */
  public String getString(String key, String onAbsent) {
    String result = (String) source.get(key);
    if (result != null) {
      return result;
    }
    return onAbsent;
  }

  /**
   * For optional or added objects.
   */
  public <T> T getObject(String key, Class<T> type, T onAbsent) {
    Object result = source.get(key);
    if (result != null) {
      return type.cast(result);
    }
    return onAbsent;
  }
}
