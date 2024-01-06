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
package com.pnambic.depanfx.java.context;

public class JavaContextDefinition {

  public static final JavaContextModelId MODEL_ID =
      new JavaContextModelId();

  /* Nodes */
  public static final JavaNodeKindId FIELD_NKID =
      new JavaNodeKindId(MODEL_ID, "Field");

  public static final JavaNodeKindId METHOD_NKID =
      new JavaNodeKindId(MODEL_ID, "Method");

  public static final JavaNodeKindId PARAMETER_NKID =
      new JavaNodeKindId(MODEL_ID, "Parameter");

  public static final JavaNodeKindId CLASS_NKID =
      new JavaNodeKindId(MODEL_ID, "Class");

  public static final JavaNodeKindId PACKAGE_NKID =
      new JavaNodeKindId(MODEL_ID, "Package");

  public static final JavaNodeKindId[] NODE_KIND_IDS = {
      // JVM recognized
      FIELD_NKID, METHOD_NKID, PARAMETER_NKID, CLASS_NKID,
      PACKAGE_NKID
  };

  /* Relations */
  public static final JavaRelationId CLASS_RELID =
      new JavaRelationId(MODEL_ID, "class");

  public static final JavaRelationId EXTENDS_RELID =
      new JavaRelationId(MODEL_ID, "extends");

  public static final JavaRelationId IMPLEMENTS_RELID =
      new JavaRelationId(MODEL_ID, "implements");

  public static final JavaRelationId ANONYMOUS_TYPE_RELID =
      new JavaRelationId(MODEL_ID, "anonymous_type");

  public static final JavaRelationId TYPE_RELID =
      new JavaRelationId(MODEL_ID, "type");

  public static final JavaRelationId STATIC_FIELD_RELID =
      new JavaRelationId(MODEL_ID, "static-field");

  public static final JavaRelationId MEMBER_FIELD_RELID =
      new JavaRelationId(MODEL_ID, "member-field");

  public static final JavaRelationId STATIC_METHOD_RELID =
      new JavaRelationId(MODEL_ID, "static-method");

  public static final JavaRelationId MEMBER_METHOD_RELID =
      new JavaRelationId(MODEL_ID, "member-method");

  public static final JavaRelationId INNER_TYPE_RELID =
      new JavaRelationId(MODEL_ID, "inner-type");

  public static final JavaRelationId CALL_RELID =
      new JavaRelationId(MODEL_ID, "call");

  public static final JavaRelationId READ_RELID =
      new JavaRelationId(MODEL_ID, "read");

  public static final JavaRelationId CLASSFILE_RELID =
      new JavaRelationId(MODEL_ID, "class-file");

  public static final JavaRelationId ERROR_HANDLING_RELID =
      new JavaRelationId(MODEL_ID, "error-handling");

  public static final JavaRelationId PACKAGE_RELID =
      new JavaRelationId(MODEL_ID, "package");

  public static final JavaRelationId PACKAGEDIR_RELID =
      new JavaRelationId(MODEL_ID, "package-dir");

  public static final JavaRelationId RUNTIME_ANNOTATION_RELID =
      new JavaRelationId(MODEL_ID, "runtime-annotation");

  public static final JavaRelationId COMPILE_ANNOTATION_RELID =
      new JavaRelationId(MODEL_ID, "compile-annotation");

  public static final JavaRelationId[] RELATION_IDS = {
      CLASS_RELID, EXTENDS_RELID, IMPLEMENTS_RELID, ANONYMOUS_TYPE_RELID,
      TYPE_RELID, STATIC_FIELD_RELID, MEMBER_FIELD_RELID,
      STATIC_METHOD_RELID, MEMBER_METHOD_RELID,
      INNER_TYPE_RELID, CALL_RELID, READ_RELID, CLASSFILE_RELID,
      ERROR_HANDLING_RELID, PACKAGE_RELID, PACKAGEDIR_RELID,
      RUNTIME_ANNOTATION_RELID, COMPILE_ANNOTATION_RELID
  };
}
