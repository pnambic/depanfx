module depanfx.bytecode {
  requires com.google.common;
  requires org.slf4j;

  requires spring.context;
  requires spring.core;
  requires spring.beans;

  requires depanfx.filesystem;
  requires depanfx.graph;
  requires depanfx.graph_doc;
  requires depanfx.java;

  exports com.pnambic.depanfx.bytecode;
}
