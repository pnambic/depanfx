package com.pnambic.depanfx.java.edgematchers.link;

import com.pnambic.depanfx.edgematchers.link.DepanFxLinkMatcherGroup;
import com.pnambic.depanfx.edgematchers.link.DepanFxLinkMatchers.Composite;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcher;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.filesystem.edgematchers.link.FileSystemLinkMatchers;
import com.pnambic.depanfx.java.graph.JavaModelDefinition;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Configuration
public class JavaLinkMatcherBuiltIns {

  public static final Path JAVA_LINK_MATCHER_PATH =
      DepanFxLinkMatcherDocument.LINK_MATCHER_TOOL_PATH
          .resolve(JavaModelDefinition.MODEL.getId().getContextModelPath());

  private static final String MEMBER_NAME = "Member";

  private static final List<DepanFxLinkMatcher> FILE_AND_JAVA_MEMBERS =
      Arrays.asList(new DepanFxLinkMatcher [] {
          FileSystemLinkMatchers.MEMBER,
          // Link file system nodes to Java classes nodes
          // JavaLinkMatchers.CLASSFILE_FORWARD,
          // JavaLinkMatchers.PACKAGE_FORWARD,

          // Link nested packages with Java class
          JavaLinkMatchers.JAVA_CLASS_MEMBER_MATCH,
          JavaLinkMatchers.JAVA_PACKAGE_MEMBER_MATCH
      });

  private static final Composite MEMBER = new Composite(FILE_AND_JAVA_MEMBERS);

  /**
   * Provide the relationships for a normal Java membership hierarchy.
   */
  public static final DepanFxLinkMatcherDocument MEMBER_DOC =
      new DepanFxLinkMatcherDocument(
          "Java Membership Hierachy", "Java hierarchy relationships.",
          JavaModelDefinition.MODEL.getId(),
          DepanFxLinkMatcherGroup.MEMBER_MATCHER_GROUP, MEMBER);

  /////////////////////////////////////
  // Paths for dependent resources to use for resource lookup

  public static final Path CLASS_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.CLASS_FORWARD_DOC);

  public static final Path EXTENDS_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.EXTENDS_FORWARD_DOC);

  public static final Path IMPLEMENTS_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.IMPLEMENTS_FORWARD_DOC);

  public static final Path TYPE_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.TYPE_FORWARD_DOC);

  public static final Path STATIC_FIELD_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.STATIC_FIELD_FORWARD_DOC);

  public static final Path MEMBER_FIELD_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.MEMBER_FIELD_FORWARD_DOC);

  public static final Path STATIC_METHOD_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.STATIC_METHOD_FORWARD_DOC);

  public static final Path MEMBER_METHOD_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.MEMBER_METHOD_FORWARD_DOC);

  public static final Path CALL_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.CALL_FORWARD_DOC);

  public static final Path READ_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.READ_FORWARD_DOC);

  public static final Path WRITE_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.WRITE_FORWARD_DOC);

  public static final Path INNER_TYPE_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.INNER_TYPE_FORWARD_DOC);

  public static final Path ANONYMOUS_TYPE_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.ANONYMOUS_TYPE_DOC);

  public static final Path CLASSFILE_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.CLASSFILE_FORWARD_DOC);

  public static final Path ERROR_HANDLING_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.ERROR_HANDLING_FORWARD_DOC);

  public static final Path PACKAGE_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.PACKAGE_FORWARD_DOC);

  public static final Path PACKAGEDIR_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.PACKAGEDIR_FORWARD_DOC);

  public static final Path RUNTIME_ANNOTATION_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.RUNTIME_ANNOTATION_FORWARD_DOC);

  public static final Path COMPILE_ANNOTATION_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.COMPILE_ANNOTATION_FORWARD_DOC);

  public static final Path MODULE_EXPORTED_TO_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.MODULE_EXPORTED_TO_FORWARD_DOC);

  public static final Path MODULE_EXPORTS_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.MODULE_EXPORTS_FORWARD_DOC);

  public static final Path MODULE_MAIN_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.MODULE_MAIN_FORWARD_DOC);

  public static final Path MODULE_PACKAGE_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.MODULE_PACKAGE_FORWARD_DOC);

  public static final Path MODULE_PROVIDES_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.MODULE_PROVIDES_FORWARD_DOC);

  public static final Path MODULE_OPENED_TO_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.MODULE_OPENED_TO_FORWARD_DOC);

  public static final Path MODULE_OPENS_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.MODULE_OPENS_FORWARD_DOC);

  public static final Path MODULE_REQUIRES_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.MODULE_REQUIRES_FORWARD_DOC);

  public static final Path MODULE_USES_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.MODULE_USES_FORWARD_DOC);

  public static final Path JAVA_CLASS_DERIVED_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.JAVA_CLASS_DERIVED_DOC);

  public static final Path JAVA_CLASS_MEMBER_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.JAVA_CLASS_MEMBER_DOC);

  public static final Path JAVA_PACKAGE_MEMBER_PATH =
      buildMatcherPath(JavaLinkMatchers.JAVA_PACKAGE_MEMBER_DOC);

  public static final Path JAVA_TREE_MEMBER_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.JAVA_TREE_MEMBER_DOC);

  public static final Path JAVA_USE_MATCHER_PATH =
      buildMatcherPath(JavaLinkMatchers.JAVA_USE_DOC);

  /**
   * Membership matcher for hierarchical tree building.
   */
  public static final Path JAVA_MEMBER_MATCHER_PATH =
      JAVA_LINK_MATCHER_PATH.resolve(MEMBER_NAME);

  /////////////////////////////////////
  // Matcher document Java hierarchies

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      hierarhcyMatcherJava() {
    return createBuiltIn(JAVA_MEMBER_MATCHER_PATH, MEMBER_DOC);
  }

  /////////////////////////////////////
  // Matcher documents for all Java relations

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      classMatcherJava() {
    return createBuiltIn(
        CLASS_MATCHER_PATH, JavaLinkMatchers.CLASS_FORWARD_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      extendsMatcherJava() {
    return createBuiltIn(
        EXTENDS_MATCHER_PATH, JavaLinkMatchers.EXTENDS_FORWARD_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      implementsMatcherJava() {
    return createBuiltIn(
        IMPLEMENTS_MATCHER_PATH, JavaLinkMatchers.IMPLEMENTS_FORWARD_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      typeMatcherJava() {
    return createBuiltIn(
        TYPE_MATCHER_PATH, JavaLinkMatchers.TYPE_FORWARD_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      staticFieldMatcherJava() {
    return createBuiltIn(
        STATIC_FIELD_MATCHER_PATH, JavaLinkMatchers.STATIC_FIELD_FORWARD_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      memberFieldMatcherJava() {
    return createBuiltIn(
        MEMBER_FIELD_MATCHER_PATH, JavaLinkMatchers.MEMBER_FIELD_FORWARD_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      staticMethodMatcherJava() {
    return createBuiltIn(
        STATIC_METHOD_MATCHER_PATH, JavaLinkMatchers.STATIC_METHOD_FORWARD_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      memberMethodMatcherJava() {
    return createBuiltIn(
        MEMBER_METHOD_MATCHER_PATH, JavaLinkMatchers.MEMBER_METHOD_FORWARD_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      callMatcherJava() {
    return createBuiltIn(
        CALL_MATCHER_PATH, JavaLinkMatchers.CALL_FORWARD_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      readMatcherJava() {
    return createBuiltIn(
        READ_MATCHER_PATH, JavaLinkMatchers.READ_FORWARD_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      writeMatcherJava() {
    return createBuiltIn(
        WRITE_MATCHER_PATH, JavaLinkMatchers.WRITE_FORWARD_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      innerTypeMatcherJava() {
    return createBuiltIn(
        INNER_TYPE_MATCHER_PATH, JavaLinkMatchers.INNER_TYPE_FORWARD_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      anonymousTypeMatcherJava() {
    return createBuiltIn(
        ANONYMOUS_TYPE_MATCHER_PATH, JavaLinkMatchers.ANONYMOUS_TYPE_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      classFileMatcherJava() {
    return createBuiltIn(
        CLASSFILE_MATCHER_PATH, JavaLinkMatchers.CLASSFILE_FORWARD_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      errorHandlingMatcherJava() {
    return createBuiltIn(
        ERROR_HANDLING_MATCHER_PATH, JavaLinkMatchers.ERROR_HANDLING_FORWARD_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      packageMatcherJava() {
    return createBuiltIn(
        PACKAGE_MATCHER_PATH, JavaLinkMatchers.PACKAGE_FORWARD_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      packageDirMatcherJava() {
    return createBuiltIn(
        PACKAGEDIR_MATCHER_PATH, JavaLinkMatchers.PACKAGEDIR_FORWARD_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      runtimeAnnotationMatcherJava() {
    return createBuiltIn(
        RUNTIME_ANNOTATION_MATCHER_PATH, JavaLinkMatchers.RUNTIME_ANNOTATION_FORWARD_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      compileAnnotationMatcherJava() {
    return createBuiltIn(
        COMPILE_ANNOTATION_MATCHER_PATH, JavaLinkMatchers.COMPILE_ANNOTATION_FORWARD_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      moduleExportedToMatcherJava() {
    return createBuiltIn(
        MODULE_EXPORTED_TO_MATCHER_PATH, JavaLinkMatchers.MODULE_EXPORTED_TO_FORWARD_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      moduleExportsMatcherJava() {
    return createBuiltIn(
        MODULE_EXPORTS_MATCHER_PATH, JavaLinkMatchers.MODULE_EXPORTS_FORWARD_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      moduleMainMatcherJava() {
    return createBuiltIn(
        MODULE_MAIN_MATCHER_PATH, JavaLinkMatchers.MODULE_MAIN_FORWARD_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      modulePackageMatcherJava() {
    return createBuiltIn(
        MODULE_PACKAGE_MATCHER_PATH, JavaLinkMatchers.MODULE_PACKAGE_FORWARD_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      moduleProvidesMatcherJava() {
    return createBuiltIn(
        MODULE_PROVIDES_MATCHER_PATH, JavaLinkMatchers.MODULE_PROVIDES_FORWARD_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      moduleOpenedToMatcherJava() {
    return createBuiltIn(
        MODULE_OPENED_TO_MATCHER_PATH, JavaLinkMatchers.MODULE_OPENED_TO_FORWARD_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      moduleOpensMatcherJava() {
    return createBuiltIn(
        MODULE_OPENS_MATCHER_PATH, JavaLinkMatchers.MODULE_OPENS_FORWARD_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      moduleRequiresMatcherJava() {
    return createBuiltIn(
        MODULE_REQUIRES_MATCHER_PATH, JavaLinkMatchers.MODULE_REQUIRES_FORWARD_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      moduleUsesMatcherJava() {
    return createBuiltIn(
        MODULE_USES_MATCHER_PATH, JavaLinkMatchers.MODULE_USES_FORWARD_DOC);
  }

  /////////////////////////////////////
  // Aggregate matchers

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      classDerivedMatcherJava() {
    return createBuiltIn(
        JAVA_CLASS_DERIVED_MATCHER_PATH, JavaLinkMatchers.JAVA_CLASS_DERIVED_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      classMembersMatcherJava() {
    return createBuiltIn(
        JAVA_CLASS_MEMBER_MATCHER_PATH, JavaLinkMatchers.JAVA_CLASS_MEMBER_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      packageMembersMatcherJava() {
    return createBuiltIn(
        JAVA_PACKAGE_MEMBER_PATH, JavaLinkMatchers.JAVA_PACKAGE_MEMBER_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      memberMatcherJava() {
    return createBuiltIn(
        JAVA_TREE_MEMBER_MATCHER_PATH, JavaLinkMatchers.JAVA_TREE_MEMBER_DOC);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxLinkMatcherDocument>
      useMatcherJava() {
    return createBuiltIn(
        JAVA_USE_MATCHER_PATH, JavaLinkMatchers.JAVA_USE_DOC);
  }

  /////////////////////////////////////

  private static Path buildMatcherPath(DepanFxLinkMatcherDocument doc) {
    return JAVA_LINK_MATCHER_PATH.resolve(doc.getToolName());
  }

  private DepanFxBuiltInContribution<DepanFxLinkMatcherDocument> createBuiltIn(
      Path docPath, DepanFxLinkMatcherDocument matcherDoc) {
    return new DepanFxBuiltInContribution.Simple<>(docPath, matcherDoc);
  }
}
