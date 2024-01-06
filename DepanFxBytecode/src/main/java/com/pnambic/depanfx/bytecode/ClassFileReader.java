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
import java.io.IOException;
import java.io.InputStream;

import com.pnambic.depanfx.filesystem.graph.DocumentNode;
import com.pnambic.depanfx.graph_doc.builder.DepanFxGraphModelBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.asm.ClassReader;
import org.springframework.asm.ClassVisitor;

/**
 * Connect an ASM {@code ClassVisitor} to the bytecode stream.
 *
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class ClassFileReader {

  private static final Logger LOG =
      LoggerFactory.getLogger(ClassFileReader.class.getName());

  private final AsmFactory asmFactory;

  private final ClassAnalysisStats analysisStats;

  /**
   * Construct a reader, using a common stats counter.
   *
   * @param packageBuilder
   */
  public ClassFileReader(
      AsmFactory asmFactory, ClassAnalysisStats analysisStats) {
    this.asmFactory = asmFactory;
    this.analysisStats = analysisStats;
  }

  /**
   * Process a single class file from either the file system or a jar.
   *
   * @param builder destination of discovered dependencies
   * @param fileNode file node associated with the contents
   * @param content input stream from .class file.
   */
  public void readClassFile(
      DepanFxGraphModelBuilder builder,
      DocumentNode docNode,
      InputStream content) {

    ClassVisitor cd = asmFactory.buildClassVisitor(builder, docNode);
    try {
      ClassReader cr = new ClassReader(content);
      cr.accept(cd, 0);
      analysisStats.incrClassesLoaded();
      return;
    } catch (IOException ioErr) {
      LOG.warn(
          "Unable to load class information for {}", docNode.getId());
    } catch (ArrayIndexOutOfBoundsException bndException) {
      LOG.info("Unreadable class file for {}", docNode.getId());
    }

    analysisStats.incrClassesFailed();
  }
}
