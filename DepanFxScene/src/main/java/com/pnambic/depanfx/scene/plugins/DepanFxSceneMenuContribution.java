package com.pnambic.depanfx.scene.plugins;

import com.pnambic.depanfx.scene.DepanFxSceneService;
import com.pnambic.depanfx.scene.DepanFxSceneViewer;

import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;

public interface DepanFxSceneMenuContribution {

  String forMenuKey();

  boolean forViewer(DepanFxSceneViewer viewer);

  boolean acceptsEvent(DepanFxSceneService sceneSrvc, ActionEvent event);

  void handleEvent(DepanFxSceneService sceneSrvc, ActionEvent event);

  /**
   */
  abstract public static class Simple implements DepanFxSceneMenuContribution {

    private final String menuKey;

    public Simple(String menuKey) {
      this.menuKey = menuKey;
    }

    @Override
    public String forMenuKey() {
      return menuKey;
    }

    @Override
    public boolean acceptsEvent(
        DepanFxSceneService sceneSrvc, ActionEvent event) {
      MenuItem item = (MenuItem) event.getSource();
      return (item.idProperty().getValue().equals(menuKey));
    }
  }

  /**
   * Assumes menuKey and viewerType are independent.
   *
   * @param <T>
   */
  abstract public static class Basic<T extends DepanFxSceneViewer>
      extends Simple {

    private final Class<T> viewerType;

    public Basic(String menuKey, Class<T> viewerType) {
      super(menuKey);
      this.viewerType = viewerType;
    }

    @Override
    public boolean forViewer(DepanFxSceneViewer viewer) {
      return viewerType.isAssignableFrom(viewer.getClass());
    }

    @Override
    public boolean acceptsEvent(
        DepanFxSceneService sceneSrvc, ActionEvent event) {
      if (!super.acceptsEvent(sceneSrvc, event)) {
        return false;
      }

      return sceneSrvc.getViewer(viewerType)
          .map(this::forViewer)
          .orElse(false);
    }
  }
}
