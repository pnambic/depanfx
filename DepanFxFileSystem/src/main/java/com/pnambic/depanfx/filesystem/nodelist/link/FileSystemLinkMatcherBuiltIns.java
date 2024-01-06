package com.pnambic.depanfx.filesystem.nodelist.link;

import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class FileSystemLinkMatcherBuiltIns {

  public static final String FILE_SYSTEM_LINK_MATCHER_DIR = "File System";

  public static final Path FILE_SYSTEM_LINK_MATCHER_PATH =
      DepanFxLinkMatcherDocument.LINK_MATCHER_TOOL_PATH
          .resolve(FILE_SYSTEM_LINK_MATCHER_DIR);

  private static final String MEMBER_NAME = "Member";

  private static final String DIRECTORY_NAME = "Directory";

  private static final String FILE_NAME = "File";

  @Bean
  public DepanFxBuiltInContribution memberMatcherFileSystem() {
    return createBuiltIn(MEMBER_NAME, FileSystemLinkMatchers.MEMBER_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution directoryMatcher() {
    return createBuiltIn(DIRECTORY_NAME, FileSystemLinkMatchers.DIRECTORY_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution fileMatcher() {
    return createBuiltIn(FILE_NAME, FileSystemLinkMatchers.FILE_DOC);
  }

  private DepanFxBuiltInContribution createBuiltIn(
      String docName, DepanFxLinkMatcherDocument doc) {
    Path docPath = FILE_SYSTEM_LINK_MATCHER_PATH.resolve(docName);
    return new DepanFxBuiltInContribution.Simple(docPath, doc);
  }
}
