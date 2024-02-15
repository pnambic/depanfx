module depanfx.graph_doc {
  requires org.slf4j;

  requires spring.context;
  requires spring.beans;

  requires depanfx.graph;
  requires depanfx.persistence;
  requires depanfx.workspace;

  opens com.pnambic.depanfx.graph_doc.persistence to spring.core;
  opens com.pnambic.depanfx.graph_doc.docdata;

  exports com.pnambic.depanfx.graph_doc.builder;
  exports com.pnambic.depanfx.graph_doc.docdata;
  exports com.pnambic.depanfx.graph_doc.model;
  exports com.pnambic.depanfx.graph_doc.persistence;
}
