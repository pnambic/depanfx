module depanfx.workspace {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.pnambic.depanfx.workspace.gui to javafx.fxml;

    exports com.pnambic.depanfx.workspace;
    exports com.pnambic.depanfx.workspace.gui;
}
