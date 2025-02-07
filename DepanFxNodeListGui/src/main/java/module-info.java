module depanfx.nodelist.gui {
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

  requires depanfx.graph;
  requires depanfx.graph_doc;
  requires depanfx.nodelist;
  requires depanfx.persistence;
  requires depanfx.perspective;
  requires depanfx.scene;
  requires depanfx.workspace;

  opens com.pnambic.depanfx.nodelist.gui
      to javafx.fxml, net.rgielen.fxweaver.core, spring.beans, spring.core;
  opens com.pnambic.depanfx.nodelist.gui.columns
      to javafx.fxml, net.rgielen.fxweaver.core, spring.beans, spring.core;
  opens com.pnambic.depanfx.nodelist.gui.sections
      to javafx.fxml, net.rgielen.fxweaver.core, spring.beans, spring.core;
  opens com.pnambic.depanfx.nodelist.gui.persistence to spring.beans;
  opens com.pnambic.depanfx.nodelist.gui.tooldata;

  exports com.pnambic.depanfx.nodelist.gui;
  exports com.pnambic.depanfx.nodelist.gui.columns;
  exports com.pnambic.depanfx.nodelist.gui.link;
  exports com.pnambic.depanfx.nodelist.gui.sections;
  exports com.pnambic.depanfx.nodelist.gui.tooldata;
}
