module depanfx.session {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires net.rgielen.fxweaver.core;

    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;

    requires org.slf4j;

    requires depanfx.perspective;
    requires depanfx.persistence;
    requires depanfx.session.data;
    requires depanfx.scene;
    requires depanfx.workspace;

    opens com.pnambic.depanfx.session.core to
        spring.core, spring.beans;
    opens com.pnambic.depanfx.session.gui to
        javafx.fxml, net.rgielen.fxweaver.core,
        spring.core, spring.beans;
    opens com.pnambic.depanfx.session.tooldata;

    exports com.pnambic.depanfx.session.core;
    exports com.pnambic.depanfx.session.gui;
    exports com.pnambic.depanfx.session.tooldata;
}
