package com.pnambic.depanfx.graph_doc.xstream;

import org.springframework.stereotype.Component;

import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.graph_doc.xstream.plugins.GraphDocPluginRegistry;
import com.pnambic.depanfx.persistence.DocumentXmlPersist;
import com.pnambic.depanfx.persistence.builder.DocumentXmlPersistBuilder;
import com.pnambic.depanfx.persistence.plugins.DocumentPersistenceContribution;

@Component
public class GraphDocPersistenceContribution implements DocumentPersistenceContribution {

  /**
   * Standard extension to use when loading or saving {@code ViewDocument}s.
   * The characters represent "DepAn Graph Info".
   */
  public static final String EXTENSION = "dgi";

  public static final String GRAPH_DOC_TAG = "graph-doc";

  private final GraphDocPluginRegistry pluginRegistry;

  public GraphDocPersistenceContribution(GraphDocPluginRegistry pluginRegistry) {
    System.out.println("GraphDocPersistenceContribution discovered");
    this.pluginRegistry = pluginRegistry;
  }

  @Override
  public boolean acceptsDocument(Object document) {
    return GraphDocument.class.isAssignableFrom(document.getClass());
  }

  @Override
  public DocumentXmlPersist getDocumentPersist(Object document) {
    GraphDocument graphDoc = (GraphDocument) document;
    DocumentXmlPersistBuilder builder = new DocumentXmlPersistBuilder();
    builder.setXStream();
    builder.setNoReferences();
    builder.addConverter(new GraphDocumentConverter());
    builder.addConverter(new GraphEdgeConverter());
    builder.addConverter(new GraphModelConverter());
    builder.addConverter(new GraphNodeConverter());
    builder.addConverter(new GraphRelationConverter());
    configContext(builder, graphDoc);
    return builder.buildDocumentXmlPersist();
  }

  private void configContext(
      DocumentXmlPersistBuilder builder, GraphDocument graphDoc) {
    pluginRegistry.applyExtensions(builder);
  }
}
