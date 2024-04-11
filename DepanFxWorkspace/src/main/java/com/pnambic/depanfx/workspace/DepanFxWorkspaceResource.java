package com.pnambic.depanfx.workspace;

public interface DepanFxWorkspaceResource<T> {

  DepanFxProjectDocument getDocument();

  T getResource();

  /**
   * This implementation is suitable for resource that reference static content,
   * such as resource in the built-in project.  This implementation may also
   * be useful for transitory resource, such as temporary compositions used to
   * initialize editor dialogs.
   */
  public static class StaticWorkspaceResource<T>
      implements DepanFxWorkspaceResource<T> {

    private final DepanFxProjectDocument rsrcDoc;

    private final T rsrcData;

    public StaticWorkspaceResource(DepanFxProjectDocument rsrcDoc, T rsrcData) {
      this.rsrcDoc = rsrcDoc;
      this.rsrcData = rsrcData;
    }

    @Override
    public DepanFxProjectDocument getDocument() {
      return rsrcDoc;
    }

    @Override
    public T getResource() {
      return rsrcData;
    }
  }
}
