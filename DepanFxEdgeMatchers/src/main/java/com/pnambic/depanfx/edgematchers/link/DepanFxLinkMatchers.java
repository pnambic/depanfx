/*
 * Copyright 2023 The Depan Project Authors
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
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcher;
import com.pnambic.depanfx.graph.api.Relation;
import com.pnambic.depanfx.graph.context.ContextRelationId;
import com.pnambic.depanfx.graph.model.GraphEdge;

import java.util.List;
import java.util.Optional;

public class DepanFxLinkMatchers {

  private DepanFxLinkMatchers() {
    // Prevent instantiation.
  }

  public static final DepanFxLinkMatcher EMPTY_MATCHER =
      new DepanFxLinkMatcher() {

        @Override
        public Optional<DepanFxLink> match(GraphEdge edge) {
          return Optional.empty();
        }
      };

  public static final DepanFxLinkMatcher ALL_EDGES_FORWARD =

      new DepanFxLinkMatcher() {

        @Override
        public Optional<DepanFxLink> match(GraphEdge edge) {
          return Optional.of(new DepanFxLinks.Forward(edge));
        }
  };

  public static final DepanFxLinkMatcher ALL_EDGES_REVERSE =

      new DepanFxLinkMatcher() {

        @Override
        public Optional<DepanFxLink> match(GraphEdge edge) {
          return Optional.of(new DepanFxLinks.Reverse(edge));
        }
  };

  public static class ForwardRelation implements DepanFxLinkMatcher {

    private final Relation<? extends ContextRelationId> forwardRelation;

    public ForwardRelation(
        Relation<? extends ContextRelationId> forwardRelation) {
      this.forwardRelation = forwardRelation;
    }

    @Override
    public Optional<DepanFxLink> match(GraphEdge edge) {
      if (edge.getRelation() == forwardRelation) {
        return Optional.of(new DepanFxLinks.Forward(edge));
      }
      return Optional.empty();
    }
  }

  public static class ReverseRelation implements DepanFxLinkMatcher {

    private final Relation<? extends ContextRelationId> reverseRelation;

    public ReverseRelation(
        Relation<? extends ContextRelationId> reverseRelation) {
      this.reverseRelation = reverseRelation;
    }

    @Override
    public Optional<DepanFxLink> match(GraphEdge edge) {
      if (edge.getRelation() == reverseRelation) {
        return Optional.of(new DepanFxLinks.Reverse(edge));
      }
      return Optional.empty();
    }
  }

  public static class Composite
      implements DepanFxLinkMatcher {

    private final List<DepanFxLinkMatcher> matchers;

    public Composite(List<DepanFxLinkMatcher> matchers) {
      this.matchers = matchers;
    }

    @Override
    public Optional<DepanFxLink> match(GraphEdge edge) {
      return matchers.stream()
          .flatMap(m -> m.match(edge).stream())
          .findFirst();
    }
  }
}
