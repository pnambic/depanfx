package com.pnambic.depanfx.workspace.basic;

import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

public class BasicDepanFxWorkspaceResource implements DepanFxWorkspaceResource {

  private final DepanFxProjectDocument document;

  private final Object resource;

  private final DepanFxWorkspace workspace;

  public BasicDepanFxWorkspaceResource(
      DepanFxProjectDocument document,
      Object resource,
      DepanFxWorkspace workspace) {
    this.document = document;
    this.resource = resource;
    this.workspace = workspace;
  }

  @Override
  public DepanFxProjectDocument getDocument() {
    return document;
  }

  @Override
  public Object getResource() {
    return resource;
  }

  @Override
  public DepanFxWorkspace getWorkspace() {
    return workspace;
  }
}
