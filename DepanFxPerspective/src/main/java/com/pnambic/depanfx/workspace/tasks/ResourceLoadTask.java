package com.pnambic.depanfx.workspace.tasks;

import com.pnambic.depanfx.perspective.plugins.DepanFxResourceRegistryContribution;
import com.pnambic.depanfx.tasks.DeferredTask;
import com.pnambic.depanfx.tasks.ProgressMonitor;
import com.pnambic.depanfx.tasks.TaskStatus;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicReference;

public class ResourceLoadTask
    implements DeferredTask<Optional<DepanFxWorkspaceResource<?>>> {

  private final DepanFxResourceRegistryContribution<?> contribution;

  private final DepanFxWorkspace workspace;

  private final DepanFxProjectDocument document;

  private final AtomicReference<TaskStatus> status =
      new AtomicReference<>(TaskStatus.READY);

  private final AtomicReference<Optional<DepanFxWorkspaceResource<?>>> result =
      new AtomicReference<>(Optional.empty());

  private final AtomicReference<Exception> failure = new AtomicReference<>();

  private volatile String message = "";

  public ResourceLoadTask(
      DepanFxResourceRegistryContribution<?> contribution,
      DepanFxWorkspace workspace,
      DepanFxProjectDocument document) {
    this.contribution = contribution;
    this.workspace = workspace;
    this.document = document;
  }

  @Override
  public String getTaskTitle() {
    return MessageFormat.format(
        "Load {0}", document.getMemberPath().getFileName());
  }

  @Override
  public int getTotalSteps() {
    return 1;
  }

  @Override
  public void start(ProgressMonitor monitor) throws Exception {
    if (!status.compareAndSet(TaskStatus.READY, TaskStatus.RUNNING)) {
      if (status.get() == TaskStatus.CANCELLED) {
        throw cancelled();
      }
    }

    String beginMessage = MessageFormat.format(
        "Loading {0}", document.getMemberPath());
    message = beginMessage;
    monitor.updateMessage(beginMessage);

    try {
      checkCancelled(monitor);

      Optional<DepanFxWorkspaceResource<?>> loaded =
          contribution.loadResource(workspace, document)
              .map(resource -> (DepanFxWorkspaceResource<?>) resource);

      checkCancelled(monitor);

      result.set(loaded);
      if (loaded.isPresent()) {
        String successMessage = MessageFormat.format(
            "Loaded {0}", document.getMemberPath());
        message = successMessage;
        monitor.advance(1, successMessage);
        monitor.complete();
        status.set(TaskStatus.COMPLETED);
      } else {
        String missingMessage = MessageFormat.format(
            "No resource produced for {0}", document.getMemberPath());
        message = missingMessage;
        monitor.advance(1, missingMessage);
        monitor.complete();
        status.set(TaskStatus.COMPLETED);
      }
    } catch (CancellationException cancel) {
      message = cancellationMessage();
      status.set(TaskStatus.CANCELLED);
      monitor.updateMessage(message);
      throw cancel;
    } catch (Exception error) {
      status.set(TaskStatus.FAILED);
      failure.set(error);
      String errorMessage = MessageFormat.format(
          "Failed to load {0}: {1}",
          document.getMemberPath(),
          error.getMessage());
      message = errorMessage;
      monitor.updateMessage(errorMessage);
      throw error;
    }
  }

  @Override
  public TaskStatus getStatus() {
    return status.get();
  }

  @Override
  public Optional<DepanFxWorkspaceResource<?>> getResult() throws Exception {
    Exception error = failure.get();
    if (error != null) {
      throw error;
    }
    if (status.get() == TaskStatus.CANCELLED) {
      throw cancelled();
    }
    return result.get();
  }

  @Override
  public void cancel() {
    TaskStatus current;
    do {
      current = status.get();
      if (current.isTerminal()) {
        return;
      }
    } while (!status.compareAndSet(current, TaskStatus.CANCELLED));
    message = cancellationMessage();
  }

  public String getMessage() {
    return message;
  }

  public DepanFxResourceRegistryContribution<?> getContribution() {
    return contribution;
  }

  public DepanFxWorkspace getWorkspace() {
    return workspace;
  }

  public DepanFxProjectDocument getDocument() {
    return document;
  }

  private void checkCancelled(ProgressMonitor monitor) {
    if (status.get() == TaskStatus.CANCELLED || monitor.isCancelled()) {
      throw cancelled();
    }
  }

  private CancellationException cancelled() {
    return new CancellationException(cancellationMessage());
  }

  private String cancellationMessage() {
    return MessageFormat.format(
        "Cancelled load for {0}", document.getMemberPath());
  }
}
