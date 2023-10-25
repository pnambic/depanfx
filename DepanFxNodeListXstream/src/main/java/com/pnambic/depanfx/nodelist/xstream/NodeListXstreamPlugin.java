package com.pnambic.depanfx.nodelist.xstream;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.persistence.DocumentXmlPersist;
import com.pnambic.depanfx.persistence.builder.DocumentXmlPersistBuilder;
import com.pnambic.depanfx.persistence.plugins.DocumentPersistenceContribution;
import com.pnambic.depanfx.persistence.plugins.GraphNodePersistencePluginRegistry;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.stereotype.Component;

@Component
public class NodeListXstreamPlugin implements DocumentPersistenceContribution {

  /**
   * Standard extension to use when loading or saving {@code NodeList}s.
   * The characters represent "DepAn Graph Info".
   */
  public static final String EXTENSION = "dnli";

  public static final String NODE_LIST_TAG = "node-list";

  private final GraphNodePersistencePluginRegistry graphNodeRegistry;

  public NodeListXstreamPlugin(GraphNodePersistencePluginRegistry graphNodeRegistry) {
    this.graphNodeRegistry = graphNodeRegistry;
  }

  @Override
  public boolean acceptsDocument(Object document) {
    return DepanFxNodeList.class.isAssignableFrom(document.getClass());
  }

  @Override
  public boolean acceptsExt(String extText) {
    return EXTENSION.equalsIgnoreCase(extText);
  }

  @Override
  public DocumentXmlPersist getDocumentPersist() {
    DocumentXmlPersistBuilder builder = new DocumentXmlPersistBuilder();

    builder.setXStream();
    builder.setNoReferences();
    builder.addConverter(new NodeListConverter());

    // Apply plugins for document elements.
    graphNodeRegistry.applyExtensions(builder, GraphNode.class);
    graphNodeRegistry.applyExtensions(builder, DepanFxWorkspaceResource.class);
    return builder.buildDocumentXmlPersist();
  }
}
