package com.pnambic.depanfx.perspective.plugins;

import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneController;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import javafx.scene.control.Cell;

/**
 * An analysis contribution that is expect to interact closely with the
 * application scene.
 */
public interface DepanFxAnalysisExtMenuContribution
    extends DepanFxOrderableContribution {

  boolean acceptsExt(String ext);

  void prepareCell(
      DepanFxSceneController scene,
      DepanFxDialogRunner dialogRunner, DepanFxWorkspace workspace,
      Cell<DepanFxWorkspaceMember> cell, String ext,
      DepanFxProjectMember member, DepanFxContextMenuBuilder builder);
}
