package com.pnambic.depanfx.workspace.basic;

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

/**
 * A common registry of workspace context.
 */
@Component
public class BasicDepanFxWorkspace implements DepanFxWorkspace {

  public static final String WORKSPACE_NAME = "Depan Workspace";

  private static final ContextModel<?, ?> EMPTY_CONTEXT_MODEL =
      new EmptyContextModel();

  private final List<ContextModel<?, ?>> contextModels;

  private final List<DepanFxProjectTree> projectList;

  private final String workspaceName;

  @Autowired
  public BasicDepanFxWorkspace(List<ContextModel<?, ?>> contextModels) {
    this(WORKSPACE_NAME, contextModels);
  }

  public BasicDepanFxWorkspace(String workspaceName, List<ContextModel<?, ?>> contextModels) {
    this.projectList = new ArrayList<>();
    this.workspaceName = workspaceName;
    this.contextModels = contextModels;
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

  public ContextModel getContextModel(ContextModelId target) {
    Optional<ContextModel<?, ?>> result = contextModels.stream()
        .filter(cm -> cm.getId().equals(target))
        .findFirst();
    return result.orElse(EMPTY_CONTEXT_MODEL);
  }

  @Override
  public void exit() {
  }
}
