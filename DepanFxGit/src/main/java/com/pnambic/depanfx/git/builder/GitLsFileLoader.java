/*
 * Copyright 2023 The Depan Project Authors
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
package com.pnambic.depanfx.git.builder;

import com.pnambic.depanfx.filesystem.graph.DirectoryNode;
import com.pnambic.depanfx.filesystem.graph.DocumentNode;
import com.pnambic.depanfx.filesystem.graph.FileSystemRelation;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.builder.DepanFxGraphModelBuilder;

import java.io.File;
import java.nio.file.Path;

public class GitLsFileLoader {

  private final DepanFxGraphModelBuilder builder;

  private final GitCommandRunner cmdRunner;

  private DirectoryNode repoRoot;

  public GitLsFileLoader(
      DepanFxGraphModelBuilder builder, GitCommandRunner cmdRunner) {
    this.builder = builder;
    this.cmdRunner = cmdRunner;
  }

  public void analyzeRepo() {
    String rootPath = File.separatorChar + cmdRunner.getGitRepoName();
    DirectoryNode rootNode = createDirectory(new File(rootPath));
    repoRoot = (DirectoryNode) builder.mapNode(rootNode);

    cmdRunner.runGitCommand("ls-files")
        .forEach(this::addFile);
  }

  private void addFile(String fileName) {
    File baseFile = new File(fileName);
    DocumentNode baseDoc = createDocument(baseFile);
    GraphNode mappedDoc = builder.mapNode(baseDoc);

    DirectoryNode mappedDir = getDirectory(baseFile.getParentFile());
    GraphEdge edge = new GraphEdge(
        mappedDir, mappedDoc, FileSystemRelation.CONTAINS_FILE);
    builder.addEdge(edge);
  }

  /**
   * Provides a mapped directory node that can be used to create relationships
   * to children nodes.
   */
  private DirectoryNode getDirectory(File dirFile) {
    if (dirFile == null) {
      return repoRoot;
    }
    if (dirFile.getPath().isEmpty()) {
      return repoRoot;
    }
    DirectoryNode selfNode = createDirectory(dirFile);
    DirectoryNode mappedNode = (DirectoryNode) builder.mapNode(selfNode);
    if (mappedNode != selfNode) {
      return mappedNode;
    }
    // Otherwise, recurse until a known directory is located.
    DirectoryNode parentNode = getDirectory(dirFile.getParentFile());
    GraphEdge edge = new GraphEdge(
        parentNode, mappedNode, FileSystemRelation.CONTAINS_DIR);
    builder.addEdge(edge);

    return mappedNode;
  }

  private DirectoryNode createDirectory(File directory) {
    Path dirPath = getElementPath(directory);
    return new DirectoryNode(dirPath);
  }

  private DocumentNode createDocument(File file) {
    Path filePath = getElementPath(file);
    return new DocumentNode(filePath);
  }

  private Path getElementPath(File elementPath) {
    return Path.of(elementPath.toURI());
  }
}
