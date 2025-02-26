package com.pnambic.depanfx.workspace.documents;

import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

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

  public void registerDocumentLoad(
      DepanFxProjectDocument projDoc, Object document) {
    registry.put(getUri(projDoc), document);

    LOG.debug("Loaded {} type {}.",
        DepanFxProjects.getDocumentLabel(projDoc),
        document.getClass().getName());
  }

  public void registerDocumentSave(
      DepanFxProjectDocument projDoc, Object document) {
    registry.put(getUri(projDoc), document);

    LOG.debug("Saved {} type {}.",
        DepanFxProjects.getDocumentLabel(projDoc),
        document.getClass().getName());
  }

  public Optional<Object> findResource(DepanFxProjectDocument projDoc) {
    Optional<Object> result =
        Optional.ofNullable(registry.get(getUri(projDoc)));
    result.ifPresentOrElse(
        r ->
        LOG.debug("findResource for {} returns {} data.",
            DepanFxProjects.getDocumentLabel(projDoc),
            r.getClass().getName()),
        () ->
        LOG.debug("findResource miss for {}.",
          DepanFxProjects.getDocumentLabel(projDoc)));
    return result;
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
