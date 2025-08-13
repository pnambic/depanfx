module depanfx.edgematchers {
  requires org.slf4j;
  requires spring.context;
  requires spring.beans;

  requires depanfx.base;
  requires depanfx.graph;
  requires pnambic.modxstream;
  requires depanfx.persistence;
  requires depanfx.workspace;

  opens com.pnambic.depanfx.edgematchers.link to spring.beans, spring.core;
  opens com.pnambic.depanfx.edgematchers.tooldata;

  exports com.pnambic.depanfx.edgematchers.link;
  exports com.pnambic.depanfx.edgematchers.tooldata;
}
