package com.pnambic.depanfx.git.gui;

import com.pnambic.depanfx.git.builder.GitCommandRunner;
import com.pnambic.depanfx.git.builder.GitLogLoader;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

@Component
@FxmlView("new-git-logs-dialog.fxml")
public class DepanFxNewGitLogsDialog implements Initializable {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNewGitLogsDialog.class.getName());

  private static final int DEFAULT_LOG_COUNT = 10;

  private final DepanFxWorkspace workspace;

  @FXML
  private TextField gitExeField;

  @FXML
  private TextField repoDirectoryField;

  @FXML
  private TextField repoNameField;

  @FXML
  private TextField branchNameField;

  @FXML
  private TextField logCountField;

  @FXML
  private TextField graphDocumentField;

  @FXML
  private TextField dstDirectoryField;

  @Autowired
  public DepanFxNewGitLogsDialog(DepanFxWorkspace workspace) {
    this.workspace = workspace;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    DepanFxGitDialogUtils.initializeGitExit(gitExeField);
    if (branchNameField.getText().isBlank()) {
      branchNameField.setText(GitLogLoader.DEFAULT_BRANCH_NAME);
    }
    if (logCountField.getText().isBlank()) {
      logCountField.setText(Integer.toString(DEFAULT_LOG_COUNT));
    }
  }

  @FXML
  private void openGitExeChooser() {
    DepanFxGitDialogUtils.runGitExeChooser(gitExeField);
  }

  @FXML
  private void openRepoDirectoryChooser() {
    DepanFxGitDialogUtils.runRepoDirectoryChooser(
        repoDirectoryField, repoNameField);
  }

  @FXML
  private void openGraphDocChooser() {
    DepanFxGitDialogUtils.runGraphDocChooser(graphDocumentField, workspace);
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

    LOG.info("git executable: {}", gitExeField.getText());
    LOG.info("Repo directory: {}", repoDirectoryField.getText());
    LOG.info("Repo name: {}", repoNameField.getText());
    LOG.info("Branch Name: {}", branchNameField.getText());
    LOG.info("Log Count: {}", logCountField.getText());
    LOG.info("Graph Doc file: {}", graphDocumentField.getText());
    LOG.info("Destination directory: {}", dstDirectoryField.getText());

    GitCommandRunner cmdRunner =
        DepanFxGitDialogUtils.createCommandRunner(
            gitExeField, repoNameField, repoDirectoryField);

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
          new GitLogLoader(dstDir, optGraphRsrc.get(), cmdRunner);
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
    try {
      Optional<DepanFxWorkspaceResource> result =
          workspace.importDocument(graphDoc);

      // Ensure the result is a GraphDocument
      return result
          .filter(r -> r.getResource() instanceof GraphDocument);
    } catch (IOException errIo) {
      throw new RuntimeException(
          "Unable to open graph " + graphDoc.getMemberName(), errIo);
    }
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
