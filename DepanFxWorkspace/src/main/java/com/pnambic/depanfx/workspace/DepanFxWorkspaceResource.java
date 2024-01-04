package com.pnambic.depanfx.workspace;

public interface DepanFxWorkspaceResource {

  DepanFxProjectDocument getDocument();

  Object getResource();

  /**
   * This implementation is suitable for resource that reference static content,
   * such as resource in the built-in project.  This implementation may also
   * be useful for transitory resource, such as temporary compositions used to
   * initialize editor dialogs.
   */
  public static class StaticWorkspaceResource
      implements DepanFxWorkspaceResource {

    private final DepanFxProjectDocument rsrcDoc;

    private final Object rsrcData;

    public StaticWorkspaceResource(DepanFxProjectDocument rsrcDoc, Object rsrcData) {
      this.rsrcDoc = rsrcDoc;
      this.rsrcData = rsrcData;
    }

    @Override
    public DepanFxProjectDocument getDocument() {
      return rsrcDoc;
    }

    @Override
    public Object getResource() {
      return rsrcData;
    }
  }
}
