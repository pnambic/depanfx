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

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.pnambic.depanfx.bytecode.AsmFactory;
import com.pnambic.depanfx.bytecode.ClassAnalysisStats;
import com.pnambic.depanfx.bytecode.ClassFileReader;
import com.pnambic.depanfx.filesystem.graph.DocumentNode;
import com.pnambic.depanfx.graph.model.GraphModel;
import com.pnambic.depanfx.graph_doc.builder.SimpleGraphModelBuilder;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ClassFileReaderTest {

  @Test
  public void testHelloWorld() {
    String testRsrc = "HelloWorld.clazz";
    ClassAnalysisStats stats = new ClassAnalysisStats();
    ClassFileReader testReader =
        new ClassFileReader(AsmFactory.ASM9_FACTORY, stats);
    SimpleGraphModelBuilder builder = new SimpleGraphModelBuilder();
    DocumentNode testDoc = new DocumentNode(resolveName(testRsrc));

    InputStream testInput = getClass().getResourceAsStream(testRsrc);
    testReader.readClassFile(builder, testDoc, testInput);
    GraphModel graph = builder.createGraphModel();
    assertEquals(18, graph.getNodes().size());
  }

  private Path resolveName(String name) {
    String baseName = ClassFileReaderTest.class.getPackageName();
    List<String> result = new ArrayList<>();
    Splitter.on('.').splitToStream(baseName).forEach(result::add);
    result.add(name);
    return new File(Joiner.on('/').join(result)).toPath();
  }
}
