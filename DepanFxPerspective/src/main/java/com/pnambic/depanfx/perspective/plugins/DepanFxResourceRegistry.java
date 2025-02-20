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
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxMemoryProject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class DepanFxResourceRegistry {

  private static Logger LOG =
      LoggerFactory.getLogger(DepanFxResourceRegistry.class);

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
     * Needed to keep from exposing the unknown type (E.G. @{code <?>}).
     */
    void openDocument(
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        DepanFxProjectDocument document);

    void openResource(
        DepanFxDialogRunner dialogRunner,
        DepanFxWorkspaceResource<?> resource);
  }

  public interface Panel {
    void openPanel(
        DepanFxSceneService sceneSrcv,
        DepanFxWorkspace workspace,
        DepanFxProjectDocument document);
  }

  public static abstract class Basic<T> implements Contribution {

    private final String resourceLabel;

    private final Class<T> dataType;

    private final String fileExt;

    private final String orderKey;

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
        DepanFxWorkspaceResource<T> wkspRsrc,
        DepanFxDialogRunner dialogRunner);

    @Override
    public boolean acceptsResource(DepanFxWorkspaceResource<?> resource) {
      return dataType.isAssignableFrom(resource.getResource().getClass());
    }

    @Override
    public void openDocument(
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        DepanFxProjectDocument document) {
      loadResource(workspace, document)
          .ifPresent(r -> runDialog(r, dialogRunner));
    }

    @Override
    public void openResource(
        DepanFxDialogRunner dialogRunner, DepanFxWorkspaceResource<?> rsrc) {
      @SuppressWarnings("unchecked")
      DepanFxWorkspaceResource<T> typedRsrc =
          (DepanFxWorkspaceResource<T>) rsrc;
      runDialog(typedRsrc, dialogRunner);
    }

    public Optional<DepanFxWorkspaceResource<T>> loadResource(
        DepanFxWorkspace workspace, DepanFxProjectDocument document) {
      try {
        return workspace.getWorkspaceResource(document, dataType);
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

  public static void dispatchContribution(
      Contribution contrib,
      DepanFxSceneService sceneSrvc,
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxProjectDocument document) {

    Optional<DepanFxWorkspaceResource<?>> optRsrc =
        getBuiltInResource(workspace, document);

    if (contrib instanceof Panel panel) {
      panel.openPanel(sceneSrvc, workspace, document);
      return;
    }
    optRsrc.ifPresentOrElse(
        r -> contrib.openResource(dialogRunner, r),
        () -> contrib.openDocument(workspace, dialogRunner, document));
  }

  private final Collection<Contribution> contribs;

  @Autowired
  public DepanFxResourceRegistry(Collection<Contribution> contribs) {
    this.contribs = contribs;
  }

  /**
   * Indicates if there a principal contribution for the the supplied document
   * which will be used for open operations.
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
  public Stream<Contribution> streamContributions(
      DepanFxWorkspace workspace,
      DepanFxProjectDocument document) {
    return selectContributions(contribs.stream(), workspace, document);
    }

  /**
   * Use the principal contribution to open the supplied document.
   */
  public void openDocument(
      DepanFxSceneService sceneSrvc,
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxProjectDocument document) {

    Optional<Contribution> optContrib =
        selectContributions(
            streamPrincipalContributions(), workspace, document)
            .findFirst();
    if (optContrib.isEmpty()) {
      return;
    }

    optContrib.ifPresent(c ->
        dispatchContribution(c, sceneSrvc, workspace, dialogRunner, document));
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
      if (workspace.getBuiltInProject() instanceof DepanFxMemoryProject projStore) {
        Optional<DepanFxWorkspaceResource<Object>> optResource =
            projStore.getResource(document.getMemberPath());
        if (optResource.isPresent()) {
          DepanFxWorkspaceResource<?> resource = optResource.get();
          return Optional.of(resource);
        }
      }
    }
    return Optional.empty();
  }
}
