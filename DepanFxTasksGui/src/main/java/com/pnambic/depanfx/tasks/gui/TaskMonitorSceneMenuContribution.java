package com.pnambic.depanfx.tasks.gui;

import com.pnambic.depanfx.scene.DepanFxSceneService;
import com.pnambic.depanfx.scene.DepanFxSceneViewer;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuContribution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.event.ActionEvent;

/**
 * Adds the task monitor dialog to the DepanFX scene menus.
 */
@Component
public class TaskMonitorSceneMenuContribution
    extends DepanFxSceneMenuContribution.Simple {

  public static final String MENU_ITEM_KEY = "viewActiveTasksItem";

  private final TaskMonitorDialogService dialogService;

  @Autowired
  public TaskMonitorSceneMenuContribution(TaskMonitorDialogService dialogService) {
    super(MENU_ITEM_KEY);
    this.dialogService = dialogService;
  }

  @Override
  public boolean forViewer(DepanFxSceneViewer viewer) {
    return true;
  }

  @Override
  public void handleEvent(DepanFxSceneService sceneSrvc, ActionEvent event) {
    dialogService.showTaskMonitor();
  }
}
