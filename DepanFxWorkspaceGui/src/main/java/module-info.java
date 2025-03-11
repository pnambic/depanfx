module depanfx.workspace.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires net.rgielen.fxweaver.core;

    requires org.slf4j;
    requires spring.context;
    requires spring.beans;

    requires pnambic.modxstream;

    requires depanfx.base;
    requires depanfx.graph_doc;
    requires depanfx.perspective;
    requires depanfx.scene;
    requires depanfx.session.data;
    requires depanfx.workspace;
    requires depanfx.persistence;

    opens com.pnambic.depanfx.workspace.gui 
        to javafx.fxml, net.rgielen.fxweaver.core, spring.beans, spring.core;
    opens com.pnambic.depanfx.workspace.viewdata;

    exports com.pnambic.depanfx.workspace.gui;
}
