module depanfx.tasks.gui {

    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires net.rgielen.fxweaver.core;

    requires spring.beans;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.core;

    requires depanfx.scene;
    requires depanfx.base;
    requires depanfx.tasks;
    requires org.slf4j;

    opens com.pnambic.depanfx.tasks.gui
        to javafx.fxml, net.rgielen.fxweaver.core, spring.beans, spring.core;

    exports com.pnambic.depanfx.tasks.gui;
}
