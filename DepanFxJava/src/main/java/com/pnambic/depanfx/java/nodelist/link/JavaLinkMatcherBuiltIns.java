package com.pnambic.depanfx.java.nodelist.link;

import com.pnambic.depanfx.filesystem.nodelist.link.FileSystemLinkMatchers;
import com.pnambic.depanfx.java.context.JavaContextDefinition;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcher;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherGroup;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatchers.Composite;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Configuration
public class JavaLinkMatcherBuiltIns {

  public static final String JAVA_LINK_MATCHER_DIR = "Java";

  public static final Path JAVA_LINK_MATCHER_PATH =
      DepanFxLinkMatcherDocument.LINK_MATCHER_TOOL_PATH
          .resolve(JAVA_LINK_MATCHER_DIR);

  private static final String MEMBER_NAME = "Member";

  private static final List<DepanFxLinkMatcher> FILE_AND_JAVA_MEMBERS =
      Arrays.asList(new DepanFxLinkMatcher [] {
          FileSystemLinkMatchers.MEMBER,
          // Link file system nodes to Java classes nodes
          JavaLinkMatchers.CLASSFILE_FORWARD,
          JavaLinkMatchers.JAVA_CLASS_MEMBER_MATCH
      });

  private static final Composite MEMBER = new Composite(FILE_AND_JAVA_MEMBERS);

  public static final DepanFxLinkMatcherDocument MEMBER_DOC =
      new DepanFxLinkMatcherDocument(
          "Java Relationship", "Java relationship.",
          JavaContextDefinition.MODEL_ID,
          DepanFxLinkMatcherGroup.MEMBER_MATCHER_GROUP, MEMBER);


  @Bean
  public DepanFxBuiltInContribution memberMatcherJava() {
    return createBuiltIn(MEMBER_NAME, MEMBER_DOC);
  }

  private DepanFxBuiltInContribution createBuiltIn(
      String docName, DepanFxLinkMatcherDocument doc) {
    Path docPath = JAVA_LINK_MATCHER_PATH.resolve(docName);
    return new DepanFxBuiltInContribution.Simple(docPath, doc);
  }
}
