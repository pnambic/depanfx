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

import com.pnambic.depanfx.edgematchers.link.DepanFxLinkMatcherGroup;
import com.pnambic.depanfx.edgematchers.link.DepanFxLinkMatchers.Composite;
import com.pnambic.depanfx.edgematchers.link.DepanFxLinkMatchers.ForwardRelation;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcher;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.filesystem.context.FileSystemContextDefinition;
import com.pnambic.depanfx.filesystem.graph.FileSystemModelDefinition;
import com.pnambic.depanfx.filesystem.graph.FileSystemRelation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileSystemLinkMatchers {

  private FileSystemLinkMatchers() {
    // Prevent instantiation.
  }

  public static final ForwardRelation DIRECTORY_FORWARD =
      new ForwardRelation(FileSystemRelation.CONTAINS_DIR);

  public static final ForwardRelation FILE_FORWARD =
      new ForwardRelation(FileSystemRelation.CONTAINS_FILE);

  public static final ForwardRelation LINK_FORWARD =
      new ForwardRelation(FileSystemRelation.SYMBOLIC_LINK);

  private static final List<DepanFxLinkMatcher> FILE_SYSTEM_MEMBERS =
      Arrays.asList(new DepanFxLinkMatcher [] {
          DIRECTORY_FORWARD, FILE_FORWARD, LINK_FORWARD
      });

  public static final Composite MEMBER = new Composite(FILE_SYSTEM_MEMBERS);

  public static final DepanFxLinkMatcherDocument MEMBER_DOC =
      new DepanFxLinkMatcherDocument(
          "File System Relationship", "File system relationship.",
          FileSystemModelDefinition.MODEL.getId(),
          DepanFxLinkMatcherGroup.MEMBER_MATCHER_GROUP, MEMBER);

  public static final DepanFxLinkMatcherDocument DIRECTORY_DOC =
      new DepanFxLinkMatcherDocument(
          "Directory Matcher", "Matches directories.",
          FileSystemModelDefinition.MODEL.getId(),
          Collections.emptyList(), DIRECTORY_FORWARD);

  public static final DepanFxLinkMatcherDocument FILE_DOC =
      new DepanFxLinkMatcherDocument(
          "File Matcher", "Matches files.",
          FileSystemModelDefinition.MODEL.getId(),
          Collections.emptyList(), FILE_FORWARD);
}
