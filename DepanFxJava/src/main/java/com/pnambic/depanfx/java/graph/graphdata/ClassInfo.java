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
package com.pnambic.depanfx.java.graph.graphdata;

import com.pnambic.depanfx.graph.info.GraphNodeInfo;

/**
 * Non-Id traits of a class.
 */
public class ClassInfo implements GraphNodeInfo {

  public enum ClassKind {
    KIND_CLASS,
    KIND_INTERFACE,
    KIND_ENUM,
    KIND_ANNOTATION,
    KIND_MODULE,
    KIND_RECORD;
  }

  private final ClassKind classKind;

  public ClassInfo(ClassKind classKind) {
    this.classKind = classKind;
  }

  public ClassKind getClassKind() {
    return classKind;
  }
}
