package com.pnambic.depanfx.app;

import com.pnambic.depanfx.DepanFxApplication;
import com.pnambic.depanfx.scene.DepanFxAppIcons;
import com.pnambic.depanfx.scene.DepanFxSceneController;

import net.rgielen.fxweaver.core.FxWeaver;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.Closeable;
import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DepanFxApp extends Application implements Closeable {

  private ConfigurableApplicationContext applicationContext;

  /** Get a reusable one up front. */
  private FxWeaver fxWeaver;

  @Override
  public void init() {
    String[] args = getParameters().getRaw().toArray(new String[0]);

    this.applicationContext = new SpringApplicationBuilder()
        // .main(getClass())
        .sources(DepanFxApplication.class)
        .run(args);

    this.fxWeaver = applicationContext.getBean(FxWeaver.class);
  }

  @Override
  public void start(Stage stage) throws Exception {
    Scene scene = DepanFxSceneController.createDepanScene(fxWeaver, this);

    stage.setTitle("DepanFX");
    DepanFxAppIcons.installDepanIcons(stage.getIcons());
    stage.setScene(scene);
    stage.show();
  }

  @Override
  public void stop() {
    applicationContext.close();
    Platform.exit();
  }

  @Override
  public void close() throws IOException {
    stop();
  }
}
