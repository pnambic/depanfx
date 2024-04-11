package com.pnambic.depanfx.workspace.basic;

import com.pnambic.depanfx.persistence.PersistDocumentTransport;
import com.pnambic.depanfx.persistence.plugins.DocumentPersistenceRegistry;
import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxProjectSpi;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.documents.DocumentRegistry;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInProject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A common registry of workspace context.
 */
@Component
public class BasicDepanFxWorkspace implements DepanFxWorkspace {

  public static final String WORKSPACE_NAME = "Depan Workspace";

  private static final Logger LOG =
      LoggerFactory.getLogger(BasicDepanFxWorkspace.class);

  private final String workspaceName;

  private final DocumentPersistenceRegistry persistRegistry;

  private final DepanFxProjectSpi builtInProj;

  private final List<DepanFxProjectTree> projectList = new ArrayList<>();

  private final DocumentRegistry documentRegistry = new DocumentRegistry();

  private final List<WorkspaceListener> listeners = new ArrayList<>();

  private DepanFxProjectTree currentProject;

  @Autowired
  public BasicDepanFxWorkspace(
      DocumentPersistenceRegistry persistRegistry,
      @Qualifier("BuiltIn Workspace")
      DepanFxProjectSpi builtInProj) {
    this(WORKSPACE_NAME, persistRegistry, builtInProj);
  }

  public BasicDepanFxWorkspace(
      String workspaceName,
      DocumentPersistenceRegistry persistRegistry,
      DepanFxProjectSpi builtInProj) {
    this.workspaceName = workspaceName;
    this.persistRegistry = persistRegistry;
    this.builtInProj = builtInProj;
  }

  @Override
  public Optional<DepanFxProjectTree> getCurrentProject() {
    return Optional.ofNullable(currentProject);
  }

  @Override
  public void setCurrentProject(DepanFxProjectTree currentProject) {
    if (currentProject == null) {
      updateCurrentProject(null);
      return;
    }
    if (projectList.contains(currentProject)) {
      updateCurrentProject(currentProject);
      return;
    }
    LOG.warn("Unknown project {} cannot be current project",
        currentProject.getMemberName());
  }

  @Override
  public List<DepanFxProjectTree> getProjectList() {
    List<DepanFxProjectTree> result = new ArrayList<>(projectList.size() + 1);
    result.addAll(projectList);
    result.add(getBuiltInProjectTree());
    return result;
  }

  @Override
  public void addProject(DepanFxProjectTree project) {
    if (!projectList.contains(project)) {
      projectList.add(project);
      notifyProjectAdded(project);
      return;
    }
    LOG.warn("Cannot add a known project {} to workspace",
        project.getMemberName());
  }

  @Override
  public DepanFxProjectSpi getBuiltInProject() {
    return builtInProj;
  }

  @Override
  public DepanFxProjectTree getBuiltInProjectTree() {
    return ((DepanFxBuiltInProject) builtInProj).getProjectTree();
  }

  @Override
  public String getMemberName() {
    return workspaceName;
  }

  @Override
  public void exit() {
  }

  @Override
  public <T> Optional<DepanFxWorkspaceResource<T>> saveDocument(
      DepanFxProjectDocument projDoc, T document)
      throws IOException {
    PersistDocumentTransport transport =
        persistRegistry.getDocumentTransport(document);
    transport.addContextValue(DepanFxWorkspace.class, this);

    try (Writer saver = openForSave(projDoc)) {
      transport.save(saver, document);
      Optional<DepanFxWorkspaceResource<T>> result =
          toWorkspaceResource(projDoc, document);
      result.ifPresent(this::registerProjectDocument);
      return result;
    }
  }

  @Override
  public <T> Optional<DepanFxWorkspaceResource<T>> loadDocument(
      DepanFxProjectDocument projDoc, String expectedLabel) {
    PersistDocumentTransport transport =
        persistRegistry.getDocumentTransport(getMemberUri(projDoc));
    transport.addContextValue(DepanFxWorkspace.class, this);

    try (Reader importer = openForLoad(projDoc)) {
      @SuppressWarnings("unchecked")
      T document = (T) transport.load(importer);
      return toWorkspaceResource(projDoc, document);
    } catch (IOException errIo) {
      LOG.error("Unable to open {} at {}", expectedLabel, projDoc, errIo);
      throw new RuntimeException(
          "Unable to open " + expectedLabel + " at " + projDoc, errIo);
    }
  }

  @Override
  public Optional<DepanFxProjectContainer> toProjectContainer(URI uri) {
    Path uriPath = Paths.get(uri);
    return projectList.stream()
        .map(p -> p.asProjectContainer(uriPath))
        .filter(o -> o.isPresent())
        .map(o -> o.get())
        .findFirst();
  }

  @Override
  public Optional<DepanFxProjectDocument> toProjectDocument(URI uri) {
    Path uriPath = Paths.get(uri);

    return projectList.stream()
        .map(p -> p.asProjectDocument(uriPath))
        .filter(o -> o.isPresent())
        .map(o -> o.get())
        .findFirst();
  }

  @Override
  public Optional<DepanFxProjectDocument> toProjectDocument(
      String projectName, String resourcePath) {

    return findProjectByName(projectName)
        .map(p -> buildProjectDocument(p, resourcePath));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Optional<DepanFxWorkspaceResource<T>> getWorkspaceResource(
      DepanFxProjectDocument resourceDoc, Class<T> type) {
    return
        getWorkspaceResource(resourceDoc, type.getName())
        .filter(r ->expectType(type, r))
        .map(r -> (DepanFxWorkspaceResource<T>) r)
        .map(Optional::of)
        .orElse(Optional.empty());
  }

  /**
   * Provide a resource from a project document.
   *
   * @param resourceDoc - source of resource to load.
   * @param expectedContent - supplemental text for message load errors.
   *   This value is often the class name of the expected value.
   */
  @Override
  public <T> Optional<DepanFxWorkspaceResource<T>> getWorkspaceResource(
      DepanFxProjectDocument resourceDoc, String expectedContent) {
    // Check for a built in resource.
    if (getBuiltInProjectTree().equals(resourceDoc.getProject())) {
      return ((DepanFxBuiltInProject) getBuiltInProject())
          .getResource(resourceDoc);
    }
    // Check if the resource has already been loaded.
    if (findResource(resourceDoc).isPresent()) {
      WorkspaceResource<T> result = new WorkspaceResource<T>(resourceDoc);
      return Optional.of(result);
    }
    // Obtain the resource from the store.
    return loadDocument(resourceDoc, expectedContent);
  }

  @Override
  public void addListener(WorkspaceListener listener) {
    listeners .add(listener);
  }

  @Override
  public void removeListener(WorkspaceListener listener) {
    listeners.remove(listener);
  }

  private void updateCurrentProject(DepanFxProjectTree currentProject) {
    DepanFxProjectTree changedProject = this.currentProject;
    this.currentProject = currentProject;
    if (changedProject != null) {
      notifyProjectChanged(changedProject);
    }
    if (currentProject != null) {
      notifyProjectChanged(currentProject);
    }
  }

  private Optional<DepanFxProjectTree> findProjectByName(String projectName) {
    if (builtInProj.getProjectName().equals(projectName)) {
      return Optional.of(getBuiltInProjectTree());
    }

    return projectList.stream()
        .filter(t -> projectName.equals(t.getMemberName()))
        .findFirst();
  }

  private DepanFxProjectDocument buildProjectDocument(
      DepanFxProjectTree projectTree, String resourcePath) {
    Path projectRoot = projectTree.getMemberPath();
    Path resource = projectRoot.resolve(resourcePath);
    return new BasicDepanFxProjectDocument(projectTree, resource);
  }

  private void registerProjectDocument(
      DepanFxWorkspaceResource<?> wkspRsrc) {
    wkspRsrc.getDocument().getProject()
        .registerDocument(wkspRsrc.getDocument());
  }

  /**
   * All loaded/known documents are saved in the cache.
   */
  private <T> Optional<DepanFxWorkspaceResource<T>> toWorkspaceResource(
      DepanFxProjectDocument projDoc, T resource) {
    documentRegistry.registerDocument(projDoc, resource);
    return Optional.of(new WorkspaceResource<>(projDoc));
  }

  private Optional<Object> findResource(DepanFxProjectDocument resourceUri) {
    return documentRegistry.findResource(resourceUri);
  }

  private static <T> boolean expectType(
      Class<T> type, DepanFxWorkspaceResource<?> docRsrc) {
    Class<? extends Object> rsrcType = docRsrc.getResource().getClass();
    if (type.isAssignableFrom(rsrcType)) {
      return true;
    }
    LOG.warn("Expected type {}, but document is {}",
        type.getName(), rsrcType.getName());
    return false;
  }

  private FileReader openForLoad(DepanFxProjectDocument projDoc)
       throws IOException {
    return new FileReader(buildDocumentFile(projDoc));
  }

  private FileWriter openForSave(DepanFxProjectDocument projDoc)
      throws IOException {
    return new FileWriter(buildDocumentFile(projDoc));
  }

  private File buildDocumentFile(DepanFxProjectDocument projDoc) {
    return new File(getMemberUri(projDoc));
  }

  private void notifyProjectAdded(DepanFxProjectTree project) {
    listeners.forEach(l -> l.onProjectAdded(project));
  }

  private void notifyProjectDeleted(DepanFxProjectTree project) {
    listeners.forEach(l -> l.onProjectDeleted(project));
  }

  private void notifyProjectChanged(DepanFxProjectTree project) {
    listeners.forEach(l -> l.onProjectChanged(project));
  }

  private URI getMemberUri( DepanFxProjectMember member) {
    return member.getMemberPath().toUri();
  }

  private class WorkspaceResource<T> implements DepanFxWorkspaceResource<T> {

    private DepanFxProjectDocument document;

    public WorkspaceResource(DepanFxProjectDocument document) {
      this.document = document;
    }

    @Override
    public DepanFxProjectDocument getDocument() {
      return document;
    }

    /**
     * Any workspace resource should have already been loaded into the
     * document registry.
     */
    @Override
    @SuppressWarnings("unchecked")
    public T getResource() {
      return (T) findResource(document).get();
    }
  }
}
