module depanfx.graph.info {
  requires org.slf4j;

  requires spring.context;
  requires spring.beans;

  requires depanfx.base;
  requires depanfx.graph;

  exports com.pnambic.depanfx.nodeinfo;
}
