package com.pnambic.depanfx.scene.plugins;

import com.pnambic.depanfx.scene.DepanFxSceneService;

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

  public void dispatch(DepanFxSceneService sceneSrvc, ActionEvent event) {
    contributions.stream()
        .filter(c -> c.acceptsEvent(sceneSrvc, event))
        .findFirst()
        .ifPresentOrElse(
            c -> c.handleEvent(sceneSrvc, event),
            () -> {
              MenuItem item = (MenuItem) event.getSource();
              LOG.info("Unable to dispatch scene menu event {}",
                  item.idProperty().getValue());
            });
  }
}
