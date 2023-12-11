package com.pnambic.depanfx.perspective.plugins;

import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneController;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.Cell;

@Component
public class DepanFxResourceMenuRegistry {

  private final List<DepanFxAnalysisExtMenuContribution> analysisContribs;

  private final List<DepanFxResourceExtMenuContribution> extContribs;

  private final List<DepanFxResourcePathMenuContribution> pathContribs;

  @Autowired
  public DepanFxResourceMenuRegistry(
      List<DepanFxAnalysisExtMenuContribution> analysisContribs,
      List<DepanFxResourceExtMenuContribution> extContribs,
      List<DepanFxResourcePathMenuContribution> pathContribs) {
    this.analysisContribs = analysisContribs;
    this.extContribs = extContribs;
    this.pathContribs = pathContribs;
  }

  /**
   * Apply every contribution that matches this document's file name extension.
   */
  public void prepareAnalysisMenu(
      DepanFxSceneController scene,
      DepanFxDialogRunner dialogRunner,
      DepanFxWorkspace workspace,
      Cell<DepanFxWorkspaceMember> cell,
      DepanFxProjectDocument document,
      DepanFxContextMenuBuilder builder) {
    Path docPath = document.getMemberPath();
    Optional<String> optExt =
        DepanFxWorkspaceFactory.getExtension(docPath.getFileName().toString());

    if (optExt.isPresent()) {
      String ext = optExt.get();

      analysisContribs.stream()
          .filter(c -> c.acceptsExt(ext))
          .forEach(c -> c.prepareCell(
              scene, dialogRunner, workspace, cell, ext, document, builder));
    }
  }

  /**
   * Apply every contribution that matches this document's file name extension.
   */
  public void prepareDocumentMenu(
      DepanFxDialogRunner dialogRunner,
      DepanFxWorkspace workspace,
      Cell<DepanFxWorkspaceMember> cell,
      DepanFxProjectDocument document,
      DepanFxContextMenuBuilder builder) {
    Path docPath = document.getMemberPath();
    Optional<String> optExt =
        DepanFxWorkspaceFactory.getExtension(docPath.getFileName().toString());

    if (optExt.isPresent()) {
      String ext = optExt.get();

      extContribs.stream()
          .filter(c -> c.acceptsExt(ext))
          .forEach(c -> c.prepareCell(
              dialogRunner, workspace, cell, ext, document, builder));
    }
  }

  /**
   * Apply every contribution that matches this document's path.
   */
  public void prepareMemberMenu(
      DepanFxDialogRunner dialogRunner,
      DepanFxWorkspace workspace,
      Cell<DepanFxWorkspaceMember> cell,
      DepanFxProjectMember member,
      DepanFxContextMenuBuilder builder) {
    Path docPath = member.getProject().getRelativePath(member.getMemberPath());

    pathContribs.stream()
        .filter(c -> c.acceptsPath(docPath))
        .forEach(c -> c.prepareCell(
            dialogRunner, workspace, cell, member, builder));
  }
}
