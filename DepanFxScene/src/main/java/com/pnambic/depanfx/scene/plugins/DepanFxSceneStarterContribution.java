package com.pnambic.depanfx.scene.plugins;

import com.pnambic.depanfx.scene.DepanFxSceneService;
import com.pnambic.depanfx.scene.DepanFxSceneViewer;

import java.io.IOException;

public interface DepanFxSceneStarterContribution {

  @SuppressWarnings("serial")
  public class LoadViewerException extends RuntimeException {

    public LoadViewerException(IOException errIo) {
      super(errIo);
    }
  }

  String getLabel();

  DepanFxSceneViewer getSceneViewer(DepanFxSceneService sceneSrvc);
}
