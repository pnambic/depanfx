/*
 * Copyright 2007 The Depan Project Authors
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
package com.pnambic.depanfx.bytecode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Enumeration;

import com.pnambic.depanfx.filesystem.graph.DirectoryNode;
import com.pnambic.depanfx.filesystem.graph.DocumentNode;
import com.pnambic.depanfx.filesystem.graph.FileSystemRelation;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.builder.DepanFxGraphModelBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Similar to {@code ClassTreeLoader}, except that it reads the files contained
 * in a jar or zip archive. (any kind of zipped file actually).
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class JarFileLister {

  private static final Logger LOG =
      LoggerFactory.getLogger(JarFileLister.class.getName());

  private final ZipFile zipFile;

  private final DepanFxGraphModelBuilder builder;

  private final ClassFileReader reader;

  /**
   * Create a new JarFileLister, to list files in file, and call callbacks of
   * listener.
   *
   * @param file {@link File} representing the jar archive to read
   * @throws ZipException if any exception occured while trying to open the zip
   *         file
   * @throws IOException for any other {@link IOException}s throws while
   *         opening the {@link ZipFile}
   */
  public JarFileLister(
      ZipFile zipFile,
      DepanFxGraphModelBuilder builder,
      ClassFileReader reader) {
    this.zipFile = zipFile;
    this.builder = builder;
    this.reader = reader;
  }

  /**
   * begin the search of class files in the classPath given to the constructor.
   */
  public void start() {
    parse();
  }

  /**
   * Give the number of files in the zip archive.
   * @return the number of files in the zip archive.
   */
  public int getFileNumber() {
    return zipFile.size();
  }

  protected DepanFxGraphModelBuilder getBuilder() {
    return builder;
  }

  /**
   * Read the Java archive, creating directory nodes, file nodes, and parsing
   * the .class contents that are present.
   */
  private void parse() {
    Enumeration<? extends ZipEntry> entries = zipFile.entries();
    while (entries.hasMoreElements()) {
      ZipEntry entry = entries.nextElement();
      String name = entry.getName();

      GraphNode entryNode = createEntryNode(entry);
      createEntryEdge(entry);

      // If it is a .class file, parse those contents.
      // TODO(leeca): re-add path filtering
      if (!entry.isDirectory() && name.endsWith(".class")) {
        try {
          InputStream inputStream = zipFile.getInputStream(entry);
          reader.readClassFile(
              getBuilder(), (DocumentNode) entryNode, inputStream);
        } catch (IOException e1) {
          LOG.error("Error while reading file {}.", name);
        }
      }
    }

    try {
      zipFile.close();
    } catch (IOException e) {
      LOG.warn(
          "Error when closing zip file {}.", zipFile.getName());
    }
  }

  private void createEntryEdge(ZipEntry entry) {
    GraphNode entryNode = createEntryNode(entry);
    String name = entry.getName();
    File parentFile = new File(name).getParentFile();

    if (null == parentFile) {
      builder.newNode(entryNode);
      return;
    }

    GraphNode parentNode =
        builder.mapNode(new DirectoryNode(parentFile.toPath()));

    if (entry.isDirectory()) {
      GraphEdge edge = new GraphEdge(
          parentNode, entryNode, FileSystemRelation.CONTAINS_DIR);
      builder.addEdge(edge);
      return;
    }

    GraphEdge edge = new GraphEdge(
        parentNode, entryNode, FileSystemRelation.CONTAINS_FILE);
    builder.addEdge(edge);

  }

  private GraphNode createEntryNode(ZipEntry entry) {
    Path entryPath = new File(entry.getName()).toPath();
    if (entry.isDirectory()) {
      return new DirectoryNode(entryPath);
    }
    return new DocumentNode(entryPath);
  }
}
