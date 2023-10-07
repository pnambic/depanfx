package com.pnambic.depanfx.scene.plugins;

import javafx.event.ActionEvent;

public interface DepanFxSceneMenuContribution {

  boolean acceptsEvent(ActionEvent event);

  void handleEvent(ActionEvent event);

}
