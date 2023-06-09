package com.pnambic.depanfx.scene.plugins;

import javafx.scene.control.Tab;

public interface DepanFxSceneStarterContribution {
  String getLabel();

  Tab createStarterTab(String label);
}
