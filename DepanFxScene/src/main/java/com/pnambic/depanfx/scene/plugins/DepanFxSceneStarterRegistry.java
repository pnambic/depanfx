package com.pnambic.depanfx.scene.plugins;

import com.pnambic.depanfx.scene.DepanFxSceneService;
import com.pnambic.depanfx.scene.DepanFxSceneViewer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DepanFxSceneStarterRegistry {

  public interface Contribution {

    String getLabel();

    DepanFxSceneViewer getSceneViewer(DepanFxSceneService sceneSrvc);
  }

  @SuppressWarnings("serial")
  public class LoadViewerException extends RuntimeException {

    public LoadViewerException(IOException errIo) {
      super(errIo);
    }
  }

  private final List<Contribution> contributions;

  @Autowired
  public DepanFxSceneStarterRegistry(
      List<Contribution> contributions) {
    this.contributions = contributions;
  }

  public List<DepanFxSceneViewer> getStarterViews(
      DepanFxSceneService sceneSrvc) {
    return contributions.stream()
        .sorted((a,b) -> a.getLabel().compareTo(b.getLabel()))
        .map(c -> c.getSceneViewer(sceneSrvc))
        .collect(Collectors.toList());
  }
}
