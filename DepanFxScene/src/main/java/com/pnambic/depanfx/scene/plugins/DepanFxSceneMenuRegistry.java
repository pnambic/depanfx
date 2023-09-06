package com.pnambic.depanfx.scene.plugins;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.event.ActionEvent;

@Component
public class DepanFxSceneMenuRegistry {

  private final List<DepanFxSceneMenuContribution> contributions;

  @Autowired
  public DepanFxSceneMenuRegistry(List<DepanFxSceneMenuContribution> contributions) {
    this.contributions = contributions;
  }

  public void dispatch(ActionEvent event) {
    DepanFxSceneMenuContribution actor = contributions.stream()
        .filter(c -> c.acceptsEvent(event))
        .findFirst()
        .get();
    actor.handleEvent(event);
  }
}
