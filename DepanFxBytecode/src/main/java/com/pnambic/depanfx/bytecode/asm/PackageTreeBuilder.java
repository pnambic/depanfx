/*
 * Copyright 2009 The Depan Project Authors
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
package com.pnambic.depanfx.bytecode.asm;

import com.pnambic.depanfx.filesystem.graph.DirectoryNode;
import com.pnambic.depanfx.filesystem.graph.FileSystemRelation;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph.model.GraphRelation;
import com.pnambic.depanfx.graph_doc.builder.DepanFxGraphModelBuilder;
import com.pnambic.depanfx.java.graph.JavaRelation;
import com.pnambic.depanfx.java.graph.PackageNode;

import java.io.File;
import java.nio.file.Path;

/**
 * Create a tree structure for packages and their corresponding file-system
 * directories.
 *
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class PackageTreeBuilder {

  private static final char WINDOWS_PATH_CHAR = '\\';

  private static final char JAVA_PATH_CHAR = '/';

  /** Dependency builder */
  private final DepanFxGraphModelBuilder builder;

  private PackageNode packageNode;

  private DirectoryNode packageDir;

  /**
   * @param builder
   */
  public PackageTreeBuilder(DepanFxGraphModelBuilder builder) {
    this.builder = builder;
  }

  /**
   * Install a package tree and its corresponding directory tree for
   * the package and tree defined by the argument.  For conventionally
   * structured packages, the @c{@code packageFile} will be a suffix of the
   * {@code treeFile}.  However, this is not required.  The {@code treeFile}
   * may be shorter then the {@code packageFile}, which lead to inferred
   * parent directories.  It may also contain directory names that do not
   * correspond to package names.  These "perverse" embeddings of packages
   * into trees can result in unexpected associations.
   *
   * @param packageFile full Java path for package
   * @param treeFile directory path that contains the package
   * @return {@code PackageElement} for {@code packageFile}
   */
  public PackageNode installPackageTree(
      File packageFile, File treeFile) {

    PackageNode baseNode = createPackage(packageFile);
    installPkgDirNodes(baseNode, treeFile.toPath());
    PackageNode result = packageNode;

    // If the package is named, create all of its parents.
    if (packageFile != null) {
      createPackageParents(packageFile, treeFile);
    }
    return result;
  }

  /**
   * Ascend both the package tree and directory tree, creating any dependencies
   * that are required.  If any package already exists, assume the rest of the
   * tree is complete and stop.
   *
   * @param packageFile path to a package
   * @param treeFile path to a directory
   */

  private void createPackageParents(File packageFile, File treeFile) {
    TreeClimber treePath = new TreeClimber(treeFile);

    // If the lookup is not the same node, then a node with that identity
    // already exists.  No need to add that package (or its parents) again.
    for(;;) {

      PackageNode childNode = packageNode;
      DirectoryNode childDir = packageDir;

      // Set up for parents of current entities
      packageFile = packageFile.getParentFile();
      treePath.ascendTree();

      // Never parent a named packaged with the unnamed package
      if (null == packageFile) {
        return;
      }

      // If the parent package already exists,
      // then this is the last set of links to the children.
      PackageNode baseNode = createPackage(packageFile);
      GraphNode foundNode = builder.findNode(baseNode.getId());

      // Update nodes (packageNode, packageDir)
      // to the new parent package and directory.
      installPkgDirNodes(baseNode, treePath.getTreePath());

      // Create the children links.
      addEdge(packageNode, childNode, JavaRelation.PACKAGE);
      addEdge(packageDir, childDir, FileSystemRelation.CONTAINS_DIR);

      // If the parent package already existed, we are done.
      if (foundNode != null) {
        return;
      };
    }
  }

  /**
   * Map the package, create the directory, and the container dependency
   * between them.
   *
   * @param packageNode preliminary node for package
   * @param dirPath path to directory
   */
  private void installPkgDirNodes(PackageNode pkgBase, Path dirPath) {
    packageNode = (PackageNode) builder.mapNode(pkgBase);

    packageDir = (DirectoryNode) builder.mapNode(new DirectoryNode(dirPath));
    addEdge(packageDir, packageNode, JavaRelation.PACKAGEDIR);
  }

  private PackageNode createPackage(File packageFile) {
    if (packageFile != null) {
      return new PackageNode(getPackageName(packageFile));
    }
    return new PackageNode("<unnamed>");
  }

  private String getPackageName(File packageFile) {
    String result = packageFile.getPath();
    if (File.separatorChar == WINDOWS_PATH_CHAR) {
      return result.replace(WINDOWS_PATH_CHAR, JAVA_PATH_CHAR);
    }
    return result;
  }

  private void addEdge(
      GraphNode head, GraphNode tail, GraphRelation relation) {
    builder.addEdge(
        new GraphEdge(builder.mapNode(head), builder.mapNode(tail), relation));
  }
}
