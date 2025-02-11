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
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxMemoryProject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

@Component
public class DepanFxResourceOpenRegistry {

  private static Logger LOG =
      LoggerFactory.getLogger(DepanFxResourceOpenRegistry.class);

  public interface Contribution {

    /**
     * Recognize a content by name.
     */
    boolean acceptsDocument(DepanFxProjectDocument document);

    /**
     * Recognize a content by type.
     */
    boolean acceptsResource(DepanFxWorkspaceResource<?> resource);

    void openDocument(
        DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner,
        DepanFxProjectDocument document);

    void openResource(
        DepanFxDialogRunner dialogRunner, DepanFxWorkspaceResource<?> resource);
  }

  public static abstract class Basic<T> implements Contribution {

    private final String fileExt;

    private final Class<T> dataType;

    public Basic(
        Class<T> dataType, String fileExt) {
      this.dataType = dataType;
      this.fileExt = fileExt;
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
    public void openDocument(DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner, DepanFxProjectDocument document) {
      Path resourcePath = document.getMemberPath();
      try {
        workspace.toProjectDocument(resourcePath.toUri())
            .flatMap(
                r -> workspace.getWorkspaceResource(r, dataType))
            .ifPresent(r -> runDialog(r, dialogRunner));
      } catch (RuntimeException errCaught) {
        LOG.error("Unable to open {}",
            resourcePath.toUri(), errCaught);
      }
    }

    @Override
    public void openResource(DepanFxDialogRunner dialogRunner,
        DepanFxWorkspaceResource<?> rsrc) {
      @SuppressWarnings("unchecked")
      DepanFxWorkspaceResource<T> typedRsrc =
          (DepanFxWorkspaceResource<T>) rsrc;
      runDialog(typedRsrc, dialogRunner);
    }
  }

  private final Collection<Contribution> contribs;

  @Autowired
  public DepanFxResourceOpenRegistry(Collection<Contribution> contribs) {
    this.contribs = contribs;
  }

  public void openDocument(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxProjectDocument document) {

    // For built ins, open as the resource.
    if (document.getProject().equals(workspace.getBuiltInProjectTree())) {
      if (workspace.getBuiltInProject() instanceof DepanFxMemoryProject projStore) {
        Optional<DepanFxWorkspaceResource<Object>> optResource =
            projStore.getResource(document.getMemberPath());
        if (optResource.isPresent()) {
          DepanFxWorkspaceResource<?> resource = optResource.get();
          contribs.stream()
              .filter(c -> c.acceptsResource(resource))
              .findFirst()
              .ifPresent(c -> c.openResource(dialogRunner, resource));
          return;
          // Anything else falls through as a document open
        }
      }
    }
    // Documents in other projects
    contribs.stream()
        .filter(c -> c.acceptsDocument(document))
        .findFirst()
        .ifPresent(c -> c.openDocument(workspace, dialogRunner, document));
  }
}
