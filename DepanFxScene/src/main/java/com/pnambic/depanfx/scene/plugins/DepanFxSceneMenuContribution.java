package com.pnambic.depanfx.scene.plugins;
import com.pnambic.depanfx.scene.DepanFxSceneService;
import com.pnambic.depanfx.scene.DepanFxSceneViewer;

import java.util.function.Consumer;

import javafx.event.ActionEvent;

public interface DepanFxSceneMenuContribution {

  /**
   * Identifies the menu item key that this contribution is associated with.
   */
  String forMenuKey();

  /**
   * Indicates if the contribution can handle the proposed viewer object.
   */
  boolean forViewer(DepanFxSceneViewer viewer);

  /**
   * Indicates whether the contribution can handle the proposed menu item key
   * given the supplied {@link DepanFxSceneService}.
   */
  boolean acceptsMenuItemKey(
      DepanFxSceneService sceneSrvc, String eventItemKey);

  /**
   * Perform the action defined by this contribution.
   * The full originating event is provided for context
   * in the situation that the action requires further contextual refinement.
   */
  void handleEvent(DepanFxSceneService sceneSrvc, ActionEvent event);

  /**
   * Basic menu contribution that uses a baked in {@link #menuItemKey}.
   */
  abstract public static class Simple implements DepanFxSceneMenuContribution {

    private final String menuItemKey;

    public Simple(String menuItemKey) {
      this.menuItemKey = menuItemKey;
    }

    @Override
    public String forMenuKey() {
      return menuItemKey;
    }

    @Override
    public boolean acceptsMenuItemKey(
        DepanFxSceneService sceneSrvc, String menuItemKey) {
      return this.menuItemKey.equals(menuItemKey);
    }
  }

  /**
   * Extends Simple to support a viewer type.
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
    public boolean acceptsMenuItemKey(
        DepanFxSceneService sceneSrvc, String menuItemKey) {
      if (!super.acceptsMenuItemKey(sceneSrvc, menuItemKey)) {
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
