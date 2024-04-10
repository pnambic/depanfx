package com.pnambic.depanfx.filesystem.nodelist.link;

import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class FileSystemLinkMatcherBuiltIns {

  public static final String FILE_SYSTEM_LINK_MATCHER_DIR = "File System";

  public static final String MEMBER_NAME = "Member";

  public static final String DIRECTORY_NAME = "Directory";

  public static final String FILE_NAME = "File";

  public static final Path FILE_SYSTEM_LINK_MATCHER_PATH =
      DepanFxLinkMatcherDocument.LINK_MATCHER_TOOL_PATH
          .resolve(FILE_SYSTEM_LINK_MATCHER_DIR);

  public static final Path FILE_SYSTEM_MEMBER_MATCHER_PATH =
      FILE_SYSTEM_LINK_MATCHER_PATH.resolve(MEMBER_NAME);

  public static final Path FILE_SYSTEM_DIRECTORY_MATCHER_PATH =
      FILE_SYSTEM_LINK_MATCHER_PATH.resolve(DIRECTORY_NAME);

  public static final Path FILE_SYSTEM_FILE_MATCHER_PATH =
      FILE_SYSTEM_LINK_MATCHER_PATH.resolve(FILE_NAME);

  @Bean
  public DepanFxBuiltInContribution memberMatcherFileSystem() {
    return createBuiltIn(MEMBER_NAME, FileSystemLinkMatchers.MEMBER_DOC,
        FILE_SYSTEM_MEMBER_MATCHER_PATH);
  }

  @Bean
  public DepanFxBuiltInContribution directoryMatcher() {
    return createBuiltIn(DIRECTORY_NAME, FileSystemLinkMatchers.DIRECTORY_DOC,
        FILE_SYSTEM_DIRECTORY_MATCHER_PATH);
  }

  @Bean
  public DepanFxBuiltInContribution fileMatcher() {
    return createBuiltIn(FILE_NAME, FileSystemLinkMatchers.FILE_DOC,
        FILE_SYSTEM_FILE_MATCHER_PATH);
  }

  private DepanFxBuiltInContribution createBuiltIn(
      String docName, DepanFxLinkMatcherDocument doc, Path docPath) {
    return new DepanFxBuiltInContribution.Simple(docPath, doc);
  }
}
