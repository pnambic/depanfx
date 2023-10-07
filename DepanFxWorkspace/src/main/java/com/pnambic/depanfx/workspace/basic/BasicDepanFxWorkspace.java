package com.pnambic.depanfx.workspace.basic;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pnambic.depanfx.graph.context.plugins.ContextModelRegistry;
import com.pnambic.depanfx.persistence.DocumentXmlPersist;
import com.pnambic.depanfx.persistence.plugins.DocumentPersistenceRegistry;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

/**
 * A common registry of workspace context.
 */
@Component
public class BasicDepanFxWorkspace implements DepanFxWorkspace {

  public static final String WORKSPACE_NAME = "Depan Workspace";

  private final ContextModelRegistry modelRegistry;

  private final DocumentPersistenceRegistry persistRegistry;

  private final List<DepanFxProjectTree> projectList;

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
  public void importDocument(URI uri) throws IOException {
    DocumentXmlPersist persist = persistRegistry.getDocumentPersist(uri);
    try (Reader importer = openForLoad(uri)) {
      persist.load(importer);
    }
  }

  private FileReader openForLoad(URI uri) throws IOException {
    return new FileReader(new File(uri));
  }

  private FileWriter openForSave(URI uri) throws IOException {
    return new FileWriter(new File(uri));
  }
}
