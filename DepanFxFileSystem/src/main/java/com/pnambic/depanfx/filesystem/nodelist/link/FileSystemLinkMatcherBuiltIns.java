package com.pnambic.depanfx.filesystem.nodelist.link;

import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.file.Path;

@Configuration
public class FileSystemLinkMatcherBuiltIns {

  public static final String BASE_PATH_NAME =
      DepanFxLinkMatcherDocument.BUILT_IN_LINK_MATCHER_PATH
      + "/File System";

  private static final String MEMBER_NAME = "Member";

  private static final String DIRECTORY_NAME = "Directory";

  private static final String FILE_NAME = "File";

  @Bean
  public DepanFxBuiltInContribution memberMatcher() {
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
    Path docPath = new File(BASE_PATH_NAME).toPath().resolve(docName);
    return new DepanFxBuiltInContribution.Simple(docPath, doc);
  }
}
