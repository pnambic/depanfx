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
package com.pnambic.depanfx.edgematchers.tooldata;

import com.pnambic.depanfx.graph.model.GraphEdge;

import java.util.Optional;

public interface DepanFxLinkMatcher {

  String EDGE_VISIBILITY_CONTEXT_RESOURCE_NAME = "Edge Visibility";

  Optional<DepanFxLink> match(GraphEdge edge);
}
