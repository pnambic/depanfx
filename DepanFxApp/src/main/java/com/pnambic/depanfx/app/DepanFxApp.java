/*
 * Copyright 2023 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pnambic.depanfx.app;

import com.pnambic.depanfx.DepanFxApplication;
import com.pnambic.depanfx.session.core.DepanFxSession;
import com.pnambic.depanfx.session.core.DepanFxSessionCliArgs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class DepanFxApp extends Application {

  private ConfigurableApplicationContext applicationContext;

  private DepanFxSession session;

  @Override
  public void init() throws Exception {
    // Explicitly just the inherited behaviors.
    // Initializing Spring during init() leads to problems on MacOS Java
    // like not being able to use String types in FX dialogs:
    //
    // Error messages like:
    // Caused by: java.lang.NullPointerException: Cannot invoke
    // "java.lang.ClassLoader.loadClass(String)" because the return value of
    // "javafx.fxml.FXMLLoader.getClassLoader()" is null.
    //
    // As recommended, don't initialize Spring in init().
    super.init();
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
  @Override
  public void start(Stage stage) throws Exception {
    Platform.setImplicitExit(true);

    // Start Spring dependency injection framework (after init()).
    String[] args = getParameters().getRaw().toArray(new String[0]);
    this.applicationContext = new SpringApplicationBuilder()
        .sources(DepanFxApplication.class)
        .run(args);

    // Configure and run the session.
    this.session = applicationContext.getBean(DepanFxSession.class);
    session.setOnClose(applicationContext::close);

    DepanFxSessionCliArgs sessionArgs =
        applicationContext.getBean(DepanFxSessionCliArgs.class);
    session.setSessionConfig(sessionArgs.getSessionConfig());
    sessionArgs.getSessionPath()
        .ifPresent(session::setSessionPath);

    session.startSession(stage);
  }

  @Override
  public void stop() {
    session.stopSession();
    applicationContext.close();
    // Stop is called by Platform.exit();
  }
}
