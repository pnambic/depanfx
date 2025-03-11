module depanfx.nodeview.data {
  requires javafx.base;
  requires javafx.graphics;  // For color definitions

  requires com.google.common;
  requires org.slf4j;
  requires spring.context;
  requires spring.beans;

  requires depanfx.graph;
  requires depanfx.graph_doc;
  requires depanfx.graph.info;
  requires depanfx.nodefilters;
  requires depanfx.nodelist;
  requires depanfx.persistence;
  requires depanfx.session.data;
  requires depanfx.workspace;
  requires pnambic.modxstream;
  requires depanfx.base;

  opens com.pnambic.depanfx.nodeview.builtins
      to spring.beans, spring.context, spring.core;

  opens com.pnambic.depanfx.nodeview.tooldata;

  exports com.pnambic.depanfx.nodeview.builtins;
  exports com.pnambic.depanfx.nodeview.persistence;
  exports com.pnambic.depanfx.nodeview.tooldata;
}
