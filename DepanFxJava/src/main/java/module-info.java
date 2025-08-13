module depanfx.java {
  requires com.google.common;
  requires org.slf4j;

  requires spring.beans;
  requires spring.context;

  requires depanfx.base;
  requires depanfx.edgematchers;
  requires depanfx.filesystem;
  requires depanfx.graph;
  requires depanfx.graph.info;
  requires depanfx.graph_doc;
  requires pnambic.modxstream;
  requires depanfx.nodefilters;
  requires depanfx.nodelist;
  requires depanfx.persistence;
  requires depanfx.workspace;

  opens com.pnambic.depanfx.java.edgematchers.link to spring.core;
  opens com.pnambic.depanfx.java.graph to spring.core;
  opens com.pnambic.depanfx.java.nodefilters to spring.core;
  opens com.pnambic.depanfx.java.persistence to spring.core;

  opens com.pnambic.depanfx.java.graph.graphdata;  // for UNNAMED module access, i.e. XStream.

  exports com.pnambic.depanfx.java.context;
  exports com.pnambic.depanfx.java.edgematchers.link;
  exports com.pnambic.depanfx.java.graph;
  exports com.pnambic.depanfx.java.graph.graphdata;
  exports com.pnambic.depanfx.java.nodefilters;
  exports com.pnambic.depanfx.java.persistence;
}
