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

import com.pnambic.depanfx.graph.info.GraphEdgeInfo;

/**
 * Non-Id traits of a module edge.
 */
public class ModuleEdgeInfo implements GraphEdgeInfo {

  public enum ModuleEdgeKind {
    KIND_MODULE,
    KIND_TRANSITIVE
  }

  private final ModuleEdgeKind moduleEdgeKind;

  public ModuleEdgeInfo(ModuleEdgeKind moduleEdgeKind) {
    this.moduleEdgeKind = moduleEdgeKind;
  }

  public ModuleEdgeKind getModuleEdgeKind() {
    return moduleEdgeKind;
  }
}