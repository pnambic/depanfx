package com.pnambic.depanfx.workspace.projects;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxProjectSpi;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.basic.BasicDepanFxProjectTree;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Perform Project storage operations on folder in a file system.
 *
 * Container and document operations are performed with file system
 * directories and files.
 */
public class DepanFxBuiltInProject implements DepanFxProjectSpi {

  /**
   * This value is blank so
   * {@link com.pnambic.depanfx.workspace.projects.DepanFxProjects.DepanFxProjects.TOOLS_PATH}
   * and its derived values work in both the FileSystem and BuildIn projects.
   */
  private static final String BUILT_IN_ROOT_PATH = "";

  public static final String BUILT_IN_LABEL = "Built In";

  public static final Path BUILT_IN_PROJECT_PATH = buildProjectPath();

  private final String projectName;

  private final DepanFxBuiltInRegistry builtIns;

  private Map<Path, DepanFxProjectContainer> containers =
      new HashMap<>();

  private Map<DepanFxProjectDocument, DepanFxBuiltInContribution> documents =
      new HashMap<>();

  private Multimap<DepanFxProjectContainer, DepanFxProjectMember> members =
      MultimapBuilder.hashKeys().arrayListValues().build();

  private BasicDepanFxProjectTree projectTree;

  public DepanFxBuiltInProject(DepanFxBuiltInRegistry builtIns) {
    this(BUILT_IN_LABEL, builtIns);
  }

  public DepanFxBuiltInProject(
      String projectName, DepanFxBuiltInRegistry builtIns) {
    this.projectName = projectName;
    this.builtIns = builtIns;
    installBuiltIns();
  }

  @Override
  public String getProjectName() {
    return projectName;
  }

  @Override
  public Path getProjectPath() {
    return BUILT_IN_PROJECT_PATH;
  }

  @Override
  public void checkProjectForNew() throws IOException {
    if (!isDirectoryEmpty()) {
      throw new DirectoryNotEmptyException(BUILT_IN_LABEL);
    }
  }

  @Override
  public boolean isDirectoryEmpty() throws IOException {
    return false;
  }

  @Override
  public void createContainer(DepanFxProjectContainer projDir) {
    installProjectContainer(projDir.getMemberPath());
  }

  @Override
  public Path getRelativePath(Path memberPath) {
    return memberPath;
  }

  @Override
  public Optional<Path> getMemberPath(Path memberPath) {
    if (!memberPath.isAbsolute()) {
      return Optional.of(memberPath);
    }
    return Optional.empty();
  }

  @Override
  public void deleteContainer(DepanFxProjectContainer projDir) {
    throw new RuntimeException(
        "Built in container " + projDir.getMemberPath().toString()
        + " cannot be deleted.");
  }

  @Override
  public void deleteDocument(DepanFxProjectDocument projDoc) {
    throw new RuntimeException(
        "Built in document " + projDoc.getMemberPath().toString()
        + " cannot be deleted.");
  }

  @Override
  public Stream<DepanFxProjectMember> getMembers(
      DepanFxProjectMember projDoc) {

    if (projDoc instanceof DepanFxProjectContainer) {
      return members.get((DepanFxProjectContainer) projDoc).stream();
    }
    return Collections.<DepanFxProjectMember>emptyList().stream();
  }

  public Stream<DepanFxBuiltInContribution> getContributions(
      Class<?> targetType) {
    return builtIns.getContribs()
        .filter(c -> targetType.isAssignableFrom(c.getDocument().getClass()));
  }

  public Optional<DepanFxWorkspaceResource> getResource(
      DepanFxProjectDocument projDoc) {
    return Optional.ofNullable(documents.get(projDoc))
        .map(c -> c.getDocument())
        .map(d -> new DepanFxWorkspaceResource.StaticWorkspaceResource(projDoc, d));
  }

  private void installBuiltIns() {
    projectTree = new BasicDepanFxProjectTree(this);
    containers.put(getProjectPath(), projectTree);

    builtIns.installBuiltIns(this);
  }

  private static Path buildProjectPath() {
    return new File(BUILT_IN_ROOT_PATH).toPath();
  }

  public DepanFxProjectTree getProjectTree() {
    return projectTree;
  }

  public DepanFxProjectContainer installProjectContainer(Path projPath) {
    if (projPath == null) {
      return projectTree;
    }
    DepanFxProjectContainer projDir = containers.get(projPath);
    if (projDir != null) {
      return projDir;
    }

    DepanFxProjectContainer parentDir =
        installProjectContainer(projPath.getParent());
    DepanFxProjectContainer result =
        parentDir.getProject().asProjectContainer(projPath).get();
    containers.put(projPath, result);
    members.put(parentDir, result);
    return result;
  }

  public void installContribDoc(
      DepanFxProjectContainer parentDir,
      DepanFxProjectDocument contribDoc,
      DepanFxBuiltInContribution contrib) {
    documents.put(contribDoc, contrib);
    members.put(parentDir, contribDoc);
  }
}
