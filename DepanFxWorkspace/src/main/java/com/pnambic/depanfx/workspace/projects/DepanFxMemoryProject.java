/*
 * Copyright 2024 The Depan Project Authors
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
package com.pnambic.depanfx.workspace.projects;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxProjectSpi;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.basic.BasicDepanFxProjectTree;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Basic implementation for a project that is held in memory.
 */
public abstract class DepanFxMemoryProject implements DepanFxProjectSpi {

  private final String projectName;

  private final BasicDepanFxProjectTree projectTree;

  private final Map<Path, DepanFxProjectContainer> containers = new HashMap<>();

  private final Multimap<DepanFxProjectContainer, DepanFxProjectMember> members =
      MultimapBuilder.hashKeys().arrayListValues().build();

  public DepanFxMemoryProject(String projectName) {
    this.projectName = projectName;
    this.projectTree = new BasicDepanFxProjectTree(this);

    containers.put(getProjectPath(), getProjectTree());
  }

  @Override
  public String getProjectName() {
    return projectName;
  }

  @Override
  public boolean isDirectoryEmpty() throws IOException {
    return false;
  }

  @Override
  public void createContainer(DepanFxProjectContainer projDir) {
    installProjectContainer(projDir.getMemberPath());
  }

  @Override
  public Path getRelativePath(Path memberPath) {
    return memberPath;
  }

  @Override
  public Optional<Path> getMemberPath(Path memberPath) {
    if (!memberPath.isAbsolute()) {
      return Optional.of(memberPath);
    }
    return Optional.empty();
  }

  @Override
  public Stream<DepanFxProjectMember> getMembers(DepanFxProjectMember projDoc) {
    if (projDoc instanceof DepanFxProjectContainer) {
      return members.get((DepanFxProjectContainer) projDoc).stream();
    }
    return Collections.<DepanFxProjectMember>emptyList().stream();
  }

  public <T> Optional<DepanFxWorkspaceResource<T>> getResource(Path rsrcPath) {

    // Let types be inferred.
    Optional<DepanFxProjectDocument> rsrcProjDoc =
        getProjectTree().asProjectDocument(rsrcPath);
  
    return getResource(rsrcProjDoc.get());
  }

  public DepanFxProjectTree getProjectTree() {
    return projectTree;
  }

  public DepanFxProjectContainer installProjectContainer(Path projPath) {
    if (projPath == null) {
      return projectTree;
    }
    DepanFxProjectContainer projDir = containers.get(projPath);
    if (projDir != null) {
      return projDir;
    }
  
    DepanFxProjectContainer parentDir =
        installProjectContainer(projPath.getParent());
    DepanFxProjectContainer result =
        parentDir.getProject().asProjectContainer(projPath).get();
    containers.put(projPath, result);
    addMember(parentDir, result);
    return result;
  }

  protected abstract <T> Optional<DepanFxWorkspaceResource<T>> getResource(
      DepanFxProjectDocument depanFxProjectDocument);

  protected void addMember(
      DepanFxProjectContainer parentDir, DepanFxProjectMember member) {
    members.put(parentDir, member);
  }
}
