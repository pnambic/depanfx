module depanfx.nodefilters.gui {
  requires javafx.controls;
  requires javafx.fxml;
  requires transitive javafx.graphics;
  requires net.rgielen.fxweaver.core;

  requires com.google.common;
  requires org.slf4j;
  requires spring.context;
  requires spring.beans;

  requires depanfx.graph;
  requires depanfx.nodefilters;
  requires depanfx.nodelist;
  requires depanfx.nodelist.gui;
  requires depanfx.persistence;
  requires depanfx.perspective;
  requires depanfx.workspace;
  requires depanfx.graph_doc;
  requires depanfx.scene;
  requires javafx.base;

  opens com.pnambic.depanfx.nodefilters.gui
      to javafx.fxml, net.rgielen.fxweaver.core, spring.beans, spring.core;

  exports com.pnambic.depanfx.nodefilters.gui;
}
