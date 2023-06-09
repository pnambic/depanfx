module depanfx.scene {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    requires spring.context;
    requires spring.beans;

    requires net.rgielen.fxweaver.core;

    opens com.pnambic.depanfx.scene to javafx.fxml, net.rgielen.fxweaver.core, spring.beans;

    exports com.pnambic.depanfx.scene;
    exports com.pnambic.depanfx.scene.plugins;
}
