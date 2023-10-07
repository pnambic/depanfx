module depanfx.scene {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires net.rgielen.fxweaver.core;

    requires org.slf4j;
    requires spring.context;
    requires spring.beans;

    opens com.pnambic.depanfx.scene to javafx.fxml, net.rgielen.fxweaver.core, spring.beans;

    exports com.pnambic.depanfx.scene;
    exports com.pnambic.depanfx.scene.plugins;
}
