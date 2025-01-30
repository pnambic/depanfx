module depanfx.persistence {

  requires spring.boot;
  requires spring.boot.autoconfigure;
  requires spring.context;
  requires spring.beans;
  requires org.slf4j;

  requires pnambic.modxstream;

  exports com.pnambic.depanfx.persistence;
  exports com.pnambic.depanfx.persistence.plugins;
}
