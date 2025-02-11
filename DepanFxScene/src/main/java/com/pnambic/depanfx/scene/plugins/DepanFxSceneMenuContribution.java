package com.pnambic.depanfx.scene.plugins;

import com.pnambic.depanfx.scene.DepanFxSceneService;

import javafx.event.ActionEvent;

public interface DepanFxSceneMenuContribution {

  boolean acceptsEvent(DepanFxSceneService sceneSrvc, ActionEvent event);

  void handleEvent(DepanFxSceneService sceneSrvc, ActionEvent event);
}
