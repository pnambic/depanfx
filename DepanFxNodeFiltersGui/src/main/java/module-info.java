module depanfx.nodefilters.gui {
  requires javafx.controls;
  requires javafx.fxml;
  requires transitive javafx.graphics;
  requires net.rgielen.fxweaver.core;

  requires com.google.common;
  requires org.slf4j;
  requires spring.beans;
  requires spring.context;

  requires javafx.base;
  requires depanfx.base;
  requires depanfx.graph;
  requires depanfx.graph_doc;
  requires depanfx.graph.info;
  requires depanfx.nodefilters;
  requires depanfx.persistence;
  requires depanfx.perspective;
  requires depanfx.scene;
  requires depanfx.workspace;

  opens com.pnambic.depanfx.nodefilters.gui
      to javafx.fxml, net.rgielen.fxweaver.core, spring.beans, spring.core;

  exports com.pnambic.depanfx.nodefilters.gui;
}
