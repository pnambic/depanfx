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
import com.pnambic.depanfx.graph.context.ContextRelationId;

import java.util.Objects;

public class JavaRelationId implements ContextRelationId {

  private final ContextModelId contextId;

  private final String relationKey;

  public JavaRelationId(
      JavaContextModelId contextId, String relationKey) {
    this.contextId = contextId;
    this.relationKey = relationKey;
  }

  @Override
  public String getRelationKey() {
    return relationKey;
  }

  @Override
  public ContextModelId getContextModelId() {
    return contextId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(contextId, relationKey);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof JavaRelationId)) {
      return false;
    }

    JavaRelationId other = (JavaRelationId) obj;
    return Objects.equals(contextId, other.contextId)
        && Objects.equals(relationKey, other.relationKey);
  }
}
