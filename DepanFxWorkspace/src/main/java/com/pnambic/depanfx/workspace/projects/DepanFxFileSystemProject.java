package com.pnambic.depanfx.workspace.projects;

import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxProjectSpi;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Perform Project storage operations on folder in a file system.
 *
 * Container and document operations are performed with file system
 * directories and files.
 */
public class DepanFxFileSystemProject implements DepanFxProjectSpi {

  private static LinkOption[] IS_DIRECTORY_LINK_OPTIONS = new LinkOption[0];

  private final String projectName;

  private final Path projectPath;

  public DepanFxFileSystemProject(String projectName, Path projectPath) {
    this.projectName = projectName;
    this.projectPath = projectPath;
  }

  @Override
  public String getProjectName() {
    return projectName;
  }

  @Override
  public Path getProjectPath() {
    return projectPath;
  }

  @Override
  public void checkProjectForNew() throws IOException {
    if (!Files.isDirectory(projectPath, IS_DIRECTORY_LINK_OPTIONS)) {
      throw new NotDirectoryException(projectPath.toString());
    }
    if (!isDirectoryEmpty()) {
      throw new DirectoryNotEmptyException(projectPath.toString());
    }
  }

  @Override
  public boolean isDirectoryEmpty() throws IOException {
    try (DirectoryStream<Path> directory =
        Files.newDirectoryStream(projectPath)) {
      return !directory.iterator().hasNext();
    }
  }

  @Override
  public void createContainer(DepanFxProjectContainer projDir) {
    Path dirPath = projDir.getMemberPath();
    dirPath.toFile().mkdirs();
  }

  @Override
  public Path getRelativePath(Path memberPath) {
    if (memberPath.isAbsolute()) {
      return projectPath.relativize(memberPath);
    }
    return memberPath;
  }

  @Override
  public Optional<Path> getMemberPath(Path memberPath) {
    if (!memberPath.isAbsolute()) {
      return Optional.of(projectPath.resolve(memberPath));
    }
    if (memberPath.startsWith(projectPath)) {
      return Optional.of(memberPath);
    }
    return Optional.empty();
  }

  @Override
  public void deleteContainer(DepanFxProjectContainer projDir) {
    Path dirPath = projDir.getMemberPath();
    dirPath.toFile().delete();
  }

  @Override
  public void deleteDocument(DepanFxProjectDocument projDoc) {
    Path docPath = projDoc.getMemberPath();
    docPath.toFile().delete();
  }

  public Stream<DepanFxProjectMember> getMembers(DepanFxProjectMember projDoc) {
    if (projDoc instanceof DepanFxProjectContainer) {
      return scanDirectory((DepanFxProjectContainer) projDoc);
    }

    return Collections.<DepanFxProjectMember>emptyList().stream();
  }

  private Stream<DepanFxProjectMember> scanDirectory(
      DepanFxProjectContainer parentDoc) {
    DepanFxProjectTree project = parentDoc.getProject();
    try {
      return Files.list(parentDoc.getMemberPath())
          .map(p -> createProjectMember(project, p));
    } catch (IOException errIo) {
      throw new RuntimeException(
          "Unable to build children for " + parentDoc.getMemberName(),
          errIo);
    }
  }

  private DepanFxProjectMember createProjectMember(
      DepanFxProjectTree project, Path memberPath) {
    if (Files.isDirectory(memberPath)) {
      return project.asProjectContainer(memberPath).get();
    }
    if (Files.isRegularFile(memberPath)) {
      return project.asProjectDocument(memberPath).get();
    }
    return project.asBadMember(memberPath);
  }
}
