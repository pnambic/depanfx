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

/**
 * Graph node id for a Java module.
 */
public class ModuleNodeId extends JavaNodeId {

  private final String moduleName;

  public ModuleNodeId(String moduleName) {
    super(JavaContextDefinition.MODULE_NKID);
    this.moduleName = moduleName;
  }

  @Override
  public String getNodeKey() {
    return moduleName;
  }

  public String getModuleName() {
    return moduleName;
  }

  @Override
  public int hashCode() {
    return hashCodeHelper(moduleName.hashCode());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ModuleNodeId)) {
      return false;
    }
    if (!super.equals(obj)) {
      return false;
    }
    ModuleNodeId other = (ModuleNodeId) obj;
    return Objects.equals(moduleName, other.moduleName);
  }
}
