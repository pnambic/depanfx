/*
 * Copyright 2024 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pnambic.depanfx.java.graph;

import com.pnambic.depanfx.graph.context.ContextRelationId;
import com.pnambic.depanfx.graph.model.GraphRelation;
import com.pnambic.depanfx.java.context.JavaContextDefinition;

public class JavaRelation extends GraphRelation {

  private JavaRelation(
      ContextRelationId id, String forwardName, String reverseName) {
    super(id, forwardName, reverseName);
  }

  /////////////////////////////////////
  // "Use" relationships

  public static final JavaRelation CALL =
      new JavaRelation(JavaContextDefinition.CALL_RELID,
          "calls", "called by");

  public static final JavaRelation READ =
      new JavaRelation(JavaContextDefinition.READ_RELID,
          "reads", "provides");

  public static final JavaRelation TYPE =
      new JavaRelation(JavaContextDefinition.TYPE_RELID,
          "type references", "used by");

  /////////////////////////////////////
  // Container relationships

  public static final JavaRelation PACKAGEDIR =
      new JavaRelation(JavaContextDefinition.PACKAGEDIR_RELID,
          "contain package", "within directory");

  public static final JavaRelation CLASSFILE =
      new JavaRelation(JavaContextDefinition.CLASSFILE_RELID,
          "contains class", "within file");

  public static final JavaRelation PACKAGE =
      new JavaRelation(JavaContextDefinition.PACKAGE_RELID,
          "contains package", "subpackage of");

  public static final JavaRelation CLASS =
      new JavaRelation(JavaContextDefinition.CLASS_RELID,
          "contains class", "in package");

  public static final JavaRelation INNER_TYPE =
      new JavaRelation(JavaContextDefinition.INNER_TYPE_RELID,
          "contains type", "within type");

  public static final JavaRelation ANONYMOUS_TYPE =
      new JavaRelation(JavaContextDefinition.ANONYMOUS_TYPE_RELID,
          "contains types", "within type");

  /////////////////////////////////////
  // Instance member relationships

  public static final JavaRelation MEMBER_FIELD =
      new JavaRelation(JavaContextDefinition.MEMBER_FIELD_RELID,
          "has member field", "member field of");

  public static final JavaRelation MEMBER_METHOD =
      new JavaRelation(JavaContextDefinition.MEMBER_METHOD_RELID,
          "has member method", "member method of");

  /////////////////////////////////////
  // Static member relationships

  public static final JavaRelation STATIC_FIELD =
      new JavaRelation(JavaContextDefinition.STATIC_FIELD_RELID,
          "has static field", "static field of");

  public static final JavaRelation STATIC_METHOD =
      new JavaRelation(JavaContextDefinition.STATIC_METHOD_RELID,
          "has static method", "static method of");

  /////////////////////////////////////
  // Depends on relationships

  public static final JavaRelation EXTENDS =
      new JavaRelation(JavaContextDefinition.EXTENDS_RELID,
          "extends", "derived from");

  public static final JavaRelation IMPLEMENTS =
      new JavaRelation(JavaContextDefinition.IMPLEMENTS_RELID,
          "implements", "implemented by");

  /////////////////////////////////////
  // Extension relationships

  public static final JavaRelation ERROR_HANDLING =
      new JavaRelation(JavaContextDefinition.ERROR_HANDLING_RELID,
          "handles", "caught by");

  /////////////////////////////////////
  // Annotation relationships

  public static final JavaRelation RUNTIME_ANNOTATION =
      new JavaRelation(JavaContextDefinition.RUNTIME_ANNOTATION_RELID,
          "annotation (runtime)", "annotator (runtime)");

  public static final JavaRelation COMPILE_ANNOTATION =
      new JavaRelation(JavaContextDefinition.COMPILE_ANNOTATION_RELID,
          "annotation (compile)", "annotator (compile)");

  /////////////////////////////////////
  // Module relationships

  public static final JavaRelation MODULE_EXPORTED_TO =
      new JavaRelation(JavaContextDefinition.MODULE_EXPORTED_TO_RELID,
          "exported to", "can use");

  public static final JavaRelation MODULE_EXPORTS =
      new JavaRelation(JavaContextDefinition.MODULE_EXPORTS_RELID,
          "exports", "exported by");

  public static final JavaRelation MODULE_PROVIDES =
      new JavaRelation(JavaContextDefinition.MODULE_PROVIDES_RELID,
          "provides", "provided by");

  public static final JavaRelation MODULE_OPENED_TO =
      new JavaRelation(JavaContextDefinition.MODULE_OPENED_TO_RELID,
          "opened to", "can inspect");

  public static final JavaRelation MODULE_OPENS =
      new JavaRelation(JavaContextDefinition.MODULE_OPENS_RELID,
          "opens", "opened by");

  public static final JavaRelation MODULE_REQUIRES =
      new JavaRelation(JavaContextDefinition.MODULE_REQUIRES_RELID,
          "requires", "required by");

  public static final JavaRelation MODULE_USES =
      new JavaRelation(JavaContextDefinition.MODULE_USES_RELID,
          "uses", "used by");

  /////////////////////////////////////
  // Everyone

  public static final GraphRelation[] RELATIONS = {
      CLASS, EXTENDS, IMPLEMENTS, ANONYMOUS_TYPE, TYPE,
      STATIC_FIELD, MEMBER_FIELD, STATIC_METHOD, MEMBER_METHOD,
      CALL, READ, CLASSFILE, ERROR_HANDLING, PACKAGE, PACKAGEDIR,
      RUNTIME_ANNOTATION, COMPILE_ANNOTATION,
      MODULE_EXPORTED_TO, MODULE_EXPORTS,
      MODULE_PROVIDES, MODULE_OPENED_TO, MODULE_OPENS,
      MODULE_REQUIRES, MODULE_USES
  };
}
