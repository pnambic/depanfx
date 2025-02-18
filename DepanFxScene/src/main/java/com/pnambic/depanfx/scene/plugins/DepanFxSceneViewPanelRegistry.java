package com.pnambic.depanfx.scene.plugins;

import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxSceneController;
import com.pnambic.depanfx.scene.DepanFxSceneViewer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javafx.scene.control.MenuItem;

/**
 * Provide a registry of scene panel components that may be created
 * within the scene.
 */
@Component
public class DepanFxSceneViewPanelRegistry {

  public interface Contribution {

    @SuppressWarnings("serial")
    public class LoadPanelException extends RuntimeException {

      public LoadPanelException(IOException errIo) {
        super(errIo);
      }
    }

    String getLabel();

    String getOrder();

    DepanFxSceneViewer getSceneViewer();
  }

  private final Collection<Contribution> viewPanelContribs;

  @Autowired
  public DepanFxSceneViewPanelRegistry(Collection<Contribution> viewPanelContribs) {
    this.viewPanelContribs = viewPanelContribs;
  }

  public List<MenuItem> buildViewPanelItems(DepanFxSceneController scene) {

    int contribCnt = viewPanelContribs.size();
    if (contribCnt > 0) {
      List<MenuItem> result = new ArrayList<>(contribCnt);
      viewPanelContribs.stream()
          .sorted((a,b) -> a.getOrder().compareTo(b.getOrder()))
          .map(c -> buildContribMenuItem(scene, c))
          .forEach(result::add);
      return result;
    }
    return Collections.emptyList();
  }

  private MenuItem buildContribMenuItem(
      DepanFxSceneController scene, Contribution contrib) {
    return DepanFxContextMenuBuilder.createActionItem(
        contrib.getLabel(),
        e -> scene.addViewer(contrib.getSceneViewer()));
  }
}
