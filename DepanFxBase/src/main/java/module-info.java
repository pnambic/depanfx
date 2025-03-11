module depanfx.base {
  requires org.slf4j;

  requires spring.context;
  requires spring.beans;

  opens com.pnambic.depanfx.base.tooldata;

  exports com.pnambic.depanfx.base;
  exports com.pnambic.depanfx.base.tooldata;
}
