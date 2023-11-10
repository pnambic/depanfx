package com.pnambic.depanfx.git.gui;

import com.pnambic.depanfx.filesystem.builder.FileSystemGraphDocBuilder;
import com.pnambic.depanfx.git.builder.GitCommandRunner;
import com.pnambic.depanfx.git.builder.GitLsFileLoader;
import com.pnambic.depanfx.graph_doc.builder.DepanFxGraphModelBuilder;
import com.pnambic.depanfx.graph_doc.builder.SimpleGraphModelBuilder;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

@Component
@FxmlView("new-git-repo-dialog.fxml")
public class DepanFxNewGitRepoDialog implements Initializable {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNewGitRepoDialog.class.getName());

  private final DepanFxWorkspace workspace;

  @FXML
  private TextField gitExeField;

  @FXML
  private TextField repoDirectoryField;

  @FXML
  private TextField repoNameField;

  @FXML
  private TextField destinationField;

  @Autowired
  public DepanFxNewGitRepoDialog(DepanFxWorkspace workspace) {
    this.workspace = workspace;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    DepanFxGitDialogUtils.initializeGitExit(gitExeField);
  }

  @FXML
  private void openGitExeChooser() {
    DepanFxGitDialogUtils.runGitExeChooser(gitExeField);
  }

  @FXML
  private void openDirectoryChooser() {
    DepanFxGitDialogUtils.runRepoDirectoryChooser(
        repoDirectoryField, repoNameField);
  }

  @FXML
  private void openFileChooser() {
    FileChooser fileChooser = prepareSaveGraphDocFileChooser();
    File selectedFile =
        fileChooser.showSaveDialog(destinationField.getScene().getWindow());
    if (selectedFile != null) {
      destinationField.setText(selectedFile.getAbsolutePath());
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

    LOG.info("git executable: {}", gitExeField.getText());
    LOG.info("Repo directory: {}", repoDirectoryField.getText());
    LOG.info("Repo name: {}", repoNameField.getText());
    LOG.info("Destination file: {}", destinationField.getText());

    DepanFxGraphModelBuilder modelBuilder = new SimpleGraphModelBuilder();
    GitCommandRunner cmdRunner =
        DepanFxGitDialogUtils.createCommandRunner(
            gitExeField, repoNameField, repoDirectoryField);

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

  private FileChooser prepareSaveGraphDocFileChooser() {
    FileChooser result =
        DepanFxGitDialogUtils.prepareGraphDocChooser(
            destinationField, workspace);

    if (destinationField.getText().isBlank()) {
      result.setInitialFileName(buildDestinationName());
    }

    return result;
  }

  private String buildDestinationName() {
    return DepanFxWorkspaceFactory.buildDocumentTimestampName(
          DepanFxGitDialogUtils.getRepoName(repoNameField),
          DepanFxGitDialogUtils.DGI_EXT);
  }
}
