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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Configure the session startup information form the command line.
 */
@Component
public class DepanFxSessionCliArgs implements ApplicationRunner {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxSessionCliArgs.class);

  private static final String SESSION_OPTION = "session";

  private final DepanFxSessionDataTransport transport;

  private DepanFxSessionConfig sessionConfig;

  private Path sessionPath;

  @Autowired
  private DepanFxSessionCliArgs(
      DepanFxSessionDataTransport transport) {
    this.transport = transport;
  }

  @Override
  public void run(ApplicationArguments args) {
    if (args.containsOption(SESSION_OPTION)) {
      String sessionSrc = args.getOptionValues(SESSION_OPTION).get(0);

      try {
        sessionConfig = restoreSession(sessionSrc);
        return;
      } catch (RuntimeException errAny) {
        LOG.error("Unable to load session data at {}", sessionSrc, errAny);
      // Fall through to default.
      }
    }
    sessionConfig = transport.defaultSessionConfig();
  }

  public DepanFxSessionConfig getSessionConfig() {
    if (sessionConfig != null) {
      return sessionConfig;
    }
    return DepanFxSessionConfig.EMPTY_SESSION_DATA;
  }

  public Optional<Path> getSessionPath() {
    return Optional.ofNullable(sessionPath);
  }

  private DepanFxSessionConfig restoreSession(String sessionSrc) {
    sessionPath = Path.of(sessionSrc);
    return transport.loadSessionConfig(sessionPath);
  }
}
