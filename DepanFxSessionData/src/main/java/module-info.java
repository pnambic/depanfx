module depanfx.session.data {

    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;

    requires org.slf4j;

    requires depanfx.persistence;
    requires depanfx.perspective;
    requires depanfx.scene;

    opens com.pnambic.depanfx.session.viewdata;

    exports com.pnambic.depanfx.session.plugins;
    exports com.pnambic.depanfx.session.viewdata;
}
