package com.pnambic.depanfx.git.gui;

import com.pnambic.depanfx.git.builder.GitCommandRunner;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class DepanFxGitDialogUtils {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxGitDialogUtils.class.getName());

  // For executables (on Windows)
  private static final String EXE_EXT = "exe";

  private static final ExtensionFilter EXE_FILTER =
      new ExtensionFilter("Executabe (*.exe)", "*." + EXE_EXT);

  // For Graph documents
  public static final String DGI_EXT = "dgi";

  private static final ExtensionFilter DGI_FILTER =
      new ExtensionFilter("Graph Info (*.dgi)", "*." + DGI_EXT);

  private static final String DEFAULT_REPO_LABEL = "Git Repo";

  private DepanFxGitDialogUtils() {
    // Prevent instantiation.
  }

  public static void initializeGitExit(TextField gitExeField) {
    if (gitExeField.getText().isBlank()) {
      gitExeField.setText(GitCommandRunner.DEFAULT_GIT_EXE);
    }
  }

  public static String getRepoName(TextField repoNameField) {
    String repoName = repoNameField.textProperty().get();
    if (!repoName.isBlank()) {
      return repoName;
    }
    return DEFAULT_REPO_LABEL;
  }

  public static GitCommandRunner createCommandRunner(
      TextField gitExeField,
      TextField repoNameField,
      TextField repoDirectoryField) {
    return new GitCommandRunner(
            gitExeField.getText(),
            getRepoName(repoNameField),
            repoDirectoryField.getText());
  }

  public static void runGitExeChooser(TextField gitExeField) {
    FileChooser fileChooser = prepareGitExeChooser(gitExeField);
    File selectedFile =
        fileChooser.showOpenDialog(gitExeField.getScene().getWindow());
    if (selectedFile != null) {
      gitExeField.setText(selectedFile.getAbsolutePath());
    }
  }

  public static void runRepoDirectoryChooser(
      TextField repoDirectoryField, TextInputControl repoNameField) {
    DirectoryChooser directoryChooser =
        prepareRepoDirectoryChooser(repoDirectoryField);
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

  public static void runGraphDocChooser(
      TextField graphDocumentField, DepanFxWorkspace workspace) {
    FileChooser fileChooser = prepareGraphDocChooser(
        graphDocumentField, workspace);
    File selectedFile =
        fileChooser.showOpenDialog(graphDocumentField.getScene().getWindow());
    if (selectedFile != null) {
      graphDocumentField.setText(selectedFile.getAbsolutePath());
    }
  }

  /**
   * Provides a {@link FileChooser} initialized for working with graph
   * documents.  The supplied text field provides an initial directory and
   * file name if not blank.  The extension filters are configuration for
   * graph documents.
   *
   * The result can be used for file open or save operations.
   *
   * Further customization of the {@link FileChooser} is expected.
   * @param workspace 
   */
  public static FileChooser prepareGraphDocChooser(
      TextField graphDocumentField, DepanFxWorkspace workspace) {
    FileChooser result = prepareFileChooser(graphDocumentField);
    result.getExtensionFilters().add(DGI_FILTER);
    result.setSelectedExtensionFilter(DGI_FILTER);

    String graghDocName = graphDocumentField.getText();
    if (!graghDocName.isBlank()) {
      File location = new File(graghDocName);
      result.setInitialDirectory(location.getParentFile());
      return result;
    }

    result.setInitialDirectory(DepanFxProjects.getCurrentGraphs(workspace));
    return result;
  }

  private static FileChooser prepareGitExeChooser(TextField gitExeField) {
    FileChooser result = prepareFileChooser(gitExeField);
    result.getExtensionFilters().add(EXE_FILTER);
    result.setSelectedExtensionFilter(EXE_FILTER);
    return result;
  }

  private static DirectoryChooser prepareRepoDirectoryChooser(
      TextField repoDirectoryField) {
    DirectoryChooser result = new DirectoryChooser();
    String destFileName = repoDirectoryField.getText();
    if (!destFileName.isBlank()) {
      File location = new File(destFileName);
      result.setInitialDirectory(location.getParentFile());
      return result;
    }
    return result;
  }

  private static FileChooser prepareFileChooser(TextField fileField) {
    FileChooser result = new FileChooser();

    String fileName = fileField.getText();
    if (!fileName.isBlank()) {
      File location = new File(fileName);
      result.setInitialFileName(location.getName());
      result.setInitialDirectory(location.getParentFile());
      return result;
    }
    return result;
  }
}
