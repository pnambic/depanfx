package com.pnambic.depanfx.workspace.tasks;

import com.pnambic.depanfx.perspective.plugins.DepanFxResourceRegistryContribution;
import com.pnambic.depanfx.tasks.TaskExecutorService;
import com.pnambic.depanfx.tasks.TaskSubmission;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WorkspaceTaskService {

  private final TaskExecutorService taskExecutorService;

  public WorkspaceTaskService(TaskExecutorService taskExecutorService) {
    this.taskExecutorService = taskExecutorService;
  }

  public TaskSubmission<Optional<DepanFxWorkspaceResource<?>>> submitResourceLoad(
      DepanFxWorkspace workspace,
      DepanFxProjectDocument document,
      DepanFxResourceRegistryContribution<?> contribution) {

    ResourceLoadTask loadTask =
        new ResourceLoadTask(contribution, workspace, document);
    return taskExecutorService.submitTask(loadTask);
  }
}
