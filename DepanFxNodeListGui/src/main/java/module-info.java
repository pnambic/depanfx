module depanfx.nodelist.gui {
  requires javafx.controls;
  requires javafx.fxml;
  requires transitive javafx.graphics;
  requires net.rgielen.fxweaver.core;

  requires com.google.common;
  requires org.slf4j;
  requires spring.context;
  requires spring.beans;

  requires depanfx.graph;
  requires depanfx.nodelist;
  requires depanfx.workspace;
  requires depanfx.graph_doc;
  requires depanfx.scene;

  opens com.pnambic.depanfx.nodelist.gui to javafx.fxml, net.rgielen.fxweaver.core, spring.beans;

  exports com.pnambic.depanfx.nodelist.gui;
}
