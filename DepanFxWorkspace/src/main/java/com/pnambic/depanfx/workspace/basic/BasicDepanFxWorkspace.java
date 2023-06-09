package com.pnambic.depanfx.workspace.basic;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

@Component
public class BasicDepanFxWorkspace implements DepanFxWorkspace {

  public static final String WORKSPACE_NAME = "Depan Workspace";

  private final List<DepanFxProjectTree> projectList;

  private final String workspaceName;

  @Autowired
  public BasicDepanFxWorkspace() {
    this(WORKSPACE_NAME);
  }

  public BasicDepanFxWorkspace(String workspaceName) {
    this.projectList = new ArrayList<>();
    this.workspaceName = workspaceName;

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
}
