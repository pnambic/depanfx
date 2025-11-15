module depanfx.app {
    requires net.rgielen.fxweaver.core;

    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;

    requires org.slf4j;

    requires depanfx.scene;
    requires depanfx.session;

    // For Spring @Component discovery
    requires depanfx.filesystem.gui;
    requires depanfx.filesystem.nodeview;
    requires depanfx.git.gui;
    requires depanfx.java.gui;
    requires depanfx.java.nodeview;
    requires depanfx.nodelist.viewer;
    requires depanfx.nodeview.gui;
    requires depanfx.persistence;
    requires depanfx.workspace.gui;
    requires depanfx.tasks;

    opens com.pnambic.depanfx to spring.core;
    opens com.pnambic.depanfx.app to spring.core;

    exports com.pnambic.depanfx;
    exports com.pnambic.depanfx.app;
}
