module depanfx.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires net.rgielen.fxweaver.core;

    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;

    requires org.slf4j;

    requires depanfx.scene;
    requires depanfx.session;
    requires depanfx.session.data;

    // For Spring @Component discovery
    requires depanfx.filesystem.gui;
    requires depanfx.filesystem.nodeview;
    requires depanfx.git;
    requires depanfx.git.gui;
    requires depanfx.graph_doc;
    requires depanfx.java;
    requires depanfx.java.gui;
    requires depanfx.java.nodeview;
    requires depanfx.nodelist.gui;
    requires depanfx.nodelist.viewer;
    requires depanfx.nodeview.gui;
    requires depanfx.perspective;
    requires depanfx.persistence;
    requires depanfx.workspace.gui;
    requires depanfx.workspace;
    requires spring.beans;

    opens com.pnambic.depanfx to spring.core;
    opens com.pnambic.depanfx.app to spring.core;

    exports com.pnambic.depanfx;
    exports com.pnambic.depanfx.app;
}
