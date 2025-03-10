module depanfx.nodelist.viewer {
  requires javafx.base;
  requires javafx.controls;
  requires javafx.fxml;
  requires transitive javafx.graphics;
  requires net.rgielen.fxweaver.core;

  requires com.google.common;
  requires org.apache.commons.csv;
  requires org.slf4j;
  requires spring.context;
  requires spring.beans;

  requires pnambic.modxstream;

  requires depanfx.base;
  requires depanfx.graph;
  requires depanfx.graph.info;
  requires depanfx.nodelist;
  requires depanfx.nodefilters.gui;
  requires depanfx.nodelist.gui;
  requires depanfx.persistence;
  requires depanfx.perspective;
  requires depanfx.session.data;
  requires depanfx.workspace;
  requires depanfx.graph_doc;
  requires depanfx.scene;

  opens com.pnambic.depanfx.nodelist.viewdata;
  opens com.pnambic.depanfx.nodelist.viewer
      to javafx.fxml, net.rgielen.fxweaver.core, spring.beans, spring.core;

  exports com.pnambic.depanfx.nodelist.viewer;
}
