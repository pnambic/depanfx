package com.pnambic.depanfx.java.nodelist.link;

import com.pnambic.depanfx.java.context.JavaContextDefinition;
import com.pnambic.depanfx.java.graph.JavaRelation;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcher;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatchers.Composite;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatchers.ForwardRelation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JavaLinkMatchers {

  public JavaLinkMatchers() {
    // Prevent instantiation.
  }

  /////////////////////////////////////
  // Matchers for each relationship

  public static final ForwardRelation CLASS_FORWARD =
      new ForwardRelation(JavaRelation.CLASS);

  public static final ForwardRelation EXTENDS_FORWARD =
      new ForwardRelation(JavaRelation.EXTENDS);

  public static final ForwardRelation IMPLEMENTS_FORWARD =
      new ForwardRelation(JavaRelation.IMPLEMENTS);

  public static final ForwardRelation TYPE_FORWARD =
      new ForwardRelation(JavaRelation.TYPE);

  public static final ForwardRelation STATIC_FIELD_FORWARD =
      new ForwardRelation(JavaRelation.STATIC_FIELD);

  public static final ForwardRelation MEMBER_FIELD_FORWARD =
      new ForwardRelation(JavaRelation.MEMBER_FIELD);

  public static final ForwardRelation STATIC_METHOD_FORWARD =
      new ForwardRelation(JavaRelation.STATIC_METHOD);

  public static final ForwardRelation MEMBER_METHOD_FORWARD =
      new ForwardRelation(JavaRelation.MEMBER_METHOD);

  public static final ForwardRelation CALL_FORWARD =
      new ForwardRelation(JavaRelation.CALL);

  public static final ForwardRelation READ_FORWARD =
      new ForwardRelation(JavaRelation.READ);

  public static final ForwardRelation WRITE_FORWARD =
      new ForwardRelation(JavaRelation.WRITE);

  public static final ForwardRelation INNER_TYPE_FORWARD =
      new ForwardRelation(JavaRelation.INNER_TYPE);

  public static final ForwardRelation ANONYMOUS_TYPE_FORWARD =
      new ForwardRelation(JavaRelation.ANONYMOUS_TYPE);

  public static final ForwardRelation CLASSFILE_FORWARD =
      new ForwardRelation(JavaRelation.CLASSFILE);

  public static final ForwardRelation ERROR_HANDLING_FORWARD =
      new ForwardRelation(JavaRelation.ERROR_HANDLING);

  public static final ForwardRelation PACKAGE_FORWARD =
      new ForwardRelation(JavaRelation.PACKAGE);

  public static final ForwardRelation PACKAGEDIR_FORWARD =
      new ForwardRelation(JavaRelation.PACKAGEDIR);

  public static final ForwardRelation RUNTIME_ANNOTATION_FORWARD =
      new ForwardRelation(JavaRelation.RUNTIME_ANNOTATION);

  public static final ForwardRelation COMPILE_ANNOTATION_FORWARD =
      new ForwardRelation(JavaRelation.COMPILE_ANNOTATION);

  public static final ForwardRelation MODULE_EXPORTED_TO_FORWARD =
      new ForwardRelation(JavaRelation.MODULE_EXPORTED_TO);

  public static final ForwardRelation MODULE_EXPORTS_FORWARD =
      new ForwardRelation(JavaRelation.MODULE_EXPORTS);

  public static final ForwardRelation MODULE_MAIN_FORWARD =
      new ForwardRelation(JavaRelation.MODULE_MAIN);

  public static final ForwardRelation MODULE_PACKAGE_FORWARD =
      new ForwardRelation(JavaRelation.MODULE_PACKAGE);

  public static final ForwardRelation MODULE_PROVIDES_FORWARD =
      new ForwardRelation(JavaRelation.MODULE_PROVIDES);

  public static final ForwardRelation MODULE_OPENED_TO_FORWARD =
      new ForwardRelation(JavaRelation.MODULE_OPENED_TO);

  public static final ForwardRelation MODULE_OPENS_FORWARD =
      new ForwardRelation(JavaRelation.MODULE_OPENS);

  public static final ForwardRelation MODULE_REQUIRES_FORWARD =
      new ForwardRelation(JavaRelation.MODULE_REQUIRES);

  public static final ForwardRelation MODULE_USES_FORWARD =
      new ForwardRelation(JavaRelation.MODULE_USES);

  /////////////////////////////////////
  // Documents for each relationship

  public static final DepanFxLinkMatcherDocument CLASS_FORWARD_DOC =
      buildMatcherDoc("Class Members", "Package contains class.", CLASS_FORWARD);

  public static final DepanFxLinkMatcherDocument EXTENDS_FORWARD_DOC =
      buildMatcherDoc("Class Extends", "Class extension.", EXTENDS_FORWARD);

  public static final DepanFxLinkMatcherDocument IMPLEMENTS_FORWARD_DOC =
      buildMatcherDoc("Class Implements", "Class implements.",
          IMPLEMENTS_FORWARD);

  public static final DepanFxLinkMatcherDocument TYPE_FORWARD_DOC =
      buildMatcherDoc("Class Dependency", "Class requires type.",
          TYPE_FORWARD);

  public static final DepanFxLinkMatcherDocument STATIC_FIELD_FORWARD_DOC =
      buildMatcherDoc("Static Field", "Static field in class.",
          STATIC_FIELD_FORWARD);

  public static final DepanFxLinkMatcherDocument MEMBER_FIELD_FORWARD_DOC =
      buildMatcherDoc("Member Field", "Member field in class.",
          MEMBER_FIELD_FORWARD);

  public static final DepanFxLinkMatcherDocument STATIC_METHOD_FORWARD_DOC =
      buildMatcherDoc("Static Method", "Static method in class.",
          STATIC_METHOD_FORWARD);

  public static final DepanFxLinkMatcherDocument MEMBER_METHOD_FORWARD_DOC =
      buildMatcherDoc("Member Method", "Member method in class.",
          MEMBER_METHOD_FORWARD);

  public static final DepanFxLinkMatcherDocument CALL_FORWARD_DOC =
      buildMatcherDoc("Method Call", "Method calls method.", CALL_FORWARD);

  public static final DepanFxLinkMatcherDocument READ_FORWARD_DOC =
      buildMatcherDoc("Field Read", "Method reads from field.", READ_FORWARD);

  public static final DepanFxLinkMatcherDocument WRITE_FORWARD_DOC =
      buildMatcherDoc("Field Write", "Method writes to field.", WRITE_FORWARD);

  public static final DepanFxLinkMatcherDocument INNER_TYPE_FORWARD_DOC =
      buildMatcherDoc("Class Inner Type", "Class contains inner type.",
          INNER_TYPE_FORWARD);

  public static final DepanFxLinkMatcherDocument ANONYMOUS_TYPE_DOC =
      buildMatcherDoc("Class Anonymous Type", "Class contains anonymous type.",
          ANONYMOUS_TYPE_FORWARD);

  public static final DepanFxLinkMatcherDocument CLASSFILE_FORWARD_DOC =
      buildMatcherDoc(
          "Class File", "File contains the implemention of the class.",
          CLASSFILE_FORWARD);

  public static final DepanFxLinkMatcherDocument ERROR_HANDLING_FORWARD_DOC =
      buildMatcherDoc("Error Handling", "Method handles errors for the type.",
          ERROR_HANDLING_FORWARD);

  public static final DepanFxLinkMatcherDocument PACKAGE_FORWARD_DOC =
      buildMatcherDoc("Package Member", "Parent package with nested package.",
          PACKAGE_FORWARD);

  public static final DepanFxLinkMatcherDocument PACKAGEDIR_FORWARD_DOC =
      buildMatcherDoc("Package Directory", "Directory contains package.",
          PACKAGEDIR_FORWARD);

  public static final DepanFxLinkMatcherDocument RUNTIME_ANNOTATION_FORWARD_DOC =
      buildMatcherDoc("Package Member", "Parent package with nested package.",
          RUNTIME_ANNOTATION_FORWARD);

  public static final DepanFxLinkMatcherDocument COMPILE_ANNOTATION_FORWARD_DOC =
      buildMatcherDoc("Package Member", "Parent package with nested package.",
          COMPILE_ANNOTATION_FORWARD);

  public static final DepanFxLinkMatcherDocument MODULE_EXPORTED_TO_FORWARD_DOC =
      buildMatcherDoc("Exported To", "Package is exported to explicit module.",
          MODULE_EXPORTED_TO_FORWARD);

  public static final DepanFxLinkMatcherDocument MODULE_EXPORTS_FORWARD_DOC =
      buildMatcherDoc("Exports", "Module exports package.",
          MODULE_EXPORTS_FORWARD);

  public static final DepanFxLinkMatcherDocument MODULE_MAIN_FORWARD_DOC =
      buildMatcherDoc("Module Main", "Module has main class.",
          MODULE_MAIN_FORWARD);

  public static final DepanFxLinkMatcherDocument MODULE_PACKAGE_FORWARD_DOC =
      buildMatcherDoc("Module Package", "Module has package.",
          MODULE_PACKAGE_FORWARD);

  public static final DepanFxLinkMatcherDocument MODULE_PROVIDES_FORWARD_DOC =
      buildMatcherDoc("Module Provides", "Module provides service class.",
          MODULE_PROVIDES_FORWARD);

  public static final DepanFxLinkMatcherDocument MODULE_OPENED_TO_FORWARD_DOC =
      buildMatcherDoc("Module Opened To", "Package opened to explicit module.",
          MODULE_OPENED_TO_FORWARD);

  public static final DepanFxLinkMatcherDocument MODULE_OPENS_FORWARD_DOC =
      buildMatcherDoc("Module Opens", "Module opens package.",
          MODULE_OPENS_FORWARD);

  public static final DepanFxLinkMatcherDocument MODULE_REQUIRES_FORWARD_DOC =
      buildMatcherDoc("Module Requires", "Module requires module.",
          MODULE_REQUIRES_FORWARD);

  public static final DepanFxLinkMatcherDocument MODULE_USES_FORWARD_DOC =
      buildMatcherDoc("Module Uses", "Module uses service class.",
          MODULE_REQUIRES_FORWARD);

  /////////////////////////////////////
  // Aggregate matchers and documents

  // Just members of classes
  public static final List<DepanFxLinkMatcher> JAVA_CLASS_MEMBERS =
      Arrays.asList(new DepanFxLinkMatcher [] {
          STATIC_FIELD_FORWARD, MEMBER_FIELD_FORWARD,
          STATIC_METHOD_FORWARD, MEMBER_METHOD_FORWARD,
          INNER_TYPE_FORWARD, ANONYMOUS_TYPE_FORWARD
      });

  public static final Composite JAVA_CLASS_MEMBER_MATCH =
      new Composite(JAVA_CLASS_MEMBERS);

  public static final DepanFxLinkMatcherDocument JAVA_CLASS_MEMBER_DOC =
      buildMatcherDoc(
          "Java Class Members", "Java class membership.",
          JAVA_CLASS_MEMBER_MATCH);

  // Just members of packages
  public static final List<DepanFxLinkMatcher> JAVA_PACKAGE_MEMBERS =
      Arrays.asList(new DepanFxLinkMatcher [] {
          CLASS_FORWARD, PACKAGE_FORWARD
      });

  public static final Composite JAVA_PACKAGE_MEMBER_MATCH =
      new Composite(JAVA_PACKAGE_MEMBERS);

  public static final DepanFxLinkMatcherDocument JAVA_PACKAGE_MEMBER_DOC =
      new DepanFxLinkMatcherDocument(
          "Java Package Members", "Java package membership.",
          JavaContextDefinition.MODEL_ID,
          Collections.emptyList(), JAVA_PACKAGE_MEMBER_MATCH);

  // Java package and class members
  public static final List<DepanFxLinkMatcher> JAVA_MEMBERS =
      Arrays.asList(new DepanFxLinkMatcher [] {
          STATIC_FIELD_FORWARD, MEMBER_FIELD_FORWARD,
          STATIC_METHOD_FORWARD, MEMBER_METHOD_FORWARD,
          INNER_TYPE_FORWARD, ANONYMOUS_TYPE_FORWARD,
          CLASS_FORWARD, PACKAGE_FORWARD
      });

  public static final Composite JAVA_MEMBER_MATCH =
      new Composite(JAVA_MEMBERS);

  public static final DepanFxLinkMatcherDocument JAVA_MEMBER_DOC =
      buildMatcherDoc(
          "Java Members", "Java membership.",
          JAVA_PACKAGE_MEMBER_MATCH);

  // Common notion of "use"
  public static final List<DepanFxLinkMatcher> USE =
      Arrays.asList(new DepanFxLinkMatcher [] {
          CALL_FORWARD, READ_FORWARD, WRITE_FORWARD
      });

  public static final Composite USE_MATCH =
      new Composite(USE);

  public static final DepanFxLinkMatcherDocument USE_DOC =
      buildMatcherDoc("Java use", "Java use.", USE_MATCH);

  private static DepanFxLinkMatcherDocument buildMatcherDoc(
      String matcherName, String matcherDescr, DepanFxLinkMatcher matcher) {
    return new DepanFxLinkMatcherDocument(
        matcherName, matcherDescr,
        JavaContextDefinition.MODEL_ID,
        Collections.emptyList(), matcher);

  }
}
