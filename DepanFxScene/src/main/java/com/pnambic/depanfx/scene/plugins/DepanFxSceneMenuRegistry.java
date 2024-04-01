package com.pnambic.depanfx.scene.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;

@Component
public class DepanFxSceneMenuRegistry {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxSceneMenuRegistry.class);

  private final List<DepanFxSceneMenuContribution> contributions;

  @Autowired
  public DepanFxSceneMenuRegistry(List<DepanFxSceneMenuContribution> contributions) {
    this.contributions = contributions;
  }

  public void dispatch(ActionEvent event) {
    contributions.stream()
        .filter(c -> c.acceptsEvent(event))
        .findFirst()
        .ifPresentOrElse(
            a -> a.handleEvent(event),
            () -> {
              MenuItem item = (MenuItem) event.getSource();
              LOG.info("Unable to dispatch scene menu event {}",
                  item.idProperty().getValue());
            });
  }
}
