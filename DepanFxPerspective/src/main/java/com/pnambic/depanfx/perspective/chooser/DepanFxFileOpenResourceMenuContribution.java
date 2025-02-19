package com.pnambic.depanfx.perspective.chooser;

import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneService;
import com.pnambic.depanfx.scene.DepanFxSceneViewer;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuContribution;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuItems;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.scene.Scene;

@Component
public class DepanFxFileOpenResourceMenuContribution
    extends DepanFxSceneMenuContribution.Simple {

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  @Autowired
  public DepanFxFileOpenResourceMenuContribution(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    super(DepanFxSceneMenuItems.FILE_OPEN_RESOURCE_ITEM);
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
  }

  @Override
  public boolean forViewer(DepanFxSceneViewer viewer) {
    return true;
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
