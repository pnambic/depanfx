module depanfx.nodeview.gui {
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

  requires depanfx.graph;
  requires depanfx.graph_doc;
  requires depanfx.jogl;
  requires depanfx.nodelist;
  requires depanfx.persistence;
  requires depanfx.perspective;
  requires depanfx.workspace;
  requires depanfx.scene;

  opens com.pnambic.depanfx.nodeview.gui
      to javafx.fxml, net.rgielen.fxweaver.core, spring.beans, spring.core;
  opens com.pnambic.depanfx.nodeview.tooldata;

  exports com.pnambic.depanfx.nodeview.gui;
  exports com.pnambic.depanfx.nodeview.tooldata;
  exports com.pnambic.depanfx.nodeview.persistence;
}
