package com.pnambic.depanfx.app;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.rgielen.fxweaver.core.FxWeaver;

@Configuration
public class DepanFxConfiguration {

  @Bean
  public FxWeaver fxWeaver(ConfigurableApplicationContext applicationContext) {
    return new FxWeaver(applicationContext::getBean, applicationContext::close);
  }
}
