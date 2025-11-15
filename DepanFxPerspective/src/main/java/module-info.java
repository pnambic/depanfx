module depanfx.perspective {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires net.rgielen.fxweaver.core;

    requires com.google.common;
    requires org.slf4j;
    requires spring.context;
    requires spring.beans;
    requires depanfx.tasks;
    requires depanfx.graph_doc;
    requires depanfx.workspace;
    requires depanfx.scene;
    requires depanfx.base;

    opens com.pnambic.depanfx.perspective
        to javafx.fxml, net.rgielen.fxweaver.core, spring.core;
    opens com.pnambic.depanfx.perspective.chooser
        to javafx.fxml, net.rgielen.fxweaver.core, spring.beans;
    opens com.pnambic.depanfx.perspective.workspace.controls
        to javafx.fxml, depanfx.scene;
    opens com.pnambic.depanfx.perspective.plugins
        to javafx.fxml, net.rgielen.fxweaver.core, spring.beans;
    opens com.pnambic.depanfx.workspace.tasks
        to spring.beans, spring.context;

    exports com.pnambic.depanfx.perspective;
    exports com.pnambic.depanfx.perspective.chooser;
    exports com.pnambic.depanfx.perspective.graphdoc;
    exports com.pnambic.depanfx.perspective.plugins;
    exports com.pnambic.depanfx.perspective.workspace.controls;
    exports com.pnambic.depanfx.workspace.tasks;
}
