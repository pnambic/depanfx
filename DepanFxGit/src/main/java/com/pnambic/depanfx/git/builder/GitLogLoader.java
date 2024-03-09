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

import com.google.common.base.Strings;
import com.pnambic.depanfx.filesystem.graph.DocumentNode;
import com.pnambic.depanfx.graph.model.GraphModel;
import com.pnambic.depanfx.graph.model.GraphModels;
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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class GitLogLoader {

  public static final String DEFAULT_BRANCH_NAME = "HEAD";

  private static final String EXT = DepanFxNodeList.NODE_LIST_EXT;

  private final String[] LOG_COMMAND = new String[] {
      "log", "--name-only", "--oneline", "-n"
  };

  private final GitLogParser parser = new GitLogParser();

  private final DepanFxWorkspace workspace;

  private final DepanFxProjectContainer dstDir;

  private final DepanFxWorkspaceResource graphRsrc;

  private final GitCommandRunner cmdRunner;

  // Unpacked for easy node lookup.
  private GraphModel graphModel;

  // First time through #processLog(), this value is null.
  private Collection<GraphNode> nodes;

  private String commitId;

  private NodeListContext loaderContext;

  public GitLogLoader(
      DepanFxWorkspace workspace, DepanFxProjectContainer dstDir,
      DepanFxWorkspaceResource graphRsrc, GitCommandRunner cmdRunner) {
    this.workspace = workspace;
    this.dstDir = dstDir;
    this.graphRsrc = graphRsrc;
    this.cmdRunner = cmdRunner;
  }

  public void loadBranchCommits(String branchName, int logCount) {
    String loadBranchName =
        Strings.isNullOrEmpty(branchName) ? DEFAULT_BRANCH_NAME : branchName;
    graphModel = ((GraphDocument) graphRsrc.getResource()).getGraph();
    loaderContext =
        new NodeListContext(graphRsrc, dstDir, loadBranchName, logCount);

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
    command.add(loaderContext.getBranchName());
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
      DocumentNode addNode = GraphModels.mapGraphNode(baseNode, graphModel);
      nodes.add(addNode);
    }
  }

  private Optional<DepanFxWorkspaceResource> saveNodeList() {
    Optional<DepanFxProjectDocument> optProjDoc =
        loaderContext.buildProjectDocument(commitId);
    if (optProjDoc.isEmpty()) {
      throw new RuntimeException(
          "Unable to save commit " + commitId);
    }

    DepanFxNodeList saveList = loaderContext.buildNodeList(commitId, nodes);
    loaderContext.bump();

    DepanFxProjectDocument projDoc = optProjDoc.get();
    try {
      return workspace.saveDocument(projDoc, saveList);
    } catch (Exception errAny) {
      throw new RuntimeException(
          "Unable to save nodelist " + projDoc.getMemberPath(), errAny);
    }
  }

  private static class NodeListContext {

    private final DepanFxWorkspaceResource graphRsrc;

    private final DepanFxProjectContainer dstDir;

    private final String branchName;

    private final int digits;

    private int count = 0;

    public NodeListContext(
        DepanFxWorkspaceResource graphRsrc,
        DepanFxProjectContainer dstDir,
        String branchName, int logMax) {
      this.graphRsrc = graphRsrc;
      this.dstDir = dstDir;
      this.branchName = branchName;
      this.digits = 1 + (int) Math.floor(Math.log10(logMax - 1));
    }

    public String getBranchName() {
      return branchName;
    }

    private Optional<DepanFxProjectDocument> buildProjectDocument(
        String commitId) {
      String baseName = getDocBaseName(commitId);
      String docName =
          DepanFxWorkspaceFactory.buildDocumentTimestampName(baseName, EXT);
      Path destPath = dstDir.getMemberPath().resolve(Paths.get(docName));
      return graphRsrc.getDocument().getProject().asProjectDocument(destPath);
    }

    public DepanFxNodeList buildNodeList(
        String commitId, Collection<GraphNode> nodes) {

      String baseName = getDocBaseName(commitId);
      String listDescr = MessageFormat.format("Node list from {0}", baseName);
      return  DepanFxNodeLists.buildNodeList(
          baseName, listDescr, graphRsrc, nodes);
    }

    public void bump() {
      count += 1;
    }

    public String getDocBaseName(String commitId) {

      return MessageFormat.format("{0}-{1} {2}",
          branchName, getDigits(), commitId);
    }

    private String getDigits() {
      return Strings.padStart(String.valueOf(count), digits, '0');
    }
  }
}
