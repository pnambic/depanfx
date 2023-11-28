package com.pnambic.depanfx.git.gui;

import com.pnambic.depanfx.git.builder.GitCommandRunner;
import com.pnambic.depanfx.git.builder.GitLogLoader;
import com.pnambic.depanfx.git.tooldata.DepanFxGitRepoData;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

@Component
@FxmlView("new-git-logs-dialog.fxml")
public class DepanFxNewGitLogsDialog
    implements DepanFxGitRepoToolDialog.Aware {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNewGitLogsDialog.class);

  private static final int DEFAULT_LOG_COUNT = 10;

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private DepanFxGitRepoData repoData;

  @FXML
  private TextField gitRepoNameField;

  @FXML
  private TextField branchNameField;

  @FXML
  private TextField logCountField;

  @FXML
  private TextField graphDocumentField;

  @FXML
  private TextField dstDirectoryField;

  @Autowired
  public DepanFxNewGitLogsDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
  }

  @Override // DepanFxGitRepoToolDialog.Aware
  public DepanFxGitRepoData getTooldata() {
    return repoData;
  }

  @Override // DepanFxGitRepoToolDialog.Aware
  public void setTooldata(DepanFxGitRepoData repoData) {
    this.repoData = repoData;
    gitRepoNameField.setText(repoData.getToolName());
  }

  @Override // DepanFxGitRepoToolDialog.Aware
  public Window getChooserWindow() {
    return gitRepoNameField.getScene().getWindow();
  }

  @FXML
  public void initialize() {
    if (branchNameField.getText().isBlank()) {
      branchNameField.setText(GitLogLoader.DEFAULT_BRANCH_NAME);
    }
    if (logCountField.getText().isBlank()) {
      logCountField.setText(Integer.toString(DEFAULT_LOG_COUNT));
    }
    gitRepoNameField.setContextMenu(
        DepanFxGitRepoMenu.buildRepoChoiceMenu(this, workspace, dialogRunner));
  }

  @FXML
  private void openGraphDocChooser() {
    DepanFxGraphDocDialogs
        .runOpenGraphDocFileChooser(graphDocumentField, workspace);
  }

  @FXML
  private void openDestinationChooser() {
    DirectoryChooser directoryChooser = prepareDstDirectoryChooser();
    File selectedDirectory =
        directoryChooser.showDialog(dstDirectoryField.getScene().getWindow());
    if (selectedDirectory != null) {
      dstDirectoryField.setText(selectedDirectory.getAbsolutePath());
    }
  }

  @FXML
  private void handleCancel() {
    closeDialog();
    LOG.info("cancelled request");
  }

  @FXML
  private void handleConfirm() {
    closeDialog();

    GitCommandRunner cmdRunner = new GitCommandRunner(repoData);

    File graphDocFile = new File(graphDocumentField.getText());
    Optional<DepanFxProjectDocument> optGraphDoc =
        workspace.toProjectDocument(graphDocFile.toURI());

    File dstDirFile = new File(dstDirectoryField.getText());
    Optional<DepanFxProjectContainer> optDstDir =
        workspace.toProjectContainer(dstDirFile.toURI());

    int logCount = Integer.parseInt(logCountField.getText());

    try {
      Optional<DepanFxWorkspaceResource> optGraphRsrc = optGraphDoc
          .flatMap(this::importGraphResource);

      DepanFxProjectContainer dstDir = optDstDir.get();
      GitLogLoader logLoader =
          new GitLogLoader(workspace, dstDir, optGraphRsrc.get(), cmdRunner);
      dstDir.getProject().registerContainer(dstDir);

      logLoader.loadBranchCommits(branchNameField.getText(), logCount);

    } catch (Exception errAny) {
      LOG.error("Unable to import logs", errAny);
    }
  }

  private void closeDialog() {
    ((Stage) dstDirectoryField.getScene().getWindow()).close();
  }

  private Optional<DepanFxWorkspaceResource> importGraphResource(
      DepanFxProjectDocument graphDoc) {
    return DepanFxWorkspaceFactory.loadDocument(
        workspace, graphDoc, GraphDocument.class);
  }

  private DirectoryChooser prepareDstDirectoryChooser() {
    DirectoryChooser result = new DirectoryChooser();

    String destFileName = dstDirectoryField.getText();
    if (!destFileName.isBlank()) {
      result.setInitialDirectory(new File(destFileName));
      return result;
    }
    result.setInitialDirectory(DepanFxProjects.getCurrentAnalyzes(workspace));
    return result;
  }
}
