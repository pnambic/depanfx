package com.pnambic.depanfx.workspace.basic;

import com.pnambic.depanfx.workspace.DepanFxProjectBadMember;
import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectSpi;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BasicDepanFxProjectTree implements DepanFxProjectTree {

  private DepanFxProjectSpi projectSpi;

  private final List<ProjectTreeListener> listeners =
      new ArrayList<>();

  public BasicDepanFxProjectTree(DepanFxProjectSpi projectSpi) {
    this.projectSpi = projectSpi;
  }

  @Override
  public String getMemberName() {
    return projectSpi.getProjectName();
  }

  @Override
  public Path getMemberPath() {
    return projectSpi.getProjectPath();
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
  public DepanFxProjectBadMember asBadMember(Path childPath) {
    return new BasicDepanFxProjectBadMember(this, childPath);
  }

  @Override
  public Optional<DepanFxProjectContainer> asProjectContainer(Path dirPath) {
    return projectSpi.getMemberPath(dirPath)
        .map(p -> new BasicDepanFxProjectContainer(this, p));
  }

  @Override
  public Optional<DepanFxProjectDocument> asProjectDocument(Path docPath) {
    return projectSpi.getMemberPath(docPath)
        .map(p -> new BasicDepanFxProjectDocument(this, p));
  }

  @Override
  public void createContainer(DepanFxProjectContainer projDir) {
    projectSpi.createContainer(projDir);
    notifyContainerAdded(projDir);
  }

  @Override
  public void registerContainer(DepanFxProjectContainer projDir) {
    notifyContainerRegistered(projDir);
  }

  @Override
  public void deleteContainer(DepanFxProjectContainer projDir) {
    projectSpi.deleteContainer(projDir);
    notifyContainerDeleted(projDir);
  }

  @Override
  public void deleteDocument(DepanFxProjectDocument projDoc) {
    projectSpi.deleteDocument(projDoc);
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

  private void notifyContainerRegistered(DepanFxProjectContainer projDir) {
    listeners.forEach(l -> l.onContainerRegistered(projDir));
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
