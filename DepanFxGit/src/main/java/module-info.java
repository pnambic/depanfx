module depanfx.git {
  requires com.google.common;
  requires org.slf4j;
  requires spring.context;

  requires depanfx.graph;
  requires depanfx.filesystem;
  requires depanfx.graph_doc;
  requires depanfx.nodelist;
  requires depanfx.persistence;
  requires depanfx.workspace;

  opens com.pnambic.depanfx.git.tooldata;

  exports com.pnambic.depanfx.git.builder;
  exports com.pnambic.depanfx.git.tooldata;
}
