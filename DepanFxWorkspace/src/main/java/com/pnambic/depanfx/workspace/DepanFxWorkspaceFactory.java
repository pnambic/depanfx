package com.pnambic.depanfx.workspace;

import com.pnambic.depanfx.workspace.basic.BasicDepanFxProjectTree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Hides the use of the {@code com.panbmic.dpeanfx.workspace.basic} package
 * as the implementations.
 */
public class DepanFxWorkspaceFactory {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxWorkspaceFactory.class);

  private static final DateTimeFormatter CONTAINER_TIMESTAMP_FORMATTER =
      DateTimeFormatter.ofPattern("yyyyMMdd");

  private static final DateTimeFormatter DOCUMENT_TIMESTAMP_FORMATTER =
      DateTimeFormatter.ofPattern("yyyyMMddHHmm");

      private DepanFxWorkspaceFactory() {
    // Prevent instantiation
  }

  public static final char EXTENSION_DOT = '.';


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

  public static File bestDocumentFile(String baseName, String ext,
      DepanFxWorkspace workspace, Path targetPath, File fallbackFile) {

    String toolName =
        DepanFxWorkspaceFactory.buildDocumentTimestampName(baseName, ext);
    return bestDocumentFile(toolName, workspace, targetPath, fallbackFile);
  }

  public static File bestDocumentFile(String documentName,
      DepanFxWorkspace workspace, Path targetPath, File fallbackFile) {

    File buildCurrentToolDir =  workspace.getCurrentProject()
        .map(t -> t.getMemberPath())
        .map(p -> p.resolve(targetPath))
        .map(p -> p.toFile())
        .map(f -> DepanFxWorkspaceFactory.bestDirectory(f, fallbackFile))
        .get();
    return new File(buildCurrentToolDir, documentName);
  }

  /**
   * Documents that do not match the supplied {@code docType} are quietly
   * dropped.
   */
  public static Optional<DepanFxWorkspaceResource> loadDocument(
      DepanFxWorkspace workspace,
      DepanFxProjectDocument projDoc,
      Class<?> docType) {
    return loadDocument(workspace, projDoc, docType.getName())
        .filter(d -> expectType(docType, d));
  }

  public static Optional<DepanFxWorkspaceResource> loadDocument(
      DepanFxWorkspace workspace, DepanFxProjectDocument projDoc,
      String typeName) {

    try {
      return workspace.importDocument(projDoc);
    } catch (IOException errIo) {
      LOG.error("Unable to open {} at {}", typeName, projDoc, errIo);
      throw new RuntimeException(
          "Unable to open " + typeName + " at " + projDoc, errIo);
    }
  }

  public static Optional<String> getExtension(String filename) {
      int dotIndex = filename.lastIndexOf(EXTENSION_DOT);
      if (dotIndex > 0 && dotIndex < filename.length() - 1) {
          return Optional.of(filename.substring(dotIndex + 1));
      }
      return Optional.empty();
  }

  public static String buildDocTitle(DepanFxProjectDocument projDoc) {
    String fullName = projDoc.getMemberPath().getFileName().toString();
    return DepanFxWorkspaceFactory.getExtension(fullName)
        .map(e -> chopExtension(fullName, e.length() + 1))
        .orElse(fullName);
  }

  private static File bestDirectory(File baseFile, File fallbackFile) {
    File containerFile = baseFile.getParentFile();
    if (containerFile.isDirectory()) {
      return baseFile;
    }

    while (containerFile.compareTo(fallbackFile) > 0) {
      File testFile = containerFile.getParentFile();
      if (testFile.isDirectory()) {
        return new File(testFile, baseFile.getName());
      }
      containerFile = testFile;
    }
    return fallbackFile;
  }

  private static String chopExtension(String filename, int extSize) {
    int length = filename.length();
    return filename.substring(0, Integer.max(0, length - extSize));
  }

  private static boolean expectType(
      Class<?> expectedType, DepanFxWorkspaceResource wrkspRsrc) {
    Class<? extends Object> rsrcType = wrkspRsrc.getResource().getClass();
    if (expectedType.isAssignableFrom(rsrcType)) {
      return true;
    }
    LOG.warn("Expected type {}, but document is {}",
        expectedType.getName(), rsrcType.getName());
    return false;
  }
}
