package com.pnambic.depanfx.workspace.projects;

import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import java.nio.file.Path;

public interface DepanFxBuiltInContribution {

  @SuppressWarnings("serial")
  public class MissingDependencyException extends RuntimeException {

    private final Path sourceRsrc;

    private final Path missingRsrc;

    public MissingDependencyException(Path sourceRsrc, Path missingRsrc) {
      this.sourceRsrc = sourceRsrc;
      this.missingRsrc = missingRsrc;
    }

    @Override
    public String getMessage() {
      StringBuilder result = new StringBuilder();
      result.append("Source resource ");
      result.append(sourceRsrc.toString());
      result.append(" unable to locate ");
      result.append(missingRsrc.toString());
      return result.toString();
    }
  }

  boolean matches(DepanFxProjectDocument projDoc);

  Path getPath();

  Object getDocument();

  public abstract class Basic implements DepanFxBuiltInContribution {

    private final Path path;

    public Basic(Path path) {
      this.path = path;
    }

    @Override
    public boolean matches(DepanFxProjectDocument projDoc) {
      return getPath().equals(projDoc.getMemberPath());
    }

    @Override
    public Path getPath() {
      return path;
    }
  }

  public class Simple extends Basic {

    private final Object document;

    public Simple(Path path, Object document) {
      super(path);
      this.document = document;
    }

    @Override
    public Object getDocument() {
      return document;
    }
  }

  public abstract class Dependent extends Basic {

    /**
     * Dependent contributions create their document late.
     */
    private Object document = null;

    public Dependent(Path path) {
      super(path);
    }

    @Override
    public Object getDocument() {
      return document;
    }

    public Object installDocument(DepanFxBuiltInProject project) {
      document = buildDocument(project);
      return document;
    };

    protected abstract Object buildDocument(DepanFxBuiltInProject project);
  }
}
