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

import com.pnambic.depanfx.filesystem.graph.DocumentNode;
import com.pnambic.depanfx.graph.model.GraphModel;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class GitLogLoader {

  public static final String DEFAULT_BRANCH_NAME = "HEAD";

  private static final String EXT = DepanFxNodeLists.EXT;

  private final String[] LOG_COMMAND = new String[] {
      "log", "--name-only", "--oneline", "-n"
  };

  private final GitLogParser parser = new GitLogParser();

  private final DepanFxProjectContainer dstDir;

  private final DepanFxWorkspaceResource graphRsrc;

  private final GitCommandRunner cmdRunner;

  // Unpacked for easy node lookup.
  private GraphModel graphModel;

  private String branchName;

  // First time through #processLog(), this value is null.
  private Collection<GraphNode> nodes;

  private String commitId;

  public GitLogLoader(DepanFxProjectContainer dstDir,
      DepanFxWorkspaceResource graphRsrc, GitCommandRunner cmdRunner) {
    this.dstDir = dstDir;
    this.graphRsrc = graphRsrc;
    this.cmdRunner = cmdRunner;
  }

  public void loadBranchCommits(String branchName, int logCount) {
    this.branchName = branchName != null ? branchName : DEFAULT_BRANCH_NAME;
    graphModel = ((GraphDocument) graphRsrc.getResource()).getGraph();

    runLogCommand(logCount)
        .forEach(this::processLog);

    // Write out any final node list.
    if (nodes != null) {
      saveNodeList();
    }
  }

  private List<String> runLogCommand(int logCount) {
    List<String> command = new ArrayList<>(LOG_COMMAND.length + 2);
    command.addAll(Arrays.asList(LOG_COMMAND));
    command.add(Integer.toString(logCount));
    command.add(branchName);
    return cmdRunner.runGitCommand(command);
  }

  private void processLog(String logLine) {
    parser.parseLine(logLine);
    if (parser.isNewCommit()) {
      if (nodes != null) {
        saveNodeList();
      }
      commitId = parser.getCommitId();
      nodes = new ArrayList<>();
    }
    if (parser.isFileName()) {
      DocumentNode baseNode = GitUtils.createDocument(parser.getFileName());
      DocumentNode addNode = GitUtils.mapGraphNode(baseNode, graphModel);
      nodes.add(addNode);
    }
  }

  private Optional<DepanFxWorkspaceResource> saveNodeList() {
    DepanFxWorkspace workspace = graphRsrc.getWorkspace();
    Optional<DepanFxProjectDocument> optProjDoc = buildProjectDocument();
    if (optProjDoc.isEmpty()) {
      throw new RuntimeException(
          "Unable to save commit " + commitId);
    }
    DepanFxProjectDocument projDoc = optProjDoc.get();

    DepanFxNodeList saveList =
        DepanFxNodeLists.buildNodeList(graphRsrc, nodes);
    try {
      return workspace.saveDocument(projDoc, saveList);
    } catch (Exception errAny) {
      throw new RuntimeException(
          "Unable to save nodelist " + projDoc.getMemberPath(), errAny);
    }
  }

  private Optional<DepanFxProjectDocument> buildProjectDocument() {
    String docName = DepanFxWorkspaceFactory.buildDocumentTimestampName(
        branchName + " " + commitId, EXT);
    Path destPath = dstDir.getMemberPath().resolve(Paths.get(docName));
    return graphRsrc.getDocument().getProject().asProjectDocument(destPath);
  }
}
