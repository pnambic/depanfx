module depanfx.filesystem.nodeview {
  requires com.google.common;
  requires javafx.graphics;  // For color definitions
  requires org.slf4j;

  requires spring.context;
  requires spring.beans;

  requires depanfx.base;
  requires depanfx.graph;
  requires depanfx.graph.info;
  requires depanfx.nodelist;
  requires depanfx.filesystem;
  requires depanfx.graph_doc;
  requires depanfx.nodeview.data;
  requires depanfx.persistence;
  requires depanfx.workspace;
  requires depanfx.nodefilters;
  requires depanfx.edgematchers;

  opens com.pnambic.depanfx.filesystem.nodeview
      to spring.beans, spring.context, spring.core;
}
