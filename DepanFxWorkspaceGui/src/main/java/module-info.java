module depanfx.workspace.gui {
    requires javafx.controls;
    requires javafx.fxml;

    requires spring.context;
    requires spring.beans;

    requires depanfx.scene;
    requires depanfx.workspace;

    opens com.pnambic.depanfx.workspace.gui to javafx.fxml, spring.beans;

    exports com.pnambic.depanfx.workspace.gui;
}
