package com.pnambic.depanfx.graph.context;

/**
 * Provide a base context for all contexts to share.
 */
public class BaseContextModelId implements ContextModelId {

  public static final String BASE_CONTEXT_KEY = "BaseContext";

  public static final String BASE_CONTEXT_PATH = "Base Context";

  @Override
  public String getContextModelKey() {
    return BASE_CONTEXT_KEY;
  }

  @Override
  public String getContextModelPath() {
    return BASE_CONTEXT_PATH;
  }

  /**
   * Since there should only be one instance of a FileSystemContextModelId
   * in a correct DepAn execution, just use object equality.
   */
  @Override
  public boolean equals(Object obj) {
    // Explicitly use the inherited equals() method, versus confusing any
    // future reviewers with its apparent absence.
    return super.equals(obj);
  }

  @Override
  public int hashCode() {
      return BASE_CONTEXT_KEY.hashCode();
  }
}
