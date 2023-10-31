module depanfx.workspace {
    requires spring.context;
    requires spring.beans;
    requires org.slf4j;

    requires depanfx.graph;
    requires depanfx.graph_doc;
    requires depanfx.persistence;
    requires depanfx.platform;

    // Instantiate a workspace
    opens com.pnambic.depanfx.workspace.basic to spring.beans;

    exports com.pnambic.depanfx.workspace;
    exports com.pnambic.depanfx.workspace.documents;
    exports com.pnambic.depanfx.workspace.projects;
}
