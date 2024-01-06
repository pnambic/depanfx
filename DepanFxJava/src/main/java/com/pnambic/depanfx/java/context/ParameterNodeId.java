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

import java.util.Objects;

public class ParameterNodeId extends JavaNodeId {

  private final String parameterData;

  public ParameterNodeId(JavaNodeKindId nodeKindId, String parameterData) {
    super(nodeKindId);
    this.parameterData = parameterData;
  }

  @Override
  public String getNodeKey() {
    return parameterData;
  }

  @Override
  public int hashCode() {
    return hashCodeHelper(parameterData.hashCode());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ParameterNodeId)) {
      return false;
    }
    if (!super.equals(obj)) {
      return false;
    }
    ParameterNodeId other = (ParameterNodeId) obj;
    return Objects.equals(parameterData, other.parameterData);
  }
}
