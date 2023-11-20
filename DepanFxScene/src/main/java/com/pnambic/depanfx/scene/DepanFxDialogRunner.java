package com.pnambic.depanfx.scene;

import net.rgielen.fxweaver.core.FxWeaver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

@Component
public class DepanFxDialogRunner {

  private final FxWeaver fxweaver;

  @Autowired
  public DepanFxDialogRunner(FxWeaver fxweaver) {
    this.fxweaver = fxweaver;
  }

  public void runDialog(Parent dialogPane, String title) {
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
    runDialog(fxweaver.loadView(type), title);
  }
}
