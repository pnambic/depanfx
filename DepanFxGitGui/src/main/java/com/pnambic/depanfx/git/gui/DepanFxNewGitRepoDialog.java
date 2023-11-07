package com.pnambic.depanfx.git.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pnambic.depanfx.filesystem.builder.FileSystemGraphDocBuilder;
import com.pnambic.depanfx.git.builder.GitCommandRunner;
import com.pnambic.depanfx.git.builder.GitLsFileLoader;
import com.pnambic.depanfx.graph_doc.builder.DepanFxGraphModelBuilder;
import com.pnambic.depanfx.graph_doc.builder.SimpleGraphModelBuilder;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("new-git-repo-dialog.fxml")
public class DepanFxNewGitRepoDialog implements Initializable {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNewGitRepoDialog.class.getName());

  // For executables (on Windows)
  private static final String EXE_EXT = "exe";

  private static final ExtensionFilter EXE_FILTER =
      new ExtensionFilter("Executabe (*.exe)", "*." + EXE_EXT);

  // For Graph documents
  private static final String DGI_EXT = "dgi";

  private static final ExtensionFilter DGI_FILTER =
      new ExtensionFilter("Graph Info (*.dgi)", "*." + DGI_EXT);

  private static final String DEFAULT_REPO_LABEL = "Git Repo";

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
    if (gitExeField.getText().isBlank()) {
      gitExeField.setText(GitCommandRunner.DEFAULT_GIT_EXE);
    }
  }

  @FXML
  private void openGitExeChooser() {
    FileChooser fileChooser = prepareGitExeChooser();
    File selectedFile =
        fileChooser.showSaveDialog(gitExeField.getScene().getWindow());
    if (selectedFile != null) {
      gitExeField.setText(selectedFile.getAbsolutePath());
    }
  }

  @FXML
  private void openDirectoryChooser() {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    File selectedDirectory =
        directoryChooser.showDialog(repoDirectoryField.getScene().getWindow());
    if (selectedDirectory != null) {
      repoDirectoryField.setText(selectedDirectory.getAbsolutePath());

      StringProperty repoNameProp = repoNameField.textProperty();
      if (repoNameProp.get().isBlank()) {
        repoNameProp.set(selectedDirectory.getName());
      }
    }
  }

  @FXML
  private void openFileChooser() {
    FileChooser fileChooser = prepareFileChooser();
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
    GitCommandRunner cmdRunner = new GitCommandRunner(
        gitExeField.getText(),
        repoNameField.getText(),
        repoDirectoryField.getText());
    analyzeRepo(modelBuilder, cmdRunner);

    GraphDocument graphDoc = new FileSystemGraphDocBuilder(modelBuilder)
        .getGraphDocument();
    File dstFile = new File(destinationField.getText());

    try {
      workspace.saveDocument(dstFile.toURI(), graphDoc);
    } catch (IOException errIo) {
      LOG.error("Unable to save " + dstFile, errIo);
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

  private FileChooser prepareGitExeChooser() {
    FileChooser result = new FileChooser();
    result.getExtensionFilters().add(EXE_FILTER);
    result.setSelectedExtensionFilter(EXE_FILTER);

    return result;
  }

  private FileChooser prepareFileChooser() {
    FileChooser result = new FileChooser();
    result.getExtensionFilters().add(DGI_FILTER);
    result.setSelectedExtensionFilter(DGI_FILTER);

    String destFileName = destinationField.getText();
    if (destFileName.isBlank()) {
      result.setInitialFileName(buildDestinationName());
      result.setInitialDirectory(getWorkspaceDestination());
      return result;
    }

    File location = new File(destFileName);
    result.setInitialFileName(location.getName());
    result.setInitialDirectory(location.getParentFile());
    return result;
  }

  private String buildDestinationName() {
    String repoName = repoNameField.textProperty().get();
    if (!repoName.isBlank()) {
      return buildTimestampName(repoName, DGI_EXT);
    }
    return buildTimestampName(DEFAULT_REPO_LABEL, DGI_EXT);
  }

  private File getWorkspaceDestination() {
    return DepanFxProjects.getCurrentGraphs(workspace);
  }

  private String buildTimestampName(String prefix, String ext) {
    return DepanFxWorkspaceFactory.buildDocumentTimestampName(
        prefix + " ", ext);
  }
}
