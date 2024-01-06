package com.pnambic.depanfx.graph_doc.xstream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.persistence.DocumentXmlPersist;
import com.pnambic.depanfx.persistence.builder.DocumentXmlPersistBuilder;
import com.pnambic.depanfx.persistence.plugins.DocumentPersistenceContribution;
import com.pnambic.depanfx.persistence.plugins.GraphNodePersistencePluginRegistry;

@Component
public class GraphDocPersistenceContribution
    implements DocumentPersistenceContribution {

  /**
   * Standard extension to use when loading or saving {@code ViewDocument}s.
   * The characters represent "DepAn Graph Info".
   */
  public static final String EXTENSION = "dgi";

  public static final String GRAPH_DOC_TAG = "graph-doc";

  private static final Class<?>[] ALLOWED_INFO_BLOCKS = new Class[] {
    EdgeInfoBlock.class, NodeInfoBlock.class
  };

  private final GraphNodePersistencePluginRegistry graphNodeRegistry;

  @Autowired
  public GraphDocPersistenceContribution(
      GraphNodePersistencePluginRegistry graphNodeRegistry) {
    this.graphNodeRegistry = graphNodeRegistry;
  }

  @Override
  public boolean acceptsDocument(Object document) {
    return GraphDocument.class.isAssignableFrom(document.getClass());
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
    builder.addConverter(new ContextModelIdConverter());
    builder.addConverter(new GraphDocumentConverter());
    builder.addConverter(new GraphEdgeConverter());
    builder.addConverter(new GraphModelConverter());
    builder.addConverter(new GraphNodeConverter());

    // XStream handled conversions
    builder.addAlias("edge-info", EdgeInfoBlock.class);
    builder.addImplicitCollection(EdgeInfoBlock.class, "infos");
    builder.addAlias("node-info", NodeInfoBlock.class);
    builder.addImplicitCollection(NodeInfoBlock.class, "infos");
    builder.addAllowedType(ALLOWED_INFO_BLOCKS);

    GraphRelationConverter relationConverter = new GraphRelationConverter();
    builder.addAliasType(
        relationConverter.getTag(), relationConverter.forType());
    builder.addConverter(relationConverter);

    // Apply plugins for graph nodes.
    graphNodeRegistry.applyExtensions(builder, GraphNode.class);
    return builder.buildDocumentXmlPersist();
  }
}
