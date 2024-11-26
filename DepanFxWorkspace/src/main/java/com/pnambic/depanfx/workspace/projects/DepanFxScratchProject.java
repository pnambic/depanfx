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

import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.basic.BasicDepanFxProjectDocument;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * The scratch project provides in memory workspace resoruces.  It it primarily
 * intended for holding new created assets before they are saved.
 *
 * Unlike the built in project, the scratch project allows new resource
 * creation during a session.
 */
public class DepanFxScratchProject extends DepanFxMemoryProject {

  private final static char[] SCRATCH_CHARACTERS =
      ("0123456789"
      + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
      + "abcdefghijklmnopqrstuvwxyz").toCharArray();

  private static final int SOURCE_LENGTH = SCRATCH_CHARACTERS.length;

  private static final String SCRATCH_ROOT_PATH = "";

  public static final String SCRATCH_LABEL = "Scratch";

  public static final Path SCRATCH_PROJECT_PATH = buildProjectPath();

  // A new one with each scratch project.
  private final Random scratchSource = new Random();

  private Map<DepanFxProjectDocument, Object> documents =
      new HashMap<>();

  public DepanFxScratchProject() {
    super(SCRATCH_LABEL);
  }

  @Override
  public Path getProjectPath() {
    return SCRATCH_PROJECT_PATH;
  }

  @Override
  public void checkProjectForNew() throws IOException {
    // Always successful.
  }

  @Override
  public boolean isDirectoryEmpty() throws IOException {
    return true;
  }

  @Override
  public void deleteContainer(DepanFxProjectContainer projDir) {
    throw new RuntimeException(
        "Scratch container " + projDir.getMemberPath().toString()
        + " cannot be deleted.");
  }

  @Override
  public void deleteDocument(DepanFxProjectDocument projDoc) {
    throw new RuntimeException(
        "Scratch document " + projDoc.getMemberPath().toString()
        + " cannot be deleted.");
  }

  @Override
  public <T> Optional<DepanFxWorkspaceResource<T>> getResource(
      DepanFxProjectDocument projDoc) {
    @SuppressWarnings("unchecked")
    T resource = (T) documents.get(projDoc);
    if (resource != null) {
      return Optional.of(
          new DepanFxWorkspaceResource.StaticWorkspaceResource<T>(
              projDoc, resource));
    }
    return Optional.empty();
  }

  public DepanFxProjectDocument createScratchDocument() {
    Path scratchPath = Path.of(createScratchName());
    return new BasicDepanFxProjectDocument(getProjectTree(), scratchPath);
  }

  public <T> void installScratchResource(T resource) {
    installScratchResource(getProjectTree(), createScratchDocument(), resource);
  }

  public <T> DepanFxWorkspaceResource<T> installScratchResource(
      DepanFxProjectContainer parentDir,
      DepanFxProjectDocument scratchDoc,
      T scratchResource) {
    documents.put(scratchDoc, scratchResource);
    addMember(parentDir, scratchDoc);

    DepanFxWorkspaceResource<T> result =
        new DepanFxWorkspaceResource.StaticWorkspaceResource<T>(
            scratchDoc, scratchResource);
    return result ;
  }

  private static Path buildProjectPath() {
    return new File(SCRATCH_ROOT_PATH).toPath();
  }

  private String createScratchName() {
    StringBuilder result = new StringBuilder();
    for (int counter = 32; counter > 0; counter--) {
      result.append(
          SCRATCH_CHARACTERS[scratchSource.nextInt(SOURCE_LENGTH)]);
    }
    return result.toString();
  }
}
