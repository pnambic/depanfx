module depanfx.filesystem {
  requires com.google.common;
  requires org.slf4j;

  requires spring.context;
  requires spring.beans;

  requires depanfx.graph;
  requires depanfx.graph_doc;

  opens com.pnambic.depanfx.filesystem.graph to spring.core;

  exports com.pnambic.depanfx.filesystem.builder;
  exports com.pnambic.depanfx.filesystem.context;
  exports com.pnambic.depanfx.filesystem.graph;
}
