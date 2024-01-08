package com.pnambic.depanfx.workspace.projects;

import com.pnambic.depanfx.workspace.DepanFxProjectDocument;

import java.nio.file.Path;

public interface DepanFxBuiltInContribution {

  boolean matches(DepanFxProjectDocument projDoc);

  Path getPath();

  Object getDocument();

  public class Simple implements DepanFxBuiltInContribution {

    private final Path path;

    private final Object document;

    public Simple(Path path, Object document) {
      this.path = path;
      this.document = document;
    }

    @Override
    public boolean matches(DepanFxProjectDocument projDoc) {
      return getPath().equals(projDoc.getMemberPath());
    }

    @Override
    public Path getPath() {
      return path;
    }

    @Override
    public Object getDocument() {
      return document;
    }
  }
}
