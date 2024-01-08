package com.pnambic.depanfx.workspace.documents;

import com.pnambic.depanfx.workspace.DepanFxProjectDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DocumentRegistry {

  private static final Logger LOG =
      LoggerFactory.getLogger(DocumentRegistry.class);

  private final Map<URI, Object> registry = new HashMap<>();

  public void registerDocument(DepanFxProjectDocument projDoc, Object document) {
    registry.put(getUri(projDoc), document);
  }

  public Optional<Object> findResource(DepanFxProjectDocument projDoc) {
    return Optional.ofNullable(registry.get(getUri(projDoc)));
  }

  public <T> List<T> findByType(Class<T> type) {
    return registry.values().stream()
        .filter(d -> type.isAssignableFrom(d.getClass()))
        .map(type::cast)
        .collect(Collectors.toList());
  }

  private URI getUri(DepanFxProjectDocument projDoc) {
    Path memberPath = projDoc.getMemberPath();
    if (!memberPath.isAbsolute()) {
      LOG.warn("Unexpected relative document path {}", memberPath);
    }
    return memberPath.toAbsolutePath().toUri();
  }
}
