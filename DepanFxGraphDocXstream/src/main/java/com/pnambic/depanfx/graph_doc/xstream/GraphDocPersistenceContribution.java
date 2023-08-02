package com.pnambic.depanfx.graph_doc.xstream;

import org.springframework.stereotype.Component;

import com.pnambic.depanfx.graph_doc.model.GraphDocument;
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

  public GraphDocPersistenceContribution() {
    System.out.println("GraphDocPersistenceContribution discovered");
  }

  @Override
  public boolean acceptsDocument(Object document) {
    return GraphDocument.class.isAssignableFrom(document.getClass());
  }

  @Override
  public DocumentXmlPersist getDocumentPersist(Object document) {
    DocumentXmlPersistBuilder builder = new DocumentXmlPersistBuilder();
    builder.setXStream();
    builder.setNoReferences();
    builder.addConverter(new ContextModelIdConverter());
    builder.addConverter(new GraphDocumentConverter());
    builder.addConverter(new GraphEdgeConverter());
    builder.addConverter(new GraphModelConverter());
    builder.addConverter(new GraphNodeConverter());
    builder.addConverter(new GraphRelationConverter());
    return builder.buildDocumentXmlPersist();
  }
}
