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
package com.pnambic.depanfx.bytecode;


import com.pnambic.depanfx.filesystem.builder.FileSystemDirectoryLoader;
import com.pnambic.depanfx.filesystem.graph.DocumentNode;
import com.pnambic.depanfx.graph_doc.builder.DepanFxGraphModelBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipFile;

/**
 * Build dependencies from all .class and Jar (or zip) files
 * in a file system tree.
 *
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class JavaTreeLoader extends FileSystemDirectoryLoader {

  private final ClassFileReader reader;

  /**
   * Construct an analyzer for a Java tree.
   *
   * @param prefixPath starting directory in file system
   * @param builder destination of discovered dependencies
   * @param reader analyzer for .class files
   */
  public JavaTreeLoader(
      String prefixPath,
      DepanFxGraphModelBuilder builder,
      ClassFileReader reader) {
    super(builder, prefixPath);
    this.reader = reader;
  }

  @Override
  protected DocumentNode visitFile(File treeFile) throws IOException {
    DocumentNode result = super.visitFile(treeFile);

    if (treeFile.getName().endsWith(".class")) {
      FileInputStream content = new FileInputStream(treeFile);
      reader.readClassFile(getBuilder(), result, content);
    }

    if (treeFile.getName().endsWith(".jar")
        || treeFile.getName().endsWith(".zip") ) {
      readZipFile(treeFile.getPath(), result);
    }

    return result;
  }

  /**
   * Build Java dependencies from a Jar file.
   *
   * @param classPath path to Jar file
   * @param builder destination of discovered dependencies
   * @param progress indicator for user interface
   * @throws IOException
   */
  private void readZipFile(String classPath, DocumentNode document)
      throws IOException {

    ZipFile zipFile = new ZipFile(classPath);
    JarFileLister jarReader =
        new JarFileLister(zipFile, getBuilder(), reader, document);
    jarReader.start();
  }
}
