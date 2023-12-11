module depanfx.git.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;

    requires spring.context;
    requires spring.beans;
    requires net.rgielen.fxweaver.core;

    requires depanfx.scene;
    requires depanfx.git;
    requires depanfx.filesystem;
    requires depanfx.graph_doc;
    requires depanfx.perspective;
    requires depanfx.workspace;
    requires javafx.graphics;

    opens com.pnambic.depanfx.git.gui to javafx.fxml, net.rgielen.fxweaver.core, spring.beans;

    exports com.pnambic.depanfx.git.gui;
}
