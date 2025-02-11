package com.pnambic.depanfx.perspective.chooser;

import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneService;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuContribution;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;

@Component
public class DepanFxFileOpenResourceMenuContribution
    implements DepanFxSceneMenuContribution {

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  @Autowired
  public DepanFxFileOpenResourceMenuContribution(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
  }

  @Override
  public boolean acceptsEvent(
      DepanFxSceneService sceneSrvc, ActionEvent event) {
    MenuItem item = (MenuItem) event.getSource();
    return item.idProperty().getValue().equals("fileOpenResourceItem");
  }

  @Override
  public void handleEvent(
      DepanFxSceneService sceneSrvc, ActionEvent event) {
    runOpenResourceDialog();
  }

  private void runOpenResourceDialog() {
    DepanFxResourceChooser chooser =
        new DepanFxResourceChooser(workspace, dialogRunner);
    chooser.setExtensionFilters(null);
    Optional<DepanFxWorkspaceMember> result =
        chooser.showOpenDialog((Scene) null);
  }
}
