package com.pnambic.depanfx.workspace.documents;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;

public class DocumentRegistry {

  private final Map<URI, Object> registry = new HashMap<>();

  public void registerDocument(DepanFxProjectDocument projDoc, Object document) {
    registry.put(getUri(projDoc), document);
  }

  public Optional<Object> findResource(DepanFxProjectDocument projDoc) {
    return Optional.ofNullable(registry.get(getUri(projDoc)));
  }

  private void registerDocument(Object document, URI uri) {
    registry.put(uri, document);
  }

  private Optional<Object> findResource(URI uri) {
    return Optional.ofNullable(registry.get(uri));
  }

  public List<GraphDocument> getGraphDocuments() {
    return registry.values().stream()
        .filter(d -> GraphDocument.class.isAssignableFrom(d.getClass()))
        .map(GraphDocument.class::cast)
        .collect(Collectors.toList());
  }

  private URI getUri(DepanFxProjectDocument projDoc) {
    return projDoc.getMemberPath().toAbsolutePath().toUri();
  }
}
