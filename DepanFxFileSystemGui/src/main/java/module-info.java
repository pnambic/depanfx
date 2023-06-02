module depanfx.filesystem.gui {
    requires javafx.controls;
    requires javafx.fxml;

    requires spring.context;
    requires spring.beans;

    requires depanfx.scene;

    opens com.pnambic.depanfx.filesystem.gui to javafx.fxml, spring.beans;

    exports com.pnambic.depanfx.filesystem.gui;
}
