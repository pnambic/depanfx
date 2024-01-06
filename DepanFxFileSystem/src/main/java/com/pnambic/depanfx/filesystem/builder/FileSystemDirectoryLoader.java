/*
 * Copyright 2009, 2023 The Depan Project Authors
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
package com.pnambic.depanfx.filesystem.builder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pnambic.depanfx.filesystem.graph.DirectoryNode;
import com.pnambic.depanfx.filesystem.graph.DocumentNode;
import com.pnambic.depanfx.filesystem.graph.FileSystemRelation;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.builder.DepanFxGraphModelBuilder;

public class FileSystemDirectoryLoader {

  private static final Logger LOG =
      LoggerFactory.getLogger(FileSystemDirectoryLoader.class);

  private static final File[] EMPTY_FILES = new File[0];

  private final DepanFxGraphModelBuilder builder;

  private final String prefixPath;

  /**
   * @param builder
   */
  public FileSystemDirectoryLoader(
      DepanFxGraphModelBuilder builder, String prefixPath) {
    this.builder = builder;
    this.prefixPath = prefixPath;
  }

  public void analyzeTree(String treePath) throws IOException {
    beginAnalysis(treePath);
    processRoot(treePath);
    finishAnalysis(treePath);
  }

  protected DepanFxGraphModelBuilder getBuilder() {
    return builder;
  }

  /**
   * Indicates the beginning of a tree traversal.
   *
   * @param treePath directory tree being traversed
   */
  protected void beginAnalysis(String treePath) {
  }

  /**
   * Indicates the completion of a tree traversal.
   *
   * @param treePath directory tree that was traversed
   */
  protected void finishAnalysis(String treePath) {
  }

  /**
   * Provides a {@code FileElement} for a discovered file.
   *
   * @param treeFile path name to file within the analysis tree
   * @throws IOException
   */
  protected DocumentNode visitFile(File treeFile) throws IOException {
    DocumentNode result = createDocument(treeFile);
    return (DocumentNode) builder.mapNode(result);
  }

  /**
   * Provides a {@code DirectoryElement} for a discovered directory.
   *
   * @param treeFile path name to directory within the analysis tree
   * @throws IOException
   */
  protected DirectoryNode visitDirectory(File treeFile) throws IOException {
    DirectoryNode parentNode = createDirectory(treeFile);
    return (DirectoryNode) builder.mapNode(parentNode);
  }

  /**
   * Process the root of the tree specially, since none of the elements
   * have relations with containers.
   *
   * @param treePath
   * @throws IOException
   */
  private void processRoot(String treePath) throws IOException {
    File treeFile = new File(treePath);

    // If it is just a file, it's pretty uninteresting - one node
    if (treeFile.isFile()) {
      DocumentNode fileNode = visitFile(treeFile);
      getBuilder().newNode(fileNode);
      return;
    }

    // If it's a directory, traverse the full tree
    if (treeFile.isDirectory()) {
      DirectoryNode parentNode = createRootNode(treeFile);
      traverseTree(parentNode, treeFile);
      return;
    }

    // Hmmm .. something unexpected
    LOG.info("Unable to load tree from {}", treePath);
  }

  private void traverseTree(GraphNode rootNode, File rootFile) {

    // TODO(leeca):  Based on performance, maybe revise to sort into
    // lists of files and directories, and process each type in batches.
    for (File child : getChildMembers(rootFile)) {
      buildChild(rootNode, child);
    }
  }

  /**
   * Handle a single child for a node.  If it is a directory, the directory's
   * children are processed recursively.
   *
   * @param rootNode Node for the parent directory
   * @param child a child element of the parent directory
   */
  private void buildChild(GraphNode rootNode, File child) {
    try {
      if (child.isFile()) {
        GraphNode file = visitFile(child);
        GraphEdge edge = new GraphEdge(
            rootNode, file, FileSystemRelation.CONTAINS_FILE);
        getBuilder().addEdge(edge);
        return;
      }
      if (child.isDirectory()) {
        GraphNode dir = visitDirectory(child);
        GraphEdge edge = new GraphEdge(
            rootNode, dir, FileSystemRelation.CONTAINS_DIR);
        getBuilder().addEdge(edge);
        traverseTree(dir, child);
        return;
      }
      LOG.warn("Unknown file system object {}", child.getCanonicalPath());
    } catch (IOException e) {
      LOG.error("Unable to access tree entity {}", child.getPath());
    }
  }

  private DirectoryNode createRootNode(File rootFile) throws IOException {
    if (prefixPath.equals(rootFile.getCanonicalPath())) {
      String rootPath = File.separatorChar + rootFile.getName();
      DirectoryNode rootDir = createDirectory(new File(rootPath));

      return (DirectoryNode) builder.mapNode(rootDir);
    }

    return visitDirectory(rootFile);
  }

  private DirectoryNode createDirectory(File directory)
      throws IOException {
    Path dirPath = getElementPath(directory);
    return new DirectoryNode(dirPath);
  }

  private DocumentNode createDocument(File file) throws IOException {
    Path filePath = getElementPath(file);
    return new DocumentNode(filePath);
  }

  /**
   * Some Windows directories (synthethic, e.g. My Music) don't have real
   * entries.
   */
  private File[] getChildMembers(File rootFile) {

    File[] result = rootFile.listFiles();
    if (result != null) {
      return result;
    }
    return EMPTY_FILES;
  }

  /**
   * Tidy up the path for elements, mostly by removing the prefix path if
   * it is present.
   *
   * @param elementPath path to file system element
   * @return canonical name for element
   * @throws IOException
   */
  private Path getElementPath(File elementPath) throws IOException {
    String dirPath = elementPath.getCanonicalPath();
    if (dirPath.startsWith(prefixPath)) {
      return Path.of(dirPath.substring(prefixPath.length()));
    }
    return Path.of(elementPath.toURI());
  }
}
