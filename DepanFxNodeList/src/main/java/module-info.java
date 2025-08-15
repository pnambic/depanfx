module depanfx.nodelist {
  requires com.google.common;
  requires org.slf4j;
  requires spring.context;
  requires spring.beans;

  requires depanfx.base;
  requires depanfx.edgematchers;
  requires depanfx.graph;
  requires depanfx.graph_doc;
  requires depanfx.graph.info;
  requires pnambic.modxstream;
  requires depanfx.nodefilters;
  requires depanfx.persistence;
  requires depanfx.workspace;

  opens com.pnambic.depanfx.nodelist.annos to spring.core;
  opens com.pnambic.depanfx.nodelist.nodefilters to spring.core;
  opens com.pnambic.depanfx.nodelist.persistence
      to spring.beans, spring.context, spring.core;
  opens com.pnambic.depanfx.nodelist.tooldata;

  exports com.pnambic.depanfx.nodelist.annos;
  exports com.pnambic.depanfx.nodelist.edgematchers;
  exports com.pnambic.depanfx.nodelist.nodefilters;
  exports com.pnambic.depanfx.nodelist.model;
  exports com.pnambic.depanfx.nodelist.tooldata;
  exports com.pnambic.depanfx.nodelist.tree;
}
