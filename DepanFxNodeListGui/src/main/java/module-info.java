module depanfx.nodelist.gui {
  requires javafx.base;
  requires javafx.controls;
  requires javafx.fxml;
  requires transitive javafx.graphics;
  requires net.rgielen.fxweaver.core;

  requires com.google.common;
  requires org.apache.commons.csv;
  requires org.slf4j;
  requires spring.beans;
  requires spring.context;

  requires pnambic.modxstream;

  requires depanfx.base;
  requires depanfx.edgematchers;
  requires depanfx.edgematchers.gui;
  requires depanfx.graph;
  requires depanfx.graph_doc;
  requires depanfx.graph.info;
  requires depanfx.nodefilters;
  requires depanfx.nodefilters.gui;
  requires depanfx.nodelist;
  requires depanfx.persistence;
  requires depanfx.perspective;
  requires depanfx.scene;
  requires depanfx.workspace;

  opens com.pnambic.depanfx.nodelist.gui
      to javafx.fxml, net.rgielen.fxweaver.core, spring.beans, spring.core;
  opens com.pnambic.depanfx.nodelist.gui.columns
      to javafx.fxml, net.rgielen.fxweaver.core, spring.beans, spring.core;
  opens com.pnambic.depanfx.nodelist.gui.columns.annos
      to javafx.fxml, net.rgielen.fxweaver.core,
         spring.beans, spring.context, spring.core;
  opens com.pnambic.depanfx.nodelist.gui.columns.infos
      to javafx.fxml, net.rgielen.fxweaver.core,
         spring.beans, spring.context, spring.core;
  opens com.pnambic.depanfx.nodelist.gui.edgematchers
      to javafx.fxml, net.rgielen.fxweaver.core,
         spring.beans, spring.context, spring.core;
  opens com.pnambic.depanfx.nodelist.gui.nodefilters
      to javafx.fxml, net.rgielen.fxweaver.core, spring.core;
  opens com.pnambic.depanfx.nodelist.gui.sections
      to javafx.fxml, net.rgielen.fxweaver.core, spring.beans, spring.core;
  opens com.pnambic.depanfx.nodelist.gui.sections.folds
      to javafx.fxml, net.rgielen.fxweaver.core,
         spring.beans, spring.context, spring.core;
  opens com.pnambic.depanfx.nodelist.gui.tooldata;

  exports com.pnambic.depanfx.nodelist.gui;
  exports com.pnambic.depanfx.nodelist.gui.columns;
  exports com.pnambic.depanfx.nodelist.gui.columns.annos;
  exports com.pnambic.depanfx.nodelist.gui.columns.infos;
  exports com.pnambic.depanfx.nodelist.gui.edgematchers;
  exports com.pnambic.depanfx.nodelist.gui.nodefilters;
  exports com.pnambic.depanfx.nodelist.gui.sections;
  exports com.pnambic.depanfx.nodelist.gui.sections.folds;
  exports com.pnambic.depanfx.nodelist.gui.tooldata;
}
