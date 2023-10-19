module depanfx.nodelist {
  requires org.slf4j;
  requires spring.context;
  requires spring.beans;
  requires com.google.common;

  requires depanfx.graph;
  requires depanfx.graph_doc;
  requires depanfx.workspace;

  exports com.pnambic.depanfx.nodelist.link;
  exports com.pnambic.depanfx.nodelist.model;
  exports com.pnambic.depanfx.nodelist.tree;
}
