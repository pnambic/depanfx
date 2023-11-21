module depanfx.git {
  requires com.google.common;
  requires org.slf4j;

  requires depanfx.graph;
  requires depanfx.nodelist;
  requires depanfx.filesystem;
  requires depanfx.graph_doc;
  requires depanfx.persistence;
  requires depanfx.workspace;
  requires spring.context;

  opens com.pnambic.depanfx.git.tooldata;

  exports com.pnambic.depanfx.git.builder;
  exports com.pnambic.depanfx.git.tooldata;
}
