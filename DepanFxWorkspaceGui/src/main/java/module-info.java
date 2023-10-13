module depanfx.workspace.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires net.rgielen.fxweaver.core;

    requires org.slf4j;
    requires spring.context;
    requires spring.beans;

    // requires depanfx.graph;
    requires depanfx.graph_doc;
    requires depanfx.nodelist;
    requires depanfx.nodelist.gui;
    requires depanfx.scene;
    requires depanfx.workspace;

    opens com.pnambic.depanfx.workspace.gui to javafx.fxml, net.rgielen.fxweaver.core, spring.beans;

    exports com.pnambic.depanfx.workspace.gui;
}
