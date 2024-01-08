package com.pnambic.depanfx.graph_doc.model;

import com.pnambic.depanfx.graph.model.GraphContextModel;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.nio.file.Path;

/**
 * Wrap the graph context model with enough metadata (name, description)
 * to place it into the built in project as a standard document.
 */
public class GraphContextDocument {

  /**
   * Common extension for any workspace persisted graph document.
   * Regardless, external persistence of graph contexts is expected
   * to be rare.
   */
  public static final String GRAPH_CONTEXT_DOC_EXT = "dgci";

  public static final Path CONTEXT_MODEL_PATH =
      DepanFxProjects.TOOLS_PATH.resolve("Graph Context Models");

  /**
   * Just the name of the graph context (e.g. "File System", "Java").
   * Maybe versioned (e.g. "Perl 5").
   */
  private final String graphContextName;

  /***
   * Rarely deep:  "Identifies the graph elements and components for the
   *  Blix graph context." is typical and fine.
   */
  private final String graphContextDescription;

  /**
   * The wrapped data.
   */
  private final GraphContextModel graphContext;

  public GraphContextDocument(String graphContextName,
      String graphContextDescription, GraphContextModel graphContext) {
    this.graphContextName = graphContextName;
    this.graphContextDescription = graphContextDescription;
    this.graphContext = graphContext;
  }

  public static String getGraphContextDocExt() {
    return GRAPH_CONTEXT_DOC_EXT;
  }

  public String getGraphContextName() {
    return graphContextName;
  }

  public String getGraphContextDescription() {
    return graphContextDescription;
  }

  public GraphContextModel getGraphContext() {
    return graphContext;
  }
}
