module depanfx.session {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires net.rgielen.fxweaver.core;

    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;

    requires org.slf4j;

    requires depanfx.perspective;
    requires depanfx.scene;
    requires depanfx.workspace;
    requires spring.beans;

    opens com.pnambic.depanfx.session to spring.core, spring.beans;

    exports com.pnambic.depanfx.session;
}
