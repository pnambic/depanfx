module depanfx.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens com.pnambic.depanfx.app to javafx.fxml;
    exports com.pnambic.depanfx.app;
}
