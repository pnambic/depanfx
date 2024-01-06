module depanfx.java {
  requires com.google.common;
  requires org.slf4j;

  requires spring.context;
  requires spring.beans;

  requires depanfx.filesystem;
  requires depanfx.graph;
  requires depanfx.nodelist;
  requires depanfx.graph_doc;
  requires depanfx.workspace;

  opens com.pnambic.depanfx.java.graph to spring.core;
  opens com.pnambic.depanfx.java.nodelist.link to spring.core;
  opens com.pnambic.depanfx.java.graph.graphdata;  // for UNNAMED module access, i.e. XStream.

  exports com.pnambic.depanfx.java.context;
  exports com.pnambic.depanfx.java.graph;
  exports com.pnambic.depanfx.java.graph.graphdata;
  exports com.pnambic.depanfx.java.nodelist.link;
}
