package com.pnambic.depanfx.perspective.graphdoc;

import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.scene.DepanFxSceneControls;

import javafx.stage.FileChooser.ExtensionFilter;

public class GraphDocumentData {

  public static final ExtensionFilter GRAPH_DOC_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Graph Info", GraphDocument.GRAPH_DOC_EXT);
}
