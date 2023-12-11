package com.pnambic.depanfx.perspective.plugins;

import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import javafx.scene.control.Cell;

public interface DepanFxResourceExtMenuContribution {

  boolean acceptsExt(String ext);

  void prepareCell(
      DepanFxDialogRunner dialogRunner, DepanFxWorkspace workspace,
      Cell<DepanFxWorkspaceMember> cell, String ext,
      DepanFxProjectMember member, DepanFxContextMenuBuilder builder);
}
