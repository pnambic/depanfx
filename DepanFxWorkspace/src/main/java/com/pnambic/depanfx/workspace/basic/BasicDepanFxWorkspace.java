package com.pnambic.depanfx.workspace.basic;

import com.pnambic.depanfx.graph.context.plugins.ContextModelRegistry;
import com.pnambic.depanfx.persistence.DocumentXmlPersist;
import com.pnambic.depanfx.persistence.plugins.DocumentPersistenceRegistry;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.documents.DocumentRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

  private final ContextModelRegistry modelRegistry;

  private final DocumentPersistenceRegistry persistRegistry;

  private final List<DepanFxProjectTree> projectList = new ArrayList<>();

  private final DocumentRegistry documentRegistry = new DocumentRegistry();

  private final String workspaceName;

  @Autowired
  public BasicDepanFxWorkspace(
      ContextModelRegistry modelRegistry,
      DocumentPersistenceRegistry resourceModels) {
    this(WORKSPACE_NAME, modelRegistry, resourceModels);
  }

  public BasicDepanFxWorkspace(String workspaceName,
      ContextModelRegistry modelRegistry,
      DocumentPersistenceRegistry persistRegistry) {
    this.workspaceName = workspaceName;
    this.modelRegistry = modelRegistry;
    this.persistRegistry = persistRegistry;
  }

  @Override
  public List<DepanFxProjectTree> getProjectList() {
    return projectList;
  }

  @Override
  public void addProject(DepanFxProjectTree project) {
    projectList.add(project);
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
      return registerDocumentSave(projDoc, document);
    }
  }

  @Override
  public Optional<DepanFxWorkspaceResource> importDocument(DepanFxProjectDocument projDoc)
      throws IOException {
    DocumentXmlPersist persist =
        persistRegistry.getDocumentPersist(getMemberUri(projDoc));
    persist.addContextValue(DepanFxWorkspace.class, this);

    try (Reader importer = openForLoad(projDoc)) {
      Object document = persist.load(importer);
      return registerDocumentLoad(projDoc, document);
    }
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
        .map(t -> buildProjectDocument(t, resourcePath));
  }

  @Override
  public Optional<DepanFxWorkspaceResource> toWorkspaceResource(
      DepanFxProjectDocument projDoc, Object resource) {
    return Optional.of(new WorkspaceResource(projDoc, resource));
  }

  @Override
  public Optional<DepanFxWorkspaceResource> getWorkspaceResource(
      DepanFxProjectDocument resourceDoc) {
    Optional<Object> optResource =
        documentRegistry.findResource(getMemberUri(resourceDoc));
    if (optResource.isPresent()) {
      return toWorkspaceResource(resourceDoc, optResource.get());
    }

    try {
      return importDocument(resourceDoc);
    } catch (IOException errIo) {
      LOG.error("Unable to aquire resource {}",
          resourceDoc, errIo);
      throw new RuntimeException(
          "Unable to aquire resource " + resourceDoc, errIo);
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

  private Optional<DepanFxWorkspaceResource> registerDocumentSave(
      DepanFxProjectDocument projDoc, Object document) {
    Optional<DepanFxWorkspaceResource> result =
        registerDocumentLoad(projDoc, document);

    // Register new documents with their project, so the UI gets a chance
    // to update.
    result.ifPresent(r ->
        r.getDocument().getProject().registerDocument(
            r.getDocument()));
    return result;
  }

  /**
   * All loaded/known documents are saved in the cache.
   */
  private Optional<DepanFxWorkspaceResource> registerDocumentLoad(
      DepanFxProjectDocument projDoc, Object document) {
    Optional<DepanFxWorkspaceResource> result =
        toWorkspaceResource(projDoc, document);
    result.ifPresent(r ->
        documentRegistry.registerDocument(r.getResource(),
            getMemberUri(r.getDocument())));
    return result;
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

  private URI getMemberUri( DepanFxProjectMember member) {
    return member.getMemberPath().toUri();
  }

  private class WorkspaceResource implements DepanFxWorkspaceResource {

    private DepanFxProjectDocument document;

    private Object resource;

    public WorkspaceResource(DepanFxProjectDocument document, Object resource) {
      this.document = document;
      this.resource = resource;
    }

    @Override
    public DepanFxWorkspace getWorkspace() {
      return BasicDepanFxWorkspace.this;
    }

    @Override
    public DepanFxProjectDocument getDocument() {
      return document;
    }

    @Override
    public Object getResource() {
      return resource;
    }
  }
}
