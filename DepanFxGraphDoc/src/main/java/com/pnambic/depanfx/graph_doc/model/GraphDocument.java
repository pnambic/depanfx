/*
 * Copyright 2010, 2023 The Depan Project Authors
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

package com.pnambic.depanfx.graph_doc.model;

import com.pnambic.depanfx.graph.context.ContextModel;
import com.pnambic.depanfx.graph.model.GraphModel;

/**
 * A document that provides information about a dependency graph.  In addition
 * to the basic nodes and edges of the dependency graph is supplemented with
 * a set of associated Analysis plugins.  This allows the UI to make productive
 * choices for the user in the face of many different kinds of analysis.
 * 
 * <p>This data structure is expected to replace the raw dependency graph files
 * (.dpang) that DepAn uses for persistent graphs.
 *
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class GraphDocument {

  /**
   * Standard extension to use when loading or saving {@code ViewDocument}s.
   * The characters represent "DepAn Graph Info".
   */
  public static final String EXTENSION = "dgi";

  /**
   * Collected relation and node type providers.
   */
  private final ContextModel<?, ?> graphModel;

  /**
   * The dependency graph provided by this document.
   */
  private final GraphModel graph;

  /**
   * Create a graph from an analyzer an a graph model.
   * 
   * @param defaultAnalyzer
   * @param graph
   */
  public GraphDocument(ContextModel<?, ?> graphModel, GraphModel graph) {
    this.graphModel = graphModel;
    this.graph = graph;
  }

  /**
   * Provide the current set of analyzers.
   * 
   * @return list of dependency analyzers for the UI to use when manipulating
   *     this graph model
   */
  public ContextModel<?, ?> getContextModel() {
    return graphModel;
  }

  /**
   * Provide the {@link GraphModel} for this {@link GraphDocument}.
   */
  public GraphModel getGraph() {
    return graph;
  }
}
