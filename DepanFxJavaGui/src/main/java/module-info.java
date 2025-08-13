module depanfx.java.gui {
  requires com.google.common;
  requires javafx.controls;
  requires javafx.fxml;
  requires net.rgielen.fxweaver.core;
  requires org.slf4j;
  requires spring.beans;
  requires spring.context;

  requires depanfx.base;
  requires depanfx.bytecode;
  requires depanfx.filesystem;
  requires depanfx.graph;
  requires depanfx.graph_doc;
  requires depanfx.java;
  requires depanfx.edgematchers;
  requires depanfx.edgematchers.gui;
  requires depanfx.nodelist;
  requires depanfx.nodelist.gui;
  requires depanfx.perspective;
  requires depanfx.workspace;
  requires depanfx.scene;
  requires depanfx.nodefilters;

  opens com.pnambic.depanfx.java.gui
    to javafx.fxml, net.rgielen.fxweaver.core,
      spring.core, spring.beans;

  exports com.pnambic.depanfx.java.gui;
}
