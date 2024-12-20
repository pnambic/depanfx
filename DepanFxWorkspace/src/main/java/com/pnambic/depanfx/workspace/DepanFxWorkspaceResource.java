package com.pnambic.depanfx.workspace;

public interface DepanFxWorkspaceResource<T> {

  DepanFxProjectDocument getDocument();

  T getResource();

  public static <T> DepanFxWorkspaceResource<T> forUpdate(
      DepanFxWorkspaceResource<T> originalRsrc, T updateData) {
    return new ForUpdateWorkspaceResource<>(originalRsrc, updateData);
  }

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

  /**
   * This implementation is suitable for a resource where the content is being
   * updated.
   */
  public static class ForUpdateWorkspaceResource<T>
      implements DepanFxWorkspaceResource<T> {

    private final DepanFxWorkspaceResource<T> baseRsrc;

    private final T rsrcData;

    public ForUpdateWorkspaceResource(
        DepanFxWorkspaceResource<T> baseRsrc, T rsrcData) {
      this.baseRsrc = baseRsrc;
      this.rsrcData = rsrcData;
    }

    @Override
    public DepanFxProjectDocument getDocument() {
      return baseRsrc.getDocument();
    }

    @Override
    public T getResource() {
      return rsrcData;
    }
  }
}
