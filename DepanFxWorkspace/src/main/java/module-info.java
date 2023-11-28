module depanfx.workspace {
    requires spring.context;
    requires spring.beans;
    requires org.slf4j;

    requires depanfx.graph;
    requires depanfx.graph_doc;
    requires depanfx.persistence;
    requires depanfx.platform;
    requires com.google.common;

    // Instantiate a workspace
    opens com.pnambic.depanfx.workspace.basic to spring.beans;
    opens com.pnambic.depanfx.workspace.projects to spring.core;

    // For XStream persistence
    opens com.pnambic.depanfx.workspace.tooldata;

    exports com.pnambic.depanfx.workspace;
    exports com.pnambic.depanfx.workspace.documents;
    exports com.pnambic.depanfx.workspace.projects;
    exports com.pnambic.depanfx.workspace.tooldata;
}
