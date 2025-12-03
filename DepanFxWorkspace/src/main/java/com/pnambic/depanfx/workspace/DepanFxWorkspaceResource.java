package com.pnambic.depanfx.workspace;

import com.pnambic.depanfx.base.tooldata.DepanFxBaseToolData;

import java.util.Comparator;
import java.util.Objects;

public interface DepanFxWorkspaceResource<T> {

  DepanFxProjectDocument getDocument();

  T getResource();

  /**
   * Provides an alphabetically ordered sequence of filter resources,
   * based on the tool name of each resource.
   * This helps ensure that consumers always see the same order,
   * regardless of set construction.
   *
   * Only one required.
   * Suitable for {@code .sorted(DepanFxWorkspaceResource.BY_RESOURCE_NAME)}
   */
  public static final Comparator<DepanFxWorkspaceResource<? extends DepanFxBaseToolData>>
  BY_RESOURCE_NAME = (l, r) ->
      DepanFxBaseToolData.BY_TOOL_NAME.compare(
            l.getResource(), r.getResource());

  public static <T> DepanFxWorkspaceResource<T> forUpdate(
      DepanFxWorkspaceResource<T> originalRsrc, T updateData) {
    return new ForUpdateWorkspaceResource<>(originalRsrc, updateData);
  }

  public static <T> DepanFxWorkspaceResource<T> forSource(
      DepanFxProjectDocument rsrcDoc, T updateData) {
    return new StaticWorkspaceResource<>(rsrcDoc, updateData);
  }

  /**
   * Indicates whether the resource is bound to a conserved project.
   *
   * The built-in project and user projects are conserved projects,
   * such that their contents will be reliably restored after a restart.
   *
   * For update resources are not yet saved, and the scratch project
   * is not a conserved project.
   *
   * @return {@code true} if the resource's document is an unsaved location
   *   (e.g. in a scratch project or is marked for updated).
   */
  public static boolean isUnsavedResource(
      DepanFxWorkspaceResource<?> resource, DepanFxWorkspace workspace) {
    // For update resource have not be saved.
    if (resource instanceof ForUpdateWorkspaceResource) {
      return true;
    }
    // The scratch tree is not bound to a conversed project.
    if (workspace.getScratchProjectTree()
        .equals(resource.getDocument().getProject())) {
      return true;
    }

    // Resource is bound to a persistent project.
    return false;
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
