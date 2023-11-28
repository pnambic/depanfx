package com.pnambic.depanfx.workspace;

public interface DepanFxWorkspaceResource {

  DepanFxProjectDocument getDocument();

  Object getResource();

  public static class Simple implements DepanFxWorkspaceResource {

    private final DepanFxProjectDocument rsrcDoc;

    private final Object rsrcData;

    public Simple(DepanFxProjectDocument rsrcDoc, Object rsrcData) {
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
