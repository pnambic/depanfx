module depanfx.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;

    requires net.rgielen.fxweaver.core;
    requires org.slf4j;

    requires depanfx.scene;

    // For Spring @Component discovery
    requires depanfx.workspace.gui;
    requires depanfx.filesystem.gui;

    opens com.pnambic.depanfx to spring.core;
    opens com.pnambic.depanfx.app to spring.core;

    exports com.pnambic.depanfx.app;
    exports com.pnambic.depanfx;
}
