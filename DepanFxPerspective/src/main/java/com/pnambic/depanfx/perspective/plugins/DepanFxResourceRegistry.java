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
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxMemoryProject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A registry of resources.
 */
@Component
public class DepanFxResourceRegistry {

  private static Logger LOG =
      LoggerFactory.getLogger(DepanFxResourceRegistry.class);

  /**
   * Resources that open to a panel may not open to a separate dialog window.
   */
  @SuppressWarnings("serial")
  public static class UseOpenPanelException
      extends UnsupportedOperationException {

    public UseOpenPanelException(
        DepanFxResourceRegistryContribution<?> contrib) {
      super("Use openPanel() for this contribution "
            + contrib.getClass().getName());
    }
  }

  private final Collection<DepanFxResourceRegistryContribution<?>> contribs;

  @Autowired
  public DepanFxResourceRegistry(
      Collection<DepanFxResourceRegistryContribution<?>> contribs) {
    this.contribs = contribs;
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

  public void openDocument(
      DepanFxWorkspace workspace,
      DepanFxSceneService sceneSrvc,
      DepanFxProjectDocument document) {

    Optional<DepanFxResourceRegistryContribution<?>> optContrib =
        getPrincipalContribution(workspace, document);

    if (optContrib.isEmpty()) {
      LOG.warn("No contribution to open document: {}", document);
      return;
    }

    // Bound scope of the type conversions
    @SuppressWarnings("rawtypes")
    DepanFxResourceRegistryContribution contrib = optContrib.get();

    @SuppressWarnings("unchecked")
    Optional<DepanFxWorkspaceResource<?>> optOpenRsrc =
        contrib.loadResource(workspace, document);

    optOpenRsrc.ifPresentOrElse(
        r -> DepanFxResourceRegistryContribution.dispatchResource(
            workspace, sceneSrvc, contrib, r),
        () -> LOG.warn("Unable to load document: {}", document));
  }

  public void openDialog(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxProjectDocument document) {

    Optional<DepanFxResourceRegistryContribution<?>> optContrib =
        getPrincipalContribution(workspace, document);

    if (optContrib.isEmpty()) {
      LOG.warn("No contribution to open document: {}", document);
      return;
    }

    // Bound scope of the type conversions
    @SuppressWarnings("rawtypes")
    DepanFxResourceRegistryContribution contrib = optContrib.get();
    if (!(contrib instanceof DepanFxResourceRegistryContribution.Dialog)) {
      LOG.warn("Principal contribution {} requires a panel: {}",
          contrib.getResourceLabel(), document);
      return;
    }

    @SuppressWarnings("unchecked")
    Optional<DepanFxWorkspaceResource<?>> optOpenRsrc =
        contrib.loadResource(workspace, document);

    optOpenRsrc.ifPresentOrElse(
        r -> DepanFxResourceRegistryContribution.dispatchDialog(
            workspace, dialogRunner, contrib, r),
        () -> LOG.warn("Unable to load document: {}", document));
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
