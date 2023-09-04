package com.pnambic.depanfx.workspace.basic;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pnambic.depanfx.graph.context.ContextModel;
import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.context.EmptyContextModel;
import com.pnambic.depanfx.persistence.DocumentXmlPersist;
import com.pnambic.depanfx.persistence.plugins.DocumentPersistenceRegistry;

/**
 * A common registry of workspace context.
 */
@Component
public class BasicDepanFxWorkspace implements DepanFxWorkspace {

  public static final String WORKSPACE_NAME = "Depan Workspace";

  private static final ContextModel<?, ?> EMPTY_CONTEXT_MODEL =
      new EmptyContextModel();

  private final List<ContextModel<?, ?>> contextModels;

  private final DocumentPersistenceRegistry persistRegistry;

  private final List<DepanFxProjectTree> projectList;

  private final String workspaceName;

  @Autowired
  public BasicDepanFxWorkspace(
      List<ContextModel<?, ?>> contextModels,
      DocumentPersistenceRegistry resourceModels) {
    this(WORKSPACE_NAME, contextModels, resourceModels);
  }

  public BasicDepanFxWorkspace(String workspaceName,
      List<ContextModel<?, ?>> contextModels,
      DocumentPersistenceRegistry persistRegistry) {
    this.projectList = new ArrayList<>();
    this.workspaceName = workspaceName;
    this.contextModels = contextModels;
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

  public ContextModel getContextModel(String modelKey) {
    Optional<ContextModel<?, ?>> result = contextModels.stream()
        .filter(cm -> cm.getId().getContextModelKey().equals(modelKey))
        .findFirst();
    return result.orElse(EMPTY_CONTEXT_MODEL);
  }

  public ContextModel getContextModel(ContextModelId modelId) {
    Optional<ContextModel<?, ?>> result = contextModels.stream()
        .filter(cm -> cm.getId().equals(modelId))
        .findFirst();
    return result.orElse(EMPTY_CONTEXT_MODEL);
  }

  @Override
  public void exit() {
  }

  @Override
  public void saveDocument(URI uri, Object document) throws IOException {
    DocumentXmlPersist persist = persistRegistry.getDocumentPersist(document);

    try (FileWriter saver = openForSave(uri)) {
      persist.save(saver, document);

    }
  }

  private FileReader openForLoad(URI uri) throws IOException {
    return new FileReader(new File(uri));
  }

  private FileWriter openForSave(URI uri) throws IOException {
    return new FileWriter(new File(uri));
  }
}
