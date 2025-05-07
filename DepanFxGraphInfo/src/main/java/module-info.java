module depanfx.graph.info {
  requires org.slf4j;

  requires spring.context;
  requires spring.beans;

  requires depanfx.base;
  requires depanfx.graph;

  opens com.pnambic.depanfx.graph.nodeanno;
  opens com.pnambic.depanfx.graph.nodeinfo;

  exports com.pnambic.depanfx.graph.info;
  exports com.pnambic.depanfx.graph.nodeanno;
  exports com.pnambic.depanfx.graph.nodeinfo;
}
