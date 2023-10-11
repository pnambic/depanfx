package com.pnambic.depanfx.workspace.documents;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.pnambic.depanfx.graph_doc.model.GraphDocument;

public class DocumentRegistry {
  private final Map<URI, Object> registry = new HashMap();

  public void registerDocument(Object document, URI uri) {
    registry.put(uri, document);
  }

  public List<GraphDocument> getGraphDocuments() {
    return registry.values().stream()
        .filter(d -> GraphDocument.class.isAssignableFrom(d.getClass()))
        .map(GraphDocument.class::cast)
        .collect(Collectors.toList());
  }
}
