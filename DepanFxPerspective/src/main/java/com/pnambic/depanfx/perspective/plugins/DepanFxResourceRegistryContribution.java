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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Provides standard mechanisms for opening a UX on all documents.
 *
 * Through various options and related interfaces ({@link DepanFxResourceRegistry.Panel}),
 * a contribution may arrange for documents or resources to open
 * into dialog windows or screen panels.
 */
public interface DepanFxResourceRegistryContribution<T> extends DepanFxOrderableContribution {

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
   * Load the supplied document as a workspace resource.
   */
  Optional<DepanFxWorkspaceResource<T>> loadResource(
      DepanFxWorkspace workspace, DepanFxProjectDocument document);

  /**
   * Opens the resource in a separate dialog window.
   */
  public static interface Dialog<T> {

    void runDialog(
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        DepanFxWorkspaceResource<T> dialogRsrc);
  }

  /**
   * Opens the resource as a panel for the supplied scene.
   */
  public static interface Panel<T> {

    void openPanel(
        DepanFxWorkspace workspace,
        DepanFxSceneService sceneSrcv,
        DepanFxWorkspaceResource<T> panelRsrc);
  }

  @SuppressWarnings("unchecked")
  public static void dispatchResource(
      DepanFxWorkspace workspace,
      DepanFxSceneService sceneSrvc,
      DepanFxResourceRegistryContribution<?> contrib,
      DepanFxWorkspaceResource<?> dispatchRsrc) {

    if (contrib instanceof Panel panel) {
      panel.openPanel(workspace, sceneSrvc, dispatchRsrc);
      return;
    }

    if (contrib instanceof Dialog dialog) {
      dialog.runDialog(workspace, sceneSrvc.getDialogRunner(), dispatchRsrc);
      return;
    }

    LoggerFactory.getLogger(DepanFxResourceRegistryContribution.class)
        .warn("Unable to open document: {}", dispatchRsrc.getDocument());
  }

  @SuppressWarnings("unchecked")
  public static void dispatchDialog(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxResourceRegistryContribution<?> contrib,
      DepanFxWorkspaceResource<?> dispatchRsrc) {

    if (contrib instanceof Dialog dialog) {
      dialog.runDialog(workspace, dialogRunner, dispatchRsrc);
      return;
    }

    LoggerFactory.getLogger(DepanFxResourceRegistryContribution.class)
        .warn("Unable to open document: {}", dispatchRsrc.getDocument());
  }

  /**
   * Core implementation of a resource contribution's behavior.
   *
   * Concrete contributions should use on the public marker subclasses,
   * such as {@link DepanFxResourceRegistryContribution.Principal} or
   * {@link DepanFxResourceRegistryContribution.Additional}.
   */
  public static class Basic<T>
      implements DepanFxResourceRegistryContribution<T> {

    private static Logger LOG = LoggerFactory.getLogger(Basic.class);

    private final String resourceLabel;

    private final Class<T> dataType;

    private final String fileExt;

    private final String orderKey;

    /**
     * Use one of the public types,
     * such as {@link DepanFxResourceRegistryContribution.Principal} or
     * {@link DepanFxResourceRegistryContribution.Additional}.
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

    @Override
    public boolean acceptsResource(DepanFxWorkspaceResource<?> resource) {
      return dataType.isAssignableFrom(resource.getResource().getClass());
    }

    @Override
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
   * Marker indicating the contribution should be preferred on opens.
   * Used to handle click-to-open operations.
   */
  public class Principal<T> extends Basic<T> {
    public Principal(
        String resourceLabel,
        Class<T> dataType,
        String fileExt,
        String orderKey) {
      super(resourceLabel, dataType, fileExt, orderKey);
    }
  }

  /**
   * Marker indicating the contribution can be used to open a resource.
   */
  public class Additional<T> extends Basic<T> {
  
    public Additional(
        String resourceLabel,
        Class<T> dataType,
        String fileExt,
        String orderKey) {
      super(resourceLabel, dataType, fileExt, orderKey);
    }
  }
}
