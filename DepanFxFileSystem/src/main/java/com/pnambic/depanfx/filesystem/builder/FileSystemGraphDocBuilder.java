package com.pnambic.depanfx.filesystem.builder;

import java.io.IOException;

import com.pnambic.depanfx.filesystem.context.FileSystemContextDefinition;
import com.pnambic.depanfx.graph.model.GraphModel;
import com.pnambic.depanfx.graph_doc.builder.DepanFxGraphModelBuilder;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;

public class FileSystemGraphDocBuilder {

  private final DepanFxGraphModelBuilder graphBuilder;

  public FileSystemGraphDocBuilder(DepanFxGraphModelBuilder graphBuilder) {
    this.graphBuilder = graphBuilder;
  }

  public void analyzeTree(String treePath) {
    FileSystemDirectoryLoader loader = new FileSystemDirectoryLoader(graphBuilder, treePath);
    try {
      loader.analyzeTree(treePath);
    } catch (IOException errIo) {
      throw new RuntimeException(
          "Unable to analyze directory " + treePath, errIo);
    }
  }

  public GraphDocument getGraphDocument() {
    GraphModel graph = graphBuilder.createGraphModel();
    return new GraphDocument(FileSystemContextDefinition.MODEL_ID, graph);
  }
}
