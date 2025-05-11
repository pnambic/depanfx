module depanfx.nodelist {
  requires com.google.common;
  requires org.slf4j;
  requires spring.context;
  requires spring.beans;

  requires depanfx.graph;
  requires depanfx.graph_doc;
  requires depanfx.graph.info;
  requires depanfx.persistence;
  requires depanfx.workspace;
  requires pnambic.modxstream;
  requires depanfx.base;

  opens com.pnambic.depanfx.nodelist.link to spring.beans, spring.core;
  opens com.pnambic.depanfx.nodelist.persistence to spring.beans, spring.context, spring.core;
  opens com.pnambic.depanfx.nodelist.tooldata;

  exports com.pnambic.depanfx.nodelist.link;
  exports com.pnambic.depanfx.nodelist.model;
  exports com.pnambic.depanfx.nodelist.tooldata;
  exports com.pnambic.depanfx.nodelist.tree;
}
