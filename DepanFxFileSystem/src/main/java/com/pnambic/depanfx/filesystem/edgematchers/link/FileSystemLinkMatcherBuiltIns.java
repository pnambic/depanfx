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
package com.pnambic.depanfx.filesystem.edgematchers.link;

import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.filesystem.graph.FileSystemModelDefinition;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class FileSystemLinkMatcherBuiltIns {

  public static final String MEMBER_NAME = "Member";

  public static final String DIRECTORY_NAME = "Directory";

  public static final String FILE_NAME = "File";

  public static final Path FILE_SYSTEM_LINK_MATCHER_PATH =
      DepanFxLinkMatcherDocument.LINK_MATCHER_TOOL_PATH
      .resolve(FileSystemModelDefinition.MODEL.getId().getContextModelPath());

  public static final Path FILE_SYSTEM_MEMBER_MATCHER_PATH =
      FILE_SYSTEM_LINK_MATCHER_PATH.resolve(MEMBER_NAME);

  public static final Path FILE_SYSTEM_DIRECTORY_MATCHER_PATH =
      FILE_SYSTEM_LINK_MATCHER_PATH.resolve(DIRECTORY_NAME);

  public static final Path FILE_SYSTEM_FILE_MATCHER_PATH =
      FILE_SYSTEM_LINK_MATCHER_PATH.resolve(FILE_NAME);

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      memberMatcherFileSystem() {

    return createBuiltIn(MEMBER_NAME, FileSystemLinkMatchers.MEMBER_DOC,
        FILE_SYSTEM_MEMBER_MATCHER_PATH);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      directoryMatcher() {

    return createBuiltIn(DIRECTORY_NAME, FileSystemLinkMatchers.DIRECTORY_DOC,
        FILE_SYSTEM_DIRECTORY_MATCHER_PATH);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      fileMatcher() {

    return createBuiltIn(FILE_NAME, FileSystemLinkMatchers.FILE_DOC,
        FILE_SYSTEM_FILE_MATCHER_PATH);
  }

  private DepanFxBuiltInContribution<DepanFxLinkMatcherDocument> createBuiltIn(
      String docName, DepanFxLinkMatcherDocument doc, Path docPath) {
    return new DepanFxBuiltInContribution.Simple<>(docPath, doc);
  }
}
