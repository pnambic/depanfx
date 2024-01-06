package com.pnambic.depanfx.java.gui;

import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.plugins.DepanFxNewResourceContribution;

import org.springframework.stereotype.Component;

import javafx.scene.control.MenuItem;

@Component
public class DepanFxNewJavaContribution
    implements DepanFxNewResourceContribution {

  private final DepanFxDialogRunner dialogRunner;

  public DepanFxNewJavaContribution(DepanFxDialogRunner dialogRunner) {
    this.dialogRunner = dialogRunner;
  }

  @Override
  public MenuItem createNewResourceMenuItem() {
    return DepanFxContextMenuBuilder.createActionItem(
        "Java", e -> runDialog());
  }

  private void runDialog() {
    dialogRunner.runDialog(
        DepanFxNewJavaDialog.class, "Create new Java graph");
  }
}
