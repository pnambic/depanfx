/*
 * Copyright 2016, 2023 The Depan Project Authors
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
package com.pnambic.depanfx.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provide a common logger for the entire persistence package.
 * Simply import this class, and use the available {@link #LOG} member.
 *
 * This logger is available for use by XStream type converters, regardless
 * of the plugin that supplies the converter.
 *
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class PersistenceLogger {

  public static final Logger LOG =
      LoggerFactory.getLogger(PersistenceLogger.class.getName());

  private PersistenceLogger() {
    // Prevent instantiation.
  }
}
