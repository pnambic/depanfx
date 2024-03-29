package com.pnambic.depanfx.perspective.plugins;

import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import java.nio.file.Path;

import javafx.scene.control.Cell;

public interface DepanFxResourcePathMenuContribution
    extends DepanFxOrderableContribution {

  boolean acceptsPath(Path rsrcPath);

  void prepareCell(
      DepanFxDialogRunner dialogRunner,
      DepanFxWorkspace workspace,
      Cell<DepanFxWorkspaceMember> cell,
      DepanFxProjectMember member,
      DepanFxContextMenuBuilder builder);
}
