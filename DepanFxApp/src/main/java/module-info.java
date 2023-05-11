module depanfx.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    requires depanfx.workspace;

    opens com.pnambic.depanfx.app to javafx.fxml;
    exports com.pnambic.depanfx.app;
}
