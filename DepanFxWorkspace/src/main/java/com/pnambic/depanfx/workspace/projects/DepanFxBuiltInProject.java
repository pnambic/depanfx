package com.pnambic.depanfx.workspace.projects;

import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Path;
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
public class DepanFxBuiltInProject extends DepanFxMemoryProject {

  /**
   * This value is blank so
   * {@link com.pnambic.depanfx.workspace.projects.DepanFxProjects.DepanFxProjects.TOOLS_PATH}
   * and its derived values work in both the FileSystem and BuildIn projects.
   */
  private static final String BUILT_IN_ROOT_PATH = "";

  public static final String BUILT_IN_LABEL = "Built In";

  public static final Path BUILT_IN_PROJECT_PATH = buildProjectPath();

  private final DepanFxBuiltInRegistry builtIns;

  private final Map<DepanFxProjectDocument, DepanFxBuiltInContribution<?>> contribs =
      new HashMap<>();

  public DepanFxBuiltInProject(DepanFxBuiltInRegistry builtIns) {
    this(BUILT_IN_LABEL, builtIns);
  }

  public DepanFxBuiltInProject(
      String projectName, DepanFxBuiltInRegistry builtIns) {
    super(projectName);
    this.builtIns = builtIns;
    installBuiltIns();
  }

  @Override
  public Path getProjectPath() {
    return BUILT_IN_PROJECT_PATH;
  }

  @Override
  public void checkProjectForNew() throws IOException {
    // Never try to set up the built in project as a new workspace
    throw new DirectoryNotEmptyException(BUILT_IN_LABEL);
  }

  @Override
  public <T> Optional<DepanFxWorkspaceResource<T>> getResource(
      DepanFxProjectDocument projDoc) {
    @SuppressWarnings("unchecked")
    DepanFxBuiltInContribution<T> contrib =
        (DepanFxBuiltInContribution<T>) contribs.get(projDoc);
    if (contrib != null) {
      return Optional.of(
          DepanFxWorkspaceResource.forSource(projDoc, contrib.getDocument()));
    }
    return Optional.empty();
  }

  @SuppressWarnings("unchecked")
  public <T> Stream<DepanFxBuiltInContribution<T>> getContributions(
      Class<T> targetType) {
    return builtIns.getContribs()
        .filter(c -> c.getDocument() != null)
        .filter(c -> targetType.isAssignableFrom(c.getDocument().getClass()))
        .map(c -> (DepanFxBuiltInContribution<T>) c);
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

  public void installContribDoc(
      DepanFxProjectContainer parentDir,
      DepanFxProjectDocument contribDoc,
      DepanFxBuiltInContribution<?> contrib) {
    contribs.put(contribDoc, contrib);
    addMember(parentDir, contribDoc);
  }

  private void installBuiltIns() {
    builtIns.installBuiltIns(this);
  }

  private static Path buildProjectPath() {
    return new File(BUILT_IN_ROOT_PATH).toPath();
  }
}
