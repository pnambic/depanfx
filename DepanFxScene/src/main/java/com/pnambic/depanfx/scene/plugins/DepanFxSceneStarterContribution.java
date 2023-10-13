package com.pnambic.depanfx.scene.plugins;

import com.pnambic.depanfx.scene.DepanFxSceneController;

import javafx.scene.control.Tab;

public interface DepanFxSceneStarterContribution {
  String getLabel();

  Tab createStarterTab(String label, DepanFxSceneController scene);
}
