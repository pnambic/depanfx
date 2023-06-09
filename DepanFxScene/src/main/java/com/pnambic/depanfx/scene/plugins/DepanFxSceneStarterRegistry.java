package com.pnambic.depanfx.scene.plugins;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

@Component
public class DepanFxSceneStarterRegistry {

  private final List<DepanFxSceneStarterContribution> contributions;

  @Autowired
  public DepanFxSceneStarterRegistry(List<DepanFxSceneStarterContribution> contributions) {
    this.contributions = contributions;
  }

  public void addStarterTabs(TabPane container) {
    for (DepanFxSceneStarterContribution contribution : contributions) {
      Tab contributionTab = contribution.createStarterTab(contribution.getLabel());
      container.getTabs().add(contributionTab);
    }
  }
}
