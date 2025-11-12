module depanfx.tasks {
  requires com.google.common;
  requires org.slf4j;
  requires spring.context;
  requires spring.beans;

  exports com.pnambic.depanfx.tasks;

  opens com.pnambic.depanfx.tasks.runtime to spring.core, spring.beans;
}
