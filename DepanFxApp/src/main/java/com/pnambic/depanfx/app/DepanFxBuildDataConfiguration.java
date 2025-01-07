/*
 * Copyright 2025 The Depan Project Authors
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

import com.pnambic.depanfx.scene.tooldata.DepanFxAboutData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

@Configuration
public class DepanFxBuildDataConfiguration {

  public static final String BUILD_DATA_PROPS_FILENAME =
      "build-info.properties";

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxBuildDataConfiguration.class);

  @Bean
  public DepanFxAboutData buildInfo() {
    Properties buildProps = new Properties();
    InputStream releaseProps =
        getClass().getResourceAsStream(BUILD_DATA_PROPS_FILENAME);

    try {
      buildProps.load(releaseProps);
    } catch (Exception errAny) {
      LOG.warn("", errAny);
    }

    return new BuildDataProps(buildProps);
  }

  public static class BuildDataProps implements DepanFxAboutData {

    public static final DateFormat FORMATTER =
        new SimpleDateFormat("yyyy-MM-dd HH:mm Z");

    static {
      FORMATTER.setLenient(true);
    }

    private final Properties buildProps;

    public BuildDataProps(Properties buildProps) {
      this.buildProps = buildProps;
    }

    @Override
    public Date getBuildDate() {
      String dateText = buildProps.getProperty("date");
      try {
        return FORMATTER.parse(dateText);
      } catch (ParseException e) {
        LOG.warn("Parse error on build date {}", dateText);
      }
      return null;
    }

    @Override
    public String getBuildSha1() {
      return buildProps.getProperty("sha1");
    }

    @Override
    public String getBuildMods() {
      return buildProps.getProperty("mods");
    }

    @Override
    public String getBuildTag() {
      return buildProps.getProperty("tag");
    }

    @Override
    public String getBuildRelease() {
      return buildProps.getProperty("release");
    }
  }
}
