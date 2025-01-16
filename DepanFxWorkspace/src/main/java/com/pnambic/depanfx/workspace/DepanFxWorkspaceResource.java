package com.pnambic.depanfx.workspace;

import java.util.Objects;

public interface DepanFxWorkspaceResource<T> {

  DepanFxProjectDocument getDocument();

  T getResource();

  public static <T> DepanFxWorkspaceResource<T> forUpdate(
      DepanFxWorkspaceResource<T> originalRsrc, T updateData) {
    return new ForUpdateWorkspaceResource<>(originalRsrc, updateData);
  }

  public static <T> DepanFxWorkspaceResource<T> forSource(
      DepanFxProjectDocument rsrcDoc, T updateData) {
    return new StaticWorkspaceResource<>(rsrcDoc, updateData);
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

    @Override
    public int hashCode() {
      return DepanFxWorkspaceResource.calcHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
      return DepanFxWorkspaceResource.calcEquals(this, obj);
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
      // Avoid stacking base resource references.
      if (baseRsrc instanceof ForUpdateWorkspaceResource<T> forUpdateRsrc) {
        this.baseRsrc = forUpdateRsrc.baseRsrc;
      } else {
        this.baseRsrc = baseRsrc;
      }
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

    @Override
    public int hashCode() {
      return DepanFxWorkspaceResource.calcHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
      return DepanFxWorkspaceResource.calcEquals(this, obj);
    }
  }

  private static int calcHashCode(DepanFxWorkspaceResource<?> rsrc) {
    return Objects.hash(rsrc.getDocument(), rsrc.getResource());
  }

  private static boolean calcEquals(
      DepanFxWorkspaceResource<?> rsrc, Object obj) {

    if (rsrc == obj) {
      return true;
    }
    if (!(obj instanceof DepanFxWorkspaceResource)) {
      return false;
    }

    DepanFxWorkspaceResource<?> other = (DepanFxWorkspaceResource<?>) obj;
    return Objects.equals(rsrc.getResource(), other.getResource())
        && Objects.equals(rsrc.getDocument(), other.getDocument());
  }

}
