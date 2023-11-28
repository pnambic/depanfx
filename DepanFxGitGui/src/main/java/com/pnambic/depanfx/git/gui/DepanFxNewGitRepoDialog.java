package com.pnambic.depanfx.git.gui;

import com.pnambic.depanfx.filesystem.builder.FileSystemGraphDocBuilder;
import com.pnambic.depanfx.git.builder.GitCommandRunner;
import com.pnambic.depanfx.git.builder.GitLsFileLoader;
import com.pnambic.depanfx.git.tooldata.DepanFxGitRepoData;
import com.pnambic.depanfx.graph_doc.builder.DepanFxGraphModelBuilder;
import com.pnambic.depanfx.graph_doc.builder.SimpleGraphModelBuilder;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;

@Component
@FxmlView("new-git-repo-dialog.fxml")
public class DepanFxNewGitRepoDialog
    implements DepanFxGitRepoToolDialog.Aware {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNewGitRepoDialog.class.getName());

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private DepanFxGitRepoData repoData;

  @FXML
  private TextField gitRepoNameField;

  @FXML
  private TextField destinationField;

  @Autowired
  public DepanFxNewGitRepoDialog(
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
    gitRepoNameField.setContextMenu(
        DepanFxGitRepoToolDialogs.buildRepoChoiceMenu(this, workspace, dialogRunner));
  }

  @FXML
  private void openDestinationChooser() {
    DepanFxGraphDocDialogs.runSaveGraphDocFileChooser(
        destinationField, repoData.getToolName(), workspace);
  }

  @FXML
  private void handleCancel() {
    closeDialog();
    LOG.info("cancelled request");
  }

  @FXML
  private void handleConfirm() {
    closeDialog();

    DepanFxGraphModelBuilder modelBuilder = new SimpleGraphModelBuilder();
    GitCommandRunner cmdRunner = new GitCommandRunner(repoData);

    File dstFile = new File(destinationField.getText());
    DepanFxProjectDocument projDoc =
        workspace.toProjectDocument(dstFile.toURI()).get();

    try {
      analyzeRepo(modelBuilder, cmdRunner);

      GraphDocument graphDoc = new FileSystemGraphDocBuilder(modelBuilder)
          .getGraphDocument();

      workspace.saveDocument(projDoc, graphDoc);
    } catch (Exception errAny) {
      LOG.error("Unable to save " + dstFile, errAny);
    }
  }

  private void closeDialog() {
    ((Stage) destinationField.getScene().getWindow()).close();
  }

  /**
   * Ensure analysis failures don't propogate outside of the analysis request.
   */
  private void analyzeRepo(
      DepanFxGraphModelBuilder modelBuilder, GitCommandRunner cmdRunner) {
    GitLsFileLoader loader = new GitLsFileLoader(modelBuilder, cmdRunner);
    try {
      loader.analyzeRepo();
    } catch (RuntimeException errBuild) {
      LOG.error("unable to build graph from git repo {} at {}",
          cmdRunner.getGitRepoName(), cmdRunner.getGitRepoPath());
    }
  }
}
