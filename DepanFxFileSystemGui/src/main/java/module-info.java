module depanfx.filesystem.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;

    requires spring.context;
    requires spring.beans;
    requires net.rgielen.fxweaver.core;

    requires depanfx.scene;
    requires depanfx.filesystem;
    requires depanfx.graph_doc;
    requires depanfx.workspace;

    opens com.pnambic.depanfx.filesystem.gui to javafx.fxml, net.rgielen.fxweaver.core, spring.beans;

    exports com.pnambic.depanfx.filesystem.gui;
}
