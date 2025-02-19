package com.pnambic.depanfx.scene.plugins;
import com.pnambic.depanfx.scene.DepanFxSceneService;
import com.pnambic.depanfx.scene.DepanFxSceneViewer;

import java.util.function.Consumer;

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

    protected final Class<T> viewerType;

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

  public static class Action<T extends DepanFxSceneViewer>
      extends Basic<T> {

    private final Consumer<T> action;

    public Action(
        String menuKey, Class<T> viewerType, Consumer<T> action) {
      super(menuKey, viewerType);
      this.action = action;
    }

    @Override
    public void handleEvent(DepanFxSceneService sceneSrvc, ActionEvent event) {
      sceneSrvc.getViewer(viewerType)
      .ifPresent(action::accept);
    }
  }
}
