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
import com.pnambic.depanfx.java.context.MemberNodeId;

public class MethodNode extends MemberNode {

  public MethodNode(String fqcn, String memberName) {
    super(new MemberNodeId(
        JavaContextDefinition.METHOD_NKID, fqcn, memberName));
  }

  public String toString() {
    String kindKey = getId().getContextNodeKindId().getNodeKindKey();
    return kindKey + ":" + getId().getNodeKey();
  }
}
