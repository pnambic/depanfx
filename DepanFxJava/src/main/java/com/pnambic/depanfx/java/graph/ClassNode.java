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

import com.pnambic.depanfx.java.context.JavaContextDefinition;
import com.pnambic.depanfx.java.context.TypeNodeId;

public class ClassNode extends JavaNode {

  public ClassNode(String fqcn) {
    super(new TypeNodeId(JavaContextDefinition.CLASS_NKID, fqcn));
  }

  public String getFQCN() {
    return ((TypeNodeId) getId()).getFQCN();
  }
}
