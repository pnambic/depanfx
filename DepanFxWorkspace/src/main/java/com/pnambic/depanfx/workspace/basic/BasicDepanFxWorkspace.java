package com.pnambic.depanfx.workspace.basic;

import java.util.ArrayList;
import java.util.List;

import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

public class BasicDepanFxWorkspace implements DepanFxWorkspace {

  private final List<DepanFxProjectTree> projectList;

  private final String workspaceName;

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
