module depanfx.platform {
    requires spring.context;
    requires spring.beans;

    requires depanfx.graph;

    exports com.pnambic.depanfx.graph.context.plugins;
}
