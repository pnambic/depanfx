/*
 * Copyright 2025 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pnambic.depanfx.perspective.plugins;

import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneService;
import com.pnambic.depanfx.tasks.TaskSubmission;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxMemoryProject;
import com.pnambic.depanfx.workspace.tasks.WorkspaceTaskService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javafx.application.Platform;

/**
 * A registry of resources.
 */
@Component
public class DepanFxResourceRegistry {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxResourceRegistry.class);

  private final Collection<DepanFxResourceRegistryContribution<?>> contribs;

  private final WorkspaceTaskService workspaceTaskService;

  @Autowired
  public DepanFxResourceRegistry(
      Collection<DepanFxResourceRegistryContribution<?>> contribs,
      WorkspaceTaskService workspaceTaskService) {
    this.contribs = contribs;
    this.workspaceTaskService = workspaceTaskService;
  }

  /**
   * Indicates if there a principal contribution for the the supplied document
   * which will be used for open document operations.
   */
  public boolean opensDocument(
      DepanFxWorkspace workspace,
      DepanFxProjectDocument document) {

    return selectContributions(
        streamPrincipalContributions(), workspace, document)
        .findAny()
        .isPresent();
  }

  /**
   * Indication if there are any contributions for the the supplied document,
   * which might be used for "open as" with the document.
   *
   * Includes the principal contribution as well as any additional
   * contributions.
   */
  public boolean acceptsDocument(
      DepanFxWorkspace workspace,
      DepanFxProjectDocument document) {

    return selectContributions(contribs.stream(), workspace, document)
        .findAny()
        .isPresent();
  }

  /**
   * Provide all contributions that claim to be able to open
   * the supplied document.
   */
  public Stream<DepanFxResourceRegistryContribution<?>> streamContributions(
      DepanFxWorkspace workspace,
      DepanFxProjectDocument document) {
    return selectContributions(contribs.stream(), workspace, document);
  }

  /**
   * Use the principal contribution to open the supplied document.
   * @return 
   */
  public Optional<DepanFxWorkspaceResource<?>> loadResource(
      DepanFxWorkspace workspace, DepanFxProjectDocument document) {

    return getPrincipalContribution(workspace, document)
        .flatMap(c -> c.loadResource(workspace, document));
  }

  public TaskSubmission<Optional<DepanFxWorkspaceResource<?>>> fetchResource(
      DepanFxWorkspace workspace,
      DepanFxProjectDocument document,
      DepanFxResourceRegistryContribution<?> c,
      Consumer<DepanFxWorkspaceResource<?>> onResourceLoad) {
    TaskSubmission<Optional<DepanFxWorkspaceResource<?>>> submission =
        workspaceTaskService.submitResourceLoad(workspace, document, c);

    submission.resultFuture().whenComplete((resource, error) -> {
      if (error != null) {
        if (error instanceof CancellationException) {
          return;
        }
        LOG.warn("Unable to load document: {}", document, error);
        return;
      }
      resource.ifPresentOrElse(
          r -> runOnFxThread(() -> onResourceLoad.accept(r)),
          () -> LOG.warn("Unable to load document: {}", document));
    });

    return submission;
  }

  public Optional<TaskSubmission<Optional<DepanFxWorkspaceResource<?>>>> openDocument(
      DepanFxWorkspace workspace,
      DepanFxSceneService sceneSrvc,
      DepanFxProjectDocument document) {

    Optional<DepanFxResourceRegistryContribution<?>> optContrib =
        getPrincipalContribution(workspace, document);

    if (optContrib.isEmpty()) {
      LOG.warn("No contribution to open document: {}", document);
      return Optional.empty();
    }

    DepanFxResourceRegistryContribution<?> contrib = optContrib.get();
    TaskSubmission<Optional<DepanFxWorkspaceResource<?>>> submission =
        fetchResource(
            workspace, document, contrib,
            r -> DepanFxResourceRegistryContribution.dispatchResource(
                workspace, sceneSrvc, contrib, r));
    return Optional.of(submission);
  }

  public Optional<TaskSubmission<Optional<DepanFxWorkspaceResource<?>>>> openDialog(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxProjectDocument document) {

    Optional<DepanFxResourceRegistryContribution<?>> optContrib =
        getPrincipalContribution(workspace, document);

    if (optContrib.isEmpty()) {
      LOG.warn("No contribution to open document: {}", document);
      return Optional.empty();
    }

    DepanFxResourceRegistryContribution<?> contrib = optContrib.get();
    if (!(contrib instanceof DepanFxResourceRegistryContribution.Dialog)) {
      LOG.warn("Principal contribution {} requires a panel."
          + "  Unable to render document {} with dialog.",
          contrib.getResourceLabel(), document);
      return Optional.empty();
    }

    TaskSubmission<Optional<DepanFxWorkspaceResource<?>>> submission =
        fetchResource(
            workspace, document, contrib,
            r -> DepanFxResourceRegistryContribution.dispatchDialog(
                workspace, dialogRunner, contrib, r));
    return Optional.of(submission);
  }

  private void runOnFxThread(Runnable action) {
    if (Platform.isFxApplicationThread()) {
      action.run();
    } else {
      Platform.runLater(action);
    }
  }

  private Optional<DepanFxResourceRegistryContribution<?>>
  getPrincipalContribution(
      DepanFxWorkspace workspace, DepanFxProjectDocument document) {
    return selectContributions(streamPrincipalContributions(), workspace, document)
        .findFirst();
  }

  private Stream<DepanFxResourceRegistryContribution<?>>
  streamPrincipalContributions() {
    return contribs.stream()
        .filter(c -> c instanceof DepanFxResourceRegistryContribution.Principal);
  }

  /**
   * Provide those contributions from the stream that claim to be able
   * to open the supplied document.
   */
  private Stream<DepanFxResourceRegistryContribution<?>> selectContributions(
      Stream<DepanFxResourceRegistryContribution<?>> stream,
      DepanFxWorkspace workspace,
      DepanFxProjectDocument document) {

    Optional<DepanFxWorkspaceResource<?>> optRsrc =
        getBuiltInResource(workspace, document);
    if (optRsrc.isPresent()) {
      DepanFxWorkspaceResource<?> rsrc = optRsrc.get();
      return stream
          .filter(c -> c.acceptsResource(rsrc));
    }

    // Documents in other projects
    return stream
        .filter(c -> c.acceptsDocument(document));
  }

  /**
   * Provide the supplied document's full resource, iff the resource is
   * in the Built In project.  Since the Built-In project is in-memory,
   * these resource retrievals are very low cost.
   */
  private static Optional<DepanFxWorkspaceResource<?>> getBuiltInResource(
      DepanFxWorkspace workspace,
      DepanFxProjectDocument document) {

    // For built ins, open as the resource.
    if (document.getProject().equals(workspace.getBuiltInProjectTree())) {
      if (workspace.getBuiltInProject()
            instanceof DepanFxMemoryProject projStore) {
        return projStore.getResource(document.getMemberPath())
            .map(r -> r); // Convert from <Object> to <?>
      }
    }
    return Optional.empty();
  }
}
