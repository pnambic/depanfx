module depanfx.edgematchers {
  requires org.slf4j;
  requires spring.context;
  requires spring.beans;
  requires depanfx.graph;
  requires depanfx.workspace;
  requires depanfx.persistence;
  requires pnambic.modxstream;
  requires depanfx.base;

  opens com.pnambic.depanfx.edgematchers.link to spring.beans, spring.core;
  opens com.pnambic.depanfx.edgematchers.tooldata;

  exports com.pnambic.depanfx.edgematchers.link;
  exports com.pnambic.depanfx.edgematchers.tooldata;
}
