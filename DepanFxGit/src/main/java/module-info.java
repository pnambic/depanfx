module depanfx.git {
  requires com.google.common;
  requires org.slf4j;

  requires depanfx.graph;
  requires depanfx.nodelist;
  requires depanfx.filesystem;
  requires depanfx.graph_doc;
  requires depanfx.workspace;

  exports com.pnambic.depanfx.git.builder;
}
