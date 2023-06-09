package com.pnambic.depanfx;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.pnambic.depanfx.app.DepanFxApp;

import javafx.application.Application;

/**
 * Spring conformant starting point.
 *
 * Convention over configuration
 * - Follows Spring's XxxApplication suffix pattern.
 * - Located at root of {@code com.pnambic.depanfx} package for natural
 * {@code @Component} discovery.
 */
@SpringBootApplication
public class DepanFxApplication {

  public static void main(String[] args) {
    Application.launch(DepanFxApp.class, args);
  }
}
