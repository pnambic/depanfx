package com.pnambic.depanfx.workspace.basic;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pnambic.depanfx.graph.context.plugins.ContextModelRegistry;
import com.pnambic.depanfx.persistence.DocumentXmlPersist;
import com.pnambic.depanfx.persistence.plugins.DocumentPersistenceRegistry;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.documents.DocumentRegistry;

/**
 * A common registry of workspace context.
 */
@Component
public class BasicDepanFxWorkspace implements DepanFxWorkspace {

  public static final String WORKSPACE_NAME = "Depan Workspace";

  private final ContextModelRegistry modelRegistry;

  private final DocumentPersistenceRegistry persistRegistry;

  private final List<DepanFxProjectTree> projectList;

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
    this.projectList = new ArrayList<>();
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
  public void saveDocument(URI uri, Object document) throws IOException {
    DocumentXmlPersist persist = persistRegistry.getDocumentPersist(document);

    try (Writer saver = openForSave(uri)) {
      persist.save(saver, document);
    }
  }

  @Override
  public Object importDocument(URI uri) throws IOException {
    DocumentXmlPersist persist = persistRegistry.getDocumentPersist(uri);
    try (Reader importer = openForLoad(uri)) {
      Object document = persist.load(importer);
      registerDocument(document, uri);
      return document;
    }
  }

  @Override
  public Optional<DepanFxProjectDocument> asProjectDocument(URI uri) {
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
  public Optional<DepanFxWorkspaceResource> asWorkspaceResource(
      URI uri, Object resource) {
    return asProjectDocument(uri)
        .map(doc -> new WorkspaceResource(doc, resource));
  }

  private void registerDocument(Object document, URI uri) {
    documentRegistry.registerDocument(document, uri);
  }

  private FileReader openForLoad(URI uri) throws IOException {
    return new FileReader(new File(uri));
  }

  private FileWriter openForSave(URI uri) throws IOException {
    return new FileWriter(new File(uri));
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
