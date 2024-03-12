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
package com.pnambic.depanfx.java.graph;

import com.pnambic.depanfx.filesystem.graph.FileSystemModelDefinition;
import com.pnambic.depanfx.graph.model.BasicGraphContextModel;
import com.pnambic.depanfx.graph.model.GraphContextModel;
import com.pnambic.depanfx.graph_doc.model.GraphContextDocument;
import com.pnambic.depanfx.graph_doc.model.GraphContextlModelDefinition;
import com.pnambic.depanfx.java.context.JavaContextDefinition;
import com.pnambic.depanfx.java.context.JavaContextModelId;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.Arrays;

@Configuration
public class JavaModelDefinition {

  public static final String JAVA_LABEL = "Java";

  public static final BasicGraphContextModel MODEL =
      new BasicGraphContextModel(
          JavaContextDefinition.MODEL_ID,
          Arrays.asList(FileSystemModelDefinition.FILE_SYSTEM_DEPENDENCY),
          Arrays.asList(JavaContextDefinition.NODE_KIND_IDS),
          Arrays.asList(JavaRelation.RELATIONS));

  public static final GraphContextDocument DOCUMENT =
      new GraphContextDocument(
          JAVA_LABEL,
          "Identifies the graph elements and components for"
              + " Java graph context.",
          MODEL);

  /**
   * Convenience for extensions built on Java models.
   */
  public static final GraphContextModel[] JAVA_DEPENDENCY = {
      GraphContextlModelDefinition.MODEL,
      FileSystemModelDefinition.MODEL,
      MODEL
  };

  /** Goes into BuiltIn project under key, not label. */
  public static final Path BUILTIN_PATH =
      GraphContextDocument.CONTEXT_MODEL_PATH.resolve(
          JavaContextModelId.JAVA_KEY);

  public static final DepanFxBuiltInContribution.Simple CONTRIBUTION =
      new DepanFxBuiltInContribution.Simple(BUILTIN_PATH, DOCUMENT);

  @Bean
  public DepanFxBuiltInContribution javaContextModel() {
    return CONTRIBUTION;
  }
}
