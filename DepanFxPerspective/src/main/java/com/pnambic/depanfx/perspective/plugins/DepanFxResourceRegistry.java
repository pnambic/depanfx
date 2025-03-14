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

import com.pnambic.depanfx.base.DepanFxOrderableContribution;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneService;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxMemoryProject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
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
   * Provides standard mechanisms for opening a UX on all documents.
   *
   * Through various options and related interfaces ({@link Panel}),
   * a contribution may arrange for documents or resources to open
   * into dialog windows or screen panels.
   */
  public interface Contribution extends DepanFxOrderableContribution {

    String getResourceLabel();

    /**
     * Recognize a content by name.
     */
    boolean acceptsDocument(DepanFxProjectDocument document);

    /**
     * Recognize a content by type.
     */
    boolean acceptsResource(DepanFxWorkspaceResource<?> resource);

    /**
     * Open the supplied document in a dialog window.
     *
     * Needed to keep from exposing the unknown type (e.g. @{code <?>}).
     * Type erasure loses track if an unbound result from {@code loadResource()}
     * is passed to this method.
     */
    void openDocument(
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        Map<?,?> loadContext,
        DepanFxProjectDocument document);

    /**
     * Open the supplied resource in a dialog window.
     */
    void openResource(
        DepanFxDialogRunner dialogRunner,
        Map<?,?> loadContext,
        DepanFxWorkspaceResource<?> resource);
  }

  /**
   * Indicates that the document should be opened as a panel for the
   * supplied scene.
   */
  public interface Panel {

    void openPanel(
        DepanFxWorkspace workspace,
        DepanFxSceneService sceneSrcv,
        DepanFxProjectDocument document);
  }

  public static abstract class Basic<T> implements Contribution {

    private final String resourceLabel;

    private final Class<T> dataType;

    private final String fileExt;

    private final String orderKey;

    /**
     * Use one of the public types,
     * such as {@link Principal} or {@link Additional}.
     */
    protected Basic(
        String resourceLabel,
        Class<T> dataType,
        String fileExt,
        String orderKey) {
      this.resourceLabel = resourceLabel;
      this.orderKey = orderKey;
      this.dataType = dataType;
      this.fileExt = fileExt;
    }

    @Override
    public String getResourceLabel() {
      return resourceLabel;
    }

    @Override
    public String getOrderKey() {
      return orderKey;
    }

    @Override
    public boolean acceptsDocument(DepanFxProjectDocument document) {

      return DepanFxWorkspaceFactory.getExtension(
          document.getMemberPath().getFileName().toString())
          .map(fileExt::equals)
          .orElse(false);
    }

    abstract protected void runDialog(
        DepanFxDialogRunner dialogRunner,
        Map<?,?> loadContext,
        DepanFxWorkspaceResource<T> wkspRsrc);

    @Override
    public boolean acceptsResource(DepanFxWorkspaceResource<?> resource) {
      return dataType.isAssignableFrom(resource.getResource().getClass());
    }

    @Override
    public void openDocument(
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        Map<?,?> loadContext,
        DepanFxProjectDocument document) {
      loadResource(workspace, loadContext, document)
          .ifPresent(r -> runDialog(dialogRunner, loadContext, r));
    }

    @Override
    public void openResource(
        DepanFxDialogRunner dialogRunner,
        Map<?,?> loadContext,
        DepanFxWorkspaceResource<?> rsrc) {
      @SuppressWarnings("unchecked")
      DepanFxWorkspaceResource<T> typedRsrc =
          (DepanFxWorkspaceResource<T>) rsrc;
      runDialog(dialogRunner, loadContext, typedRsrc);
    }

    public Optional<DepanFxWorkspaceResource<T>> loadResource(
        DepanFxWorkspace workspace,
        Map<?,?> loadContext,
        DepanFxProjectDocument document) {
      try {
        return workspace.getWorkspaceResource(document, dataType, loadContext);
      } catch (RuntimeException errCaught) {
        LOG.error("Unable to open {}",
            document.getMemberPath().toUri(), errCaught);
      }
      return Optional.empty();
    }
  }

  /**
   * Marker indicating the contribution adds to the basic type open.
   */
  public static abstract class Principal<T> extends Basic<T> {

    public Principal(
        String resourceLabel,
        Class<T> dataType,
        String fileExt,
        String orderKey) {
      super(resourceLabel, dataType, fileExt, orderKey);
    }
  }

  /**
   * Marker indicating the contribution adds to the basic type open.
   */
  public static abstract class Additional<T> extends Basic<T> {

    public Additional(
        String resourceLabel,
        Class<T> dataType,
        String fileExt,
        String orderKey) {
      super(resourceLabel, dataType, fileExt, orderKey);
    }
  }

  /**
   * Resources that open to a panel may not open to a separate dialog window.
   */
  @SuppressWarnings("serial")
  public static class UseOpenPanelException
      extends UnsupportedOperationException {

    public UseOpenPanelException(Contribution contrib) {
      super("Use openPanel() for this contribution "
            + contrib.getClass().getName());
    }
  }

  public static void dispatchContribution(
      Contribution contrib,
      DepanFxWorkspace workspace,
      DepanFxSceneService sceneSrvc,
      Map<?,?> loadContext,
      DepanFxProjectDocument document) {

    if (contrib instanceof Panel panel) {
      panel.openPanel(workspace, sceneSrvc, document);
      return;
    }

    Optional<DepanFxWorkspaceResource<?>> optRsrc =
        getBuiltInResource(workspace, document);
    DepanFxDialogRunner dialogRunner = sceneSrvc.getDialogRunner();
    optRsrc.ifPresentOrElse(
        r -> contrib.openResource(dialogRunner, loadContext, r),
        () -> contrib.openDocument(
            workspace, dialogRunner, loadContext, document));
  }

  public static void dispatchContribution(
      Contribution contrib,
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      Map<?,?> loadContext,
      DepanFxProjectDocument document) {

    Optional<DepanFxWorkspaceResource<?>> optRsrc =
        getBuiltInResource(workspace, document);

    optRsrc.ifPresentOrElse(
        r -> contrib.openResource(dialogRunner, loadContext, r),
        () -> contrib.openDocument(
            workspace, dialogRunner, loadContext, document));
  }

  private final Collection<Contribution> contribs;

  @Autowired
  public DepanFxResourceRegistry(Collection<Contribution> contribs) {
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
   * Indicates if there a principal contribution for the the supplied document
   * which will be used for open dialog operations.
   */
  public boolean opensDialog(
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
  public Stream<Contribution> streamContributions(
      DepanFxWorkspace workspace,
      DepanFxProjectDocument document) {
    return selectContributions(contribs.stream(), workspace, document);
    }

  /**
   * Use the principal contribution to open the supplied document.
   */
  public void openDocument(
      DepanFxWorkspace workspace,
      DepanFxSceneService sceneSrvc,
      Map<?,?> loadContext,
      DepanFxProjectDocument document) {

    selectContributions(
        streamPrincipalContributions(), workspace, document)
        .findFirst()
        .ifPresent(c -> dispatchContribution(
            c, workspace, sceneSrvc, loadContext, document));
  }

  /**
   * Use the principal contribution to open the supplied document.
   */
  public void openDialog(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      Map<?,?> loadContext,
      DepanFxProjectDocument document) {

    selectContributions(streamPrincipalContributions(), workspace, document)
        .filter(c -> !(c instanceof Panel))
        .findFirst()
        .ifPresent(c -> dispatchContribution(
            c, workspace, dialogRunner, loadContext, document));
  }

  private Stream<Contribution> streamPrincipalContributions() {
    return contribs.stream().filter(c -> c instanceof Principal);
  }

  /**
   * Provide those contributions from the stream that claim to be able
   * to open the supplied document.
   */
  private Stream<Contribution> selectContributions(
      Stream<Contribution> stream,
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
