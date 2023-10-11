package com.pnambic.depanfx.workspace;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.pnambic.depanfx.workspace.basic.BasicDepanFxProjectBadMember;
import com.pnambic.depanfx.workspace.basic.BasicDepanFxProjectContainer;
import com.pnambic.depanfx.workspace.basic.BasicDepanFxProjectDocument;
import com.pnambic.depanfx.workspace.basic.BasicDepanFxProjectTree;

public class DepanFxWorkspaceFactory {

  private static final DateTimeFormatter CONTAINER_TIMESTAMP_FORMATTER =
      DateTimeFormatter.ofPattern("yyyyMMdd");

  private static final DateTimeFormatter DOCUMENT_TIMESTAMP_FORMATTER =
      DateTimeFormatter.ofPattern("yyyyMMddHHmm");

  private DepanFxWorkspaceFactory() {
    // Prevent instantiation
  }

  public static DepanFxProjectTree createDepanFxProjectTree(
      String projectName, Path projectPath) {
    return new BasicDepanFxProjectTree(projectName, projectPath);
  }

  public static DepanFxProjectContainer createDepanFxProjectContainer(
      DepanFxProjectTree projectTree, Path projectPath) {
    return new BasicDepanFxProjectContainer(projectTree, projectPath);
  }

  public static DepanFxProjectDocument createDepanFxProjectDocument(
      DepanFxProjectTree projectTree, Path projectPath) {
    return new BasicDepanFxProjectDocument(projectTree, projectPath);
  }

  public static DepanFxProjectBadMember createDepanFxProjectBadMember(
      DepanFxProjectTree projectName, Path projectPath) {
    return new BasicDepanFxProjectBadMember(projectName, projectPath);
  }

  public static String buildContainerTimestampName(String prefix) {
    LocalDateTime now = LocalDateTime.now();
    StringBuilder result = new StringBuilder(prefix);
    CONTAINER_TIMESTAMP_FORMATTER.formatTo(now, result);
    return result.toString();
  }

  public static String buildDocumentTimestampName(String prefix, String ext) {
    LocalDateTime now = LocalDateTime.now();
    StringBuilder result = new StringBuilder(prefix);
    DOCUMENT_TIMESTAMP_FORMATTER.formatTo(now, result);
    result.append(".");
    result.append(ext);
    return result.toString();
  }
}
