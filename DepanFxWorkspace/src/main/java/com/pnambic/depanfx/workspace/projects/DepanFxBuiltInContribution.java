package com.pnambic.depanfx.workspace.projects;

import com.pnambic.depanfx.workspace.DepanFxProjectDocument;

import java.nio.file.Path;

public interface DepanFxBuiltInContribution<T> {

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

  T getDocument();

  public abstract class Basic<T> implements DepanFxBuiltInContribution<T> {

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

  public class Simple<T> extends Basic<T> {

    private final T document;

    public Simple(Path path, T document) {
      super(path);
      this.document = document;
    }

    @Override
    public T getDocument() {
      return document;
    }
  }

  public abstract class Dependent<T> extends Basic<T> {

    /**
     * Dependent contributions create their document late.
     */
    private T document = null;

    public Dependent(Path path) {
      super(path);
    }

    @Override
    public T getDocument() {
      return document;
    }

    public T installDocument(DepanFxBuiltInProject project) {
      document = buildDocument(project);
      return document;
    };

    protected abstract T buildDocument(DepanFxBuiltInProject project);
  }
}
