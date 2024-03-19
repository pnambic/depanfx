package com.pnambic.depanfx.perspective.plugins;

import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

import javafx.scene.control.Cell;

public interface DepanFxResourceExtMenuContribution
    extends DepanFxOrderableContribution {

  boolean acceptsExt(String ext);

  void prepareCell(
      DepanFxDialogRunner dialogRunner, DepanFxWorkspace workspace,
      Cell<DepanFxWorkspaceMember> cell, String ext,
      DepanFxProjectMember member, DepanFxContextMenuBuilder builder);

  public static abstract class Basic
      implements DepanFxResourceExtMenuContribution {

    private static Logger LOG = LoggerFactory.getLogger(Basic.class);

    private final String fileExt;

    private final String editActionLabel;

    private final Class<?> dataType;

    private final String orderKey;

    public Basic(
        Class<?> dataType, String orderKey,
        String editActionLabel, String fileExt) {
      this.dataType = dataType;
      this.orderKey = orderKey;
      this.editActionLabel = editActionLabel;
      this.fileExt = fileExt;
    }

    @Override
    public String getOrderKey() {
      return orderKey;
    }

    @Override
    public boolean acceptsExt(String fileExt) {
      return this.fileExt.equals(fileExt);
    }

    @Override
    public void prepareCell(
        DepanFxDialogRunner dialogRunner, DepanFxWorkspace workspace,
        Cell<DepanFxWorkspaceMember> cell, String ext,
        DepanFxProjectMember member, DepanFxContextMenuBuilder builder) {
      Path docPath = member.getMemberPath();
      DepanFxResourcePerspectives.installOnOpen(cell, docPath,
          p -> runEditAction(dialogRunner, workspace, p));
      builder.appendActionItem(
          editActionLabel,
          e -> runEditAction(dialogRunner, workspace, docPath));
    }

    abstract protected void runDialog(
        DepanFxWorkspaceResource wkspRsrc, DepanFxDialogRunner dialogRunner);

    private void runEditAction(
        DepanFxDialogRunner dialogRunner,
        DepanFxWorkspace workspace,
        Path rsrcPath) {
      try {
        workspace.toProjectDocument(rsrcPath.toUri())
            .flatMap(
                r -> workspace.getWorkspaceResource(r, dataType))
            .ifPresent(r -> runDialog(r, dialogRunner));
      } catch (RuntimeException errCaught) {
        LOG.error("Unable to open data {} for {}",
            rsrcPath.toUri(), editActionLabel, errCaught);
      }
    }
  }
}
