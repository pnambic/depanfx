module depanfx.persistence {

  requires spring.boot;
  requires spring.boot.autoconfigure;
  requires spring.context;

  requires depanfx.xstream;

  exports com.pnambic.depanfx.persistence;
  exports com.pnambic.depanfx.persistence.plugins;
}
