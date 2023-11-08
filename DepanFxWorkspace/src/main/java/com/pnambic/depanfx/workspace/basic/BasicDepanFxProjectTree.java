package com.pnambic.depanfx.workspace.basic;

import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BasicDepanFxProjectTree implements DepanFxProjectTree {

  private final String projectName;

  private final Path projectPath;

  private final List<ProjectTreeListener> listeners =
      new ArrayList<>();

  public BasicDepanFxProjectTree(String projectName, Path projectPath) {
    this.projectName = projectName;

    this.projectPath = projectPath;
  }

  @Override
  public String getMemberName() {
    return projectName;
  }

  @Override
  public Path getMemberPath() {
    return projectPath;
  }
  @Override
  public DepanFxProjectTree getProject() {
    return this;
  }


  /**
   * Projects have no project parent.  Projects may be associated with a
   * workspace, but that relationship is controlled from the other workspace
   * side.
   */
  @Override
  public Optional<DepanFxProjectContainer> getParent() {
    return Optional.empty();
  }

  @Override
  public Optional<DepanFxProjectContainer> asProjectContainer(Path dirPath) {
    if (dirPath.startsWith(projectPath)) {
      BasicDepanFxProjectContainer result =
          new BasicDepanFxProjectContainer(this, dirPath);
      return Optional.of(result);
    }
    return Optional.empty();
  }

  @Override
  public Optional<DepanFxProjectDocument> asProjectDocument(Path docPath) {
    if (docPath.startsWith(projectPath)) {
      BasicDepanFxProjectDocument result =
          new BasicDepanFxProjectDocument(this, docPath);
      return Optional.of(result);
    }
    return Optional.empty();
  }

  @Override
  public void createContainer(DepanFxProjectContainer projDir) {
    Path dirPath = projDir.getMemberPath();
    dirPath.toFile().mkdirs();
    notifyContainerAdded(projDir);
  }

  @Override
  public void deleteContainer(DepanFxProjectContainer projDir) {
    Path dirPath = projDir.getMemberPath();
    dirPath.toFile().delete();
    notifyContainerDeleted(projDir);
  }

  @Override
  public void deleteDocument(DepanFxProjectDocument projDoc) {
    Path dirPath = projDoc.getMemberPath();
    dirPath.toFile().delete();
    notifyDocumentDeleted(projDoc);
  }

  @Override
  public void registerDocument(DepanFxProjectDocument projDoc) {
    notifyDocumentAdded(projDoc);
  }

  @Override
  public void addListener(ProjectTreeListener listener) {
    listeners.add(listener);
  }

  @Override
  public void removeListener(ProjectTreeListener listener) {
    listeners.remove(listener);
  }

  private void notifyContainerAdded(DepanFxProjectContainer projDir) {
    listeners.forEach(l -> l.onContainerAdded(projDir));
  }

  private void notifyContainerDeleted(DepanFxProjectContainer projDir) {
    listeners.forEach(l -> l.onContainerDeleted(projDir));
  }

  private void notifyDocumentAdded(DepanFxProjectDocument projDoc) {
    listeners.forEach(l -> l.onDocumentAdded(projDoc));
  }

  private void notifyDocumentDeleted(DepanFxProjectDocument projDoc) {
    listeners.forEach(l -> l.onDocumentDeleted(projDoc));
  }

}
