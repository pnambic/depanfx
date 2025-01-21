module depanfx.nodeview.gui {
  requires java.desktop;
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
  requires depanfx.nodefilters;
  requires depanfx.nodefilters.gui;
  requires depanfx.nodelist;
  requires depanfx.nodelist.gui;
  requires depanfx.nodeview.data;
  requires depanfx.persistence;
  requires depanfx.perspective;
  requires depanfx.scene;
  requires depanfx.session.data;
  requires depanfx.workspace;

  opens com.pnambic.depanfx.nodeview.gui
      to javafx.fxml, net.rgielen.fxweaver.core,
         spring.beans, spring.core;
  opens com.pnambic.depanfx.nodeview.layouts
      to javafx.fxml, net.rgielen.fxweaver.core,
         spring.beans, spring.context, spring.core;
  opens com.pnambic.depanfx.nodeview.viewdata;

  exports com.pnambic.depanfx.nodeview.gui;
  exports com.pnambic.depanfx.nodeview.layouts;
}
