package com.pnambic.depanfx.workspace.basic;

import com.pnambic.depanfx.persistence.DocumentXmlPersist;
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
    result.add(((DepanFxBuiltInProject) builtInProj).getProjectTree());
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
  public Optional<DepanFxWorkspaceResource> saveDocument(
      DepanFxProjectDocument projDoc, Object document)
      throws IOException {
    DocumentXmlPersist persist = persistRegistry.getDocumentPersist(document);
    persist.addContextValue(DepanFxWorkspace.class, this);

    try (Writer saver = openForSave(projDoc)) {
      persist.save(saver, document);
      Optional<DepanFxWorkspaceResource> result =
          toWorkspaceResource(projDoc, document);
      result.ifPresent(this::registerProjectDocument);
      return result;
    }
  }

  @Override
  public Optional<DepanFxWorkspaceResource> loadDocument(
      DepanFxProjectDocument projDoc, String expectedLabel) {
    DocumentXmlPersist persist =
        persistRegistry.getDocumentPersist(getMemberUri(projDoc));
    persist.addContextValue(DepanFxWorkspace.class, this);

    try (Reader importer = openForLoad(projDoc)) {
      Object document = persist.load(importer);
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
    for (DepanFxProjectTree project : projectList) {
      Optional<DepanFxProjectContainer> result = project.asProjectContainer(uriPath);
      if (result.isPresent()) {
        return result;
      }
    }
    return Optional.empty();
  }

  @Override
  public Optional<DepanFxProjectDocument> toProjectDocument(URI uri) {
    Path uriPath = Paths.get(uri);
    for (DepanFxProjectTree project : projectList) {
      Optional<DepanFxProjectDocument> result = project.asProjectDocument(uriPath);
      if (result.isPresent()) {
        return result;
      }
    }
    return Optional.empty();
  }

  @Override
  public Optional<DepanFxProjectDocument> toProjectDocument(
      String projectName, String resourcePath) {

    return findProjectByName(projectName)
        .map(p -> buildProjectDocument(p, resourcePath));
  }


  @Override
  public Optional<DepanFxWorkspaceResource> getWorkspaceResource(
      DepanFxProjectDocument resourceDoc, String expectedContent) {
    if (resourceDoc.getProject().equals(getBuiltInProjectTree())) {
      return ((DepanFxBuiltInProject) getBuiltInProject())
          .getResource(resourceDoc);
    }
    return findResource(resourceDoc)
        // No need to re-register if it was found.
        .map(r -> new WorkspaceResource(resourceDoc))
        .map(DepanFxWorkspaceResource.class::cast)
        .map(Optional::of)
        .orElseGet(() -> loadDocument(resourceDoc, expectedContent));
  }

  @Override
  public Optional<DepanFxWorkspaceResource> getWorkspaceResource(
      DepanFxProjectDocument resourceDoc, Class<?> type) {
    return getWorkspaceResource(resourceDoc, type.getName())
        .filter(d -> expectType(type, d));
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

  private void registerProjectDocument(DepanFxWorkspaceResource wkspRsrc) {
    wkspRsrc.getDocument().getProject()
        .registerDocument(wkspRsrc.getDocument());
  }

  /**
   * All loaded/known documents are saved in the cache.
   */
  private Optional<DepanFxWorkspaceResource> toWorkspaceResource(
      DepanFxProjectDocument projDoc, Object resource) {
    documentRegistry.registerDocument(projDoc, resource);
    return Optional.of(new WorkspaceResource(projDoc));
  }

  private Optional<Object> findResource(DepanFxProjectDocument resourceUri) {
    return documentRegistry.findResource(resourceUri);
  }

  private static boolean expectType(
      Class<?> expectedType, DepanFxWorkspaceResource wrkspRsrc) {
    Class<? extends Object> rsrcType = wrkspRsrc.getResource().getClass();
    if (expectedType.isAssignableFrom(rsrcType)) {
      return true;
    }
    LOG.warn("Expected type {}, but document is {}",
        expectedType.getName(), rsrcType.getName());
    return false;
  }

  private FileReader openForLoad(DepanFxProjectDocument projDoc) throws IOException {
    return new FileReader(buildDocumentFile(projDoc));
  }

  private FileWriter openForSave(DepanFxProjectDocument projDoc) throws IOException {
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

  private class WorkspaceResource implements DepanFxWorkspaceResource {

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
    public Object getResource() {
      return findResource(document).get();
    }
  }
}
