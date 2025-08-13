module depanfx.java.nodeview {
  requires javafx.graphics;  // For color definitions

  requires com.google.common;
  requires org.slf4j;
  requires spring.beans;
  requires spring.context;

  requires depanfx.base;
  requires depanfx.edgematchers;
  requires depanfx.graph;
  requires depanfx.graph.info;
  requires depanfx.nodefilters;
  requires depanfx.nodelist;
  requires depanfx.filesystem;
  requires depanfx.graph_doc;
  requires depanfx.java;
  requires depanfx.nodeview.data;
  requires depanfx.persistence;
  requires depanfx.workspace;

  opens com.pnambic.depanfx.java.nodeview
      to spring.beans, spring.context, spring.core;
}
