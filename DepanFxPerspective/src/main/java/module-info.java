module depanfx.perspective {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires net.rgielen.fxweaver.core;

    requires org.slf4j;
    requires spring.context;
    requires spring.beans;
    requires depanfx.workspace;
    requires depanfx.scene;

    opens com.pnambic.depanfx.perspective to spring.core;
    opens com.pnambic.depanfx.perspective.plugins to javafx.fxml, net.rgielen.fxweaver.core, spring.beans;

    exports com.pnambic.depanfx.perspective;
    exports com.pnambic.depanfx.perspective.plugins;
}
