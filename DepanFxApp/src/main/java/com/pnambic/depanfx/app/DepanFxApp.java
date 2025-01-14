package com.pnambic.depanfx.app;

import com.pnambic.depanfx.DepanFxApplication;
import com.pnambic.depanfx.session.core.DepanFxSession;
import com.pnambic.depanfx.session.core.DepanFxSessionCliArgs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.Closeable;
import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class DepanFxApp extends Application implements Closeable {

  private DepanFxSession session;

  private ConfigurableApplicationContext applicationContext;

  @Override
  public void init() {
    main();
  }

  @Override
  public void start(Stage stage) throws Exception {
    Platform.setImplicitExit(true);

    session.startSession(stage);
  }

  @Override
  public void stop() {
    session.stopSession();
    applicationContext.close();
    Platform.exit();
  }

  @Override
  public void close() throws IOException {
    stop();
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

    this.session = applicationContext.getBean(DepanFxSession.class);
    session.setOnClose(applicationContext::close);

    DepanFxSessionCliArgs sessionArgs =
        applicationContext.getBean(DepanFxSessionCliArgs.class);
    session.setSessionConfig(sessionArgs.getSessionConfig());
    sessionArgs.getSessionPath()
        .ifPresent(session::setSessionPath);
  }
}
