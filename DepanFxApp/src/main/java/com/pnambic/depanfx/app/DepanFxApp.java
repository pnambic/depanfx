package com.pnambic.depanfx.app;

import java.io.Closeable;
import java.io.IOException;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.pnambic.depanfx.DepanFxApplication;
import com.pnambic.depanfx.scene.DepanFxSceneController;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;

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

    stage.setTitle("DepanFX w/ JavaFX and Gradle");
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
