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
package com.pnambic.depanfx.edgematchers.link;

import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLink;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;

public class DepanFxLinks {

  private DepanFxLinks () {
    // Prevent instantiation.
  }

  public static class Simple implements DepanFxLink {

    private final GraphNode source;

    private final GraphNode target;

    public Simple(GraphNode source, GraphNode target) {
      this.source = source;
      this.target = target;
    }

    @Override
    public GraphNode getSource() {
      return source;
    }

    @Override
    public GraphNode getTarget() {
      return target;
    }
  }

  static abstract class OnEdge implements DepanFxLink {

    protected final GraphEdge edge;

    public OnEdge(GraphEdge edge) {
      this.edge = edge;
    }
  }

  static class Forward extends OnEdge {

    public Forward(GraphEdge edge) {
      super(edge);
    }

    @Override
    public GraphNode getSource() {
      return edge.getHead();
    }

    @Override
    public GraphNode getTarget() {
      return edge.getTail();
    }
  }

  static class Reverse extends OnEdge {

    public Reverse(GraphEdge edge) {
      super(edge);
    }

    @Override
    public GraphNode getSource() {
      return edge.getTail();
    }

    @Override
    public GraphNode getTarget() {
      return edge.getHead();
    }
  }
}
