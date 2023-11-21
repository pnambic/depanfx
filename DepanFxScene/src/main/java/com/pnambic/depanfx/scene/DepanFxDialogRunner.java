package com.pnambic.depanfx.scene;

import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Get a dialog with both {@link FxWeaver} and Spring Boot injection.
 */
@Component
public class DepanFxDialogRunner {

  private final FxWeaver fxweaver;

  @Autowired
  public DepanFxDialogRunner(FxWeaver fxweaver) {
    this.fxweaver = fxweaver;
  }

  public static void runDialog(Parent dialogPane, String title) {
    Stage dialogStage = new Stage();
    dialogStage.initModality(Modality.APPLICATION_MODAL);
    dialogStage.setTitle(title);
    DepanFxAppIcons.installDepanIcons(dialogStage.getIcons());
    dialogStage.setScene(new Scene(dialogPane));
    dialogStage.showAndWait();
  }

  /**
   * Resolution of controller and dialog elements uses both the
   * FxWeaver and Spring injection capabilities.e
   */
  public void runDialog(Class<?> type, String title) {
    // Equivalent to createDialogAndParent(type).runDialog(title),
    // but avoids an object creation.s
    Parent view = fxweaver.loadView(type);
    runDialog(view, title);
  }

  public <C> Dialog<C>createDialogAndParent(Class<C> type) {
    return new Dialog<C>(fxweaver.load(type));
  }

  public static class Dialog <C>{
    private final FxControllerAndView<C, Parent> fxLoad;

    public Dialog(FxControllerAndView<C, Parent> fxLoad) {
      this.fxLoad = fxLoad;
    }

    public void runDialog(String title) {
      DepanFxDialogRunner.runDialog(fxLoad.getView().get(), title);
    }

    public C getController() {
      return fxLoad.getController();
    }
  }
}
