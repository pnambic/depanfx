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

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.context.ContextNodeKindId;

import java.util.Objects;

public class JavaNodeKindId implements ContextNodeKindId {

  private final ContextModelId contextId;

  private final String nodeKindKey;

  public JavaNodeKindId(ContextModelId contextId, String nodeKindKey) {
    this.contextId = contextId;
    this.nodeKindKey = nodeKindKey;
  }

  @Override
  public ContextModelId getContextModelId() {
    return contextId;
  }

  @Override
  public String getNodeKindKey() {
    return nodeKindKey;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    JavaNodeKindId other = (JavaNodeKindId) obj;
    return Objects.equals(contextId, other.contextId)
        && Objects.equals(nodeKindKey, other.nodeKindKey);
  }

  @Override
  public int hashCode() {
      return Objects.hash(contextId, nodeKindKey);
  }
}
