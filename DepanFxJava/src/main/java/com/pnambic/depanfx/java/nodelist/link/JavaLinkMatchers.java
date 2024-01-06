package com.pnambic.depanfx.java.nodelist.link;

import com.pnambic.depanfx.java.context.JavaContextDefinition;
import com.pnambic.depanfx.java.graph.JavaRelation;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcher;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherGroup;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatchers.Composite;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatchers.ForwardRelation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JavaLinkMatchers {

  public JavaLinkMatchers() {
    // Prevent instantiation.
  }

  public static final ForwardRelation EXTENDS_FORWARD =
      new ForwardRelation(JavaRelation.EXTENDS);

  public static final ForwardRelation IMPLEMENTS_FORWARD =
      new ForwardRelation(JavaRelation.IMPLEMENTS);

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

  public static final ForwardRelation INNER_TYPE_FORWARD =
      new ForwardRelation(JavaRelation.INNER_TYPE);

  public static final ForwardRelation CLASSFILE_FORWARD =
      new ForwardRelation(JavaRelation.CLASSFILE);

  public static final ForwardRelation PACKAGE_FORWARD =
      new ForwardRelation(JavaRelation.PACKAGE);

  public static final ForwardRelation PACKAGEDIR_FORWARD =
      new ForwardRelation(JavaRelation.PACKAGEDIR);

  public static final ForwardRelation RUNTIME_ANNOTATION_FORWARD =
      new ForwardRelation(JavaRelation.RUNTIME_ANNOTATION);

  public static final ForwardRelation COMPILE_ANNOTATION_FORWARD =
      new ForwardRelation(JavaRelation.COMPILE_ANNOTATION);

  // Full membership over graph should include
  public static final List<DepanFxLinkMatcher> JAVA_CLASS_MEMBERS =
      Arrays.asList(new DepanFxLinkMatcher [] {
          STATIC_FIELD_FORWARD, MEMBER_FIELD_FORWARD,
          STATIC_METHOD_FORWARD, MEMBER_METHOD_FORWARD,
          INNER_TYPE_FORWARD
      });

  public static final Composite JAVA_CLASS_MEMBER_MATCH =
      new Composite(JAVA_CLASS_MEMBERS);

  public static final DepanFxLinkMatcherDocument JAVA_MEMBER_DOC =
      new DepanFxLinkMatcherDocument(JavaContextDefinition.MODEL_ID,
          Collections.emptyList(), JAVA_CLASS_MEMBER_MATCH);

  public static final DepanFxLinkMatcherDocument CALL_DOC =
      new DepanFxLinkMatcherDocument(JavaContextDefinition.MODEL_ID,
          Collections.emptyList(), CALL_FORWARD);
}
