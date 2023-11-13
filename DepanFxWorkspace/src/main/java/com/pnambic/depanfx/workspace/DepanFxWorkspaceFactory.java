package com.pnambic.depanfx.workspace;

import com.pnambic.depanfx.workspace.basic.BasicDepanFxProjectTree;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Hides the use of the {@code com.panbmic.dpeanfx.workspace.basic} package
 * as the implementations.
 */
public class DepanFxWorkspaceFactory {

  private static final DateTimeFormatter CONTAINER_TIMESTAMP_FORMATTER =
      DateTimeFormatter.ofPattern("yyyyMMdd");

  private static final DateTimeFormatter DOCUMENT_TIMESTAMP_FORMATTER =
      DateTimeFormatter.ofPattern("yyyyMMddHHmm");

      private DepanFxWorkspaceFactory() {
    // Prevent instantiation
  }

  public static DepanFxProjectTree createDepanFxProjectTree(
      DepanFxProjectSpi projSpi) {
    return new BasicDepanFxProjectTree(projSpi);
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
    result.append(" ");
    DOCUMENT_TIMESTAMP_FORMATTER.formatTo(now, result);
    result.append(".");
    result.append(ext);
    return result.toString();
  }
}
