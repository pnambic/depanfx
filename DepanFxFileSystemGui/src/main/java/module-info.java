module depanfx.filesystem.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires net.rgielen.fxweaver.core;

    requires com.google.common;
    requires org.slf4j;
    requires spring.context;
    requires spring.beans;

    requires depanfx.edgematchers;
    requires depanfx.edgematchers.gui;
    requires depanfx.filesystem;
    requires depanfx.graph;
    requires depanfx.graph_doc;
    requires depanfx.nodelist;
    requires depanfx.nodelist.gui;
    requires depanfx.nodeview.data;
    requires depanfx.perspective;
    requires depanfx.workspace;
    requires depanfx.scene;

    opens com.pnambic.depanfx.filesystem.gui
      to javafx.fxml, net.rgielen.fxweaver.core,
         spring.beans, spring.core;

    exports com.pnambic.depanfx.filesystem.gui;
}
