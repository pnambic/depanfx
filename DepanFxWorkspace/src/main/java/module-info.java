module depanfx.workspace {
    requires spring.context;
    requires spring.beans;

    requires depanfx.graph;
    requires depanfx.persistence;
    requires depanfx.platform;

    opens com.pnambic.depanfx.workspace.basic to spring.beans;

    exports com.pnambic.depanfx.workspace;
}
