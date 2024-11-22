/*
 * Copyright 2024 The Depan Project Authors
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
package com.pnambic.depanfx.session.core;

import com.pnambic.depanfx.scene.DepanFxSceneViewer;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneStarterRegistry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Configure the session startup information form the command line.
 */
@Component
public class DepanFxSessionCliArgs implements ApplicationRunner {

  private final DepanFxSceneStarterRegistry starterRegistry;

  private DepanFxSessionConfig sessionConfig;

  @Autowired
  private DepanFxSessionCliArgs(DepanFxSceneStarterRegistry starterRegistry) {
    this.starterRegistry = starterRegistry;
  }

  @Override
  public void run(ApplicationArguments args) {
    if (args.containsOption("session")) {
      String sessionPath = args.getOptionValues("session").get(0);
      sessionConfig = restoreSession(sessionPath);
      if (sessionConfig == null) {
        return;
      }
      // Fall through to default.
    }
    sessionConfig = defaultSession();
  }

  private DepanFxSessionConfig restoreSession(String sessionPath) {
    return defaultSession();
    // LATER: more like this
    //if (sessionPath == null) {
    //  sessionInfo = restoreSession(sessionPath);
    //}
  }

  private DepanFxSessionConfig defaultSession() {
    List<DepanFxSceneViewer> defaultViewers =
        starterRegistry.getStarterViews();

    DepanFxSceneConfig sceneInfo = new DepanFxSceneConfig(
        "Initial Startup Scene",
        "Initial scene created at DepanFX startup.",
        defaultViewers);

    List<DepanFxSceneConfig> scenes = new ArrayList<>(1);
    scenes.add(sceneInfo);

    DepanFxSessionConfig result = new DepanFxSessionConfig(
        scenes);
    return result;
  }

  public DepanFxSessionConfig getSessionConfig() {
    if (sessionConfig == null) {
      return DepanFxSessionConfig.EMPTY_SESSION_DATA;
    }
    return sessionConfig;
  }
}
