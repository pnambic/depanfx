package com.pnambic.depanfx.scene.plugins;

import com.pnambic.depanfx.scene.DepanFxSceneViewer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DepanFxSceneStarterRegistry {

  private final List<DepanFxSceneStarterContribution> contributions;

  @Autowired
  public DepanFxSceneStarterRegistry(List<DepanFxSceneStarterContribution> contributions) {
    this.contributions = contributions;
  }

  public List<DepanFxSceneViewer> getStarterViews() {
    return contributions.stream()
        .sorted((a,b) -> a.getLabel().compareTo(b.getLabel()))
        .map(c -> c.getSceneViewer())
        .collect(Collectors.toList());
  }
}
