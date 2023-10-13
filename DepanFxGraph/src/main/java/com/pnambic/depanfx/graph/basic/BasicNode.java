/*
 * Copyright 2006,2023 The Depan Project Authors
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
package com.pnambic.depanfx.graph.basic;

import com.pnambic.depanfx.graph.api.Node;
import com.pnambic.depanfx.graph.api.NodeVisitor;

public class BasicNode<N> implements Node<N> {

  private final N id;

  public BasicNode(N id) {
    this.id = id;
  }

  @Override
  public N getId() {
    return id;
  }

  @Override
  public void accept(NodeVisitor vistor) {
    // Ignore unrecognized visitors.
  }

}
