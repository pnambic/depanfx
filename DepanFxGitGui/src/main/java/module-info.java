module depanfx.git.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.slf4j;

    requires com.google.common;
    requires spring.context;
    requires spring.beans;
    requires net.rgielen.fxweaver.core;

    requires depanfx.base;
    requires depanfx.git;
    requires depanfx.filesystem;
    requires depanfx.graph;
    requires depanfx.graph_doc;
    requires depanfx.perspective;
    requires depanfx.scene;
    requires depanfx.workspace;

    opens com.pnambic.depanfx.git.gui
      to javafx.fxml, net.rgielen.fxweaver.core, spring.beans, spring.core;

    exports com.pnambic.depanfx.git.gui;
}
