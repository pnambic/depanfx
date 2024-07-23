package com.pnambic.depanfx.app;

import com.pnambic.depanfx.DepanFxApplication;
import com.pnambic.depanfx.scene.DepanFxAppIcons;
import com.pnambic.depanfx.scene.DepanFxSceneController;

import net.rgielen.fxweaver.core.FxWeaver;

import org.springframework.boot.SpringApplication;
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
    main();
  }

  @Override
  public void start(Stage stage) throws Exception {
    Platform.setImplicitExit(true);
    Scene scene = DepanFxSceneController.createDepanScene(fxWeaver, this);

    stage.setTitle("DepanFX");
    DepanFxAppIcons.installDepanIcons(stage.getIcons());
    stage.setScene(scene);
    stage.show();
  }

  @Override // Application - Invoked from Platform.exit().
  public void stop() {
    applicationContext.close();
  }

  @Override // Closable
  public void close() throws IOException {
    Platform.exit();
  }

  /**
   * Help Spring Boot find this class and identify it as
   * the main class for the application.
   *
   * The {@link DepanFxApplication#main(String[])} doesn't get recognized,
   * 'cuz that's in a different thread.
   *
   * See {@link SpringApplication#deduceMainApplicationClass()} for details
   * regarding the discovery of the main application class.
   */
  public void main() {
    String[] args = getParameters().getRaw().toArray(new String[0]);

    this.applicationContext = new SpringApplicationBuilder()
        // .main(getClass())
        .sources(DepanFxApplication.class)
        .run(args);

    this.fxWeaver = applicationContext.getBean(FxWeaver.class);
  }
}
