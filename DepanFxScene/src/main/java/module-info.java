module depanfx.scene {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires net.rgielen.fxweaver.core;

    requires com.google.common;
    requires org.slf4j;
    requires spring.context;
    requires spring.beans;
    requires spring.core;
    requires java.desktop;
    requires javafx.base;

    requires depanfx.base;

    opens com.pnambic.depanfx.scene to
      javafx.fxml, net.rgielen.fxweaver.core, spring.beans, spring.core;

    exports com.pnambic.depanfx.scene;
    exports com.pnambic.depanfx.scene.plugins;
    exports com.pnambic.depanfx.scene.tooldata;
}
