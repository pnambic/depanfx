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

import com.pnambic.depanfx.graph.context.ContextNodeId;
import com.pnambic.depanfx.graph.context.ContextNodeKindId;

import java.util.Objects;

public abstract class JavaNodeId implements ContextNodeId {

  private final JavaNodeKindId nodeKindId;

  public JavaNodeId(JavaNodeKindId nodeKindId) {
    this.nodeKindId = nodeKindId;
  }

  @Override
  public ContextNodeKindId getContextNodeKindId() {
    return nodeKindId;
  }

  @Override
  public int hashCode() {
    return nodeKindId.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof JavaNodeId)) {
      return false;
    }
    JavaNodeId other = (JavaNodeId) obj;
    return Objects.equals(nodeKindId, other.nodeKindId);
  }

  /**
   * Combine the hash codes from this class and a derived type to provide
   * the required {@code hashCode()} value.
   *
   * @param derivedHash hash code of member fields in the derived type.
   * @return a hash code suitable for return by derived types.
   */
  protected int hashCodeHelper(int derivedHash) {
    final int prime = 31;
    return derivedHash + (prime * nodeKindId.hashCode());
  }
}
