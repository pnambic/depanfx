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

  exports com.pnambic.depanfx.nodelist.gui;
}
