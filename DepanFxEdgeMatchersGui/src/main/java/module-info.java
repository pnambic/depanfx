module depanfx.edgematchers.gui {
  requires javafx.base;
  requires javafx.controls;
  requires javafx.fxml;
  requires transitive javafx.graphics;
  requires net.rgielen.fxweaver.core;

  requires org.slf4j;
  requires spring.context;
  requires spring.beans;
  requires pnambic.modxstream;

  requires depanfx.edgematchers;
  requires depanfx.perspective;
  requires depanfx.scene;
  requires depanfx.workspace;
  requires depanfx.base;

  opens com.pnambic.depanfx.edgematchers.gui
      to javafx.fxml, net.rgielen.fxweaver.core, spring.beans, spring.core;

  exports com.pnambic.depanfx.edgematchers.gui;
}
