package com.pnambic.depanfx.git.gui;

import com.pnambic.depanfx.git.tooldata.DepanFxGitRepoData;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
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
import java.io.IOException;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.Window;

@Component
@FxmlView("git-repo-tool-dialog.fxml")
public class DepanFxGitRepoToolDialog {

  public interface Aware {

    /**
     * A window to parent the dialog.  Normally obtained from TextField for
     * the displayed value.
     */
    Window getChooserWindow();

    /** Provide the git repo data from the aware component. */
    DepanFxGitRepoData getTooldata();

    /** Update the aware component with new git repo data. */
    void setTooldata(DepanFxGitRepoData repoData);
  } 

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxGitRepoToolDialog.class.getName());

  private static final ExtensionFilter GIT_REPO_TOOL_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Git Repo Tool", DepanFxGitRepoData.GIT_REPO_TOOL_EXT);

  // For executables (on Windows)
  private static final String EXE_EXT = "exe";

  private static final ExtensionFilter EXE_FILTER =
      DepanFxSceneControls.buildExtFilter("Executable", EXE_EXT);

  private static final String DEFAULT_REPO_DESCRIPTION = "Git repository ";

  private final DepanFxWorkspace workspace;

  Optional<DepanFxWorkspaceResource> optGitRepoRsrc;

  @FXML
  private TextField gitExeField;

  @FXML
  private TextField repoDirectoryField;

  @FXML
  private TextField repoNameField;

  @FXML
  private TextField toolNameField;

  @FXML
  private TextField toolDescriptionField;

  @FXML
  private TextField destinationField;

  @Autowired
  public DepanFxGitRepoToolDialog(DepanFxWorkspace workspace) {
    this.workspace = workspace;
  }

  /**
   * Modify an existing git repo tooldata with the git repo tool dialog.
   */
  public static void runEditDialog(
      DepanFxProjectDocument projDoc,
      DepanFxGitRepoData repoData,
      DepanFxDialogRunner dialogRunner) {
    Dialog<DepanFxGitRepoToolDialog> repoChooser =
        dialogRunner.createDialogAndParent(DepanFxGitRepoToolDialog.class);
    repoChooser.getController().setTooldata(repoData);
    repoChooser.getController().setDestination(projDoc);
    repoChooser.runDialog("Edit git Repository");
  }

  /**
   * Create a new git repo tooldata with the git repo tool dialog.
   */
  public static Dialog<DepanFxGitRepoToolDialog> runCreateDialog(
      DepanFxGitRepoData repoData, DepanFxDialogRunner dialogRunner) {
    Dialog<DepanFxGitRepoToolDialog> result =
        dialogRunner.createDialogAndParent(DepanFxGitRepoToolDialog.class);

    result.getController().setTooldata(repoData);
    result.runDialog("Create git Repository");
    return result;
  }

  public static void setGitRepoTooldataFilters(FileChooser result) {
    result.getExtensionFilters().add(GIT_REPO_TOOL_FILTER);
    result.setSelectedExtensionFilter(GIT_REPO_TOOL_FILTER);
  }

  @FXML
  public void initialize() {
    repoDirectoryField.textProperty().addListener(
        (observable, oldValue, newValue) -> updateRepoNameFromDir(newValue));
    repoNameField.textProperty().addListener(
        (observable, oldValue, newValue) -> updateToolName(newValue));
    repoNameField.textProperty().addListener(
        (observable, oldValue, newValue) -> updateToolDescription(newValue));
  }

  public void setTooldata(DepanFxGitRepoData repoData) {
    gitExeField.setText(repoData.getGitExe());
    repoDirectoryField.setText(repoData.getGitRepoPath());
    repoNameField.setText(repoData.getGitRepoName());
    toolNameField.setText(repoData.getToolName());
    toolDescriptionField.setText(repoData.getToolDescription());
    gitExeField.setText(repoData.getGitExe());
  }

  public void setDestination(DepanFxProjectDocument projDoc) {
    destinationField.setText(projDoc.getMemberPath().toString());
  }

  public Optional<DepanFxWorkspaceResource> getWorkspaceResource() {
    return optGitRepoRsrc;
  }

  @FXML
  private void openGitExeChooser() {
    FileChooser fileChooser = prepareGitExeChooser();
    File selectedFile =
        fileChooser.showOpenDialog(gitExeField.getScene().getWindow());
    if (selectedFile != null) {
      gitExeField.setText(selectedFile.getAbsolutePath());
    }
  }

  @FXML
  private void openRepoDirectoryChooser() {
    DirectoryChooser directoryChooser = prepareRepoDirectoryChooser();
    File selectedDirectory =
        directoryChooser.showDialog(repoDirectoryField.getScene().getWindow());
    if (selectedDirectory != null) {
      repoDirectoryField.setText(selectedDirectory.getAbsolutePath());
    }
  }

  @FXML
  private void openFileChooser() {
    FileChooser fileChooser = prepareDestinationFileChooser();
    File selectedFile =
        fileChooser.showSaveDialog(destinationField.getScene().getWindow());
    if (selectedFile != null) {
      destinationField.setText(selectedFile.getAbsolutePath());
    }
  }

  @FXML
  private void handleCancel() {
    closeDialog();
    optGitRepoRsrc = Optional.empty();
  }

  @FXML
  private void handleConfirm() {
    closeDialog();

    DepanFxGitRepoData repoData = new DepanFxGitRepoData(
        toolNameField.getText(), toolDescriptionField.getText(),
        gitExeField.getText(), repoNameField.getText(),
        repoDirectoryField.getText());

    File dstDocFile = new File(destinationField.getText());
    optGitRepoRsrc = workspace.toProjectDocument(dstDocFile.toURI())
        .flatMap(d -> saveDocument(d, repoData));
  }

  private Optional<DepanFxWorkspaceResource> saveDocument(
      DepanFxProjectDocument projDoc, Object docData) {
    try {
      return workspace.saveDocument(projDoc, docData);
    } catch (IOException errIo) {
      LOG.error("Unable to save {}, type {}",
          projDoc.toString(), docData.getClass().getName(), errIo);
      throw new RuntimeException(
          "Unable to save " + projDoc.toString()
          + ", type " + docData.getClass().getName(),
          errIo);
    }
  }

  private void closeDialog() {
    ((Stage) destinationField.getScene().getWindow()).close();
  }

  private void updateRepoNameFromDir(String newValue) {
    if (repoNameField.getText().isBlank()) {
      String repoName = new File(newValue).getName();
      if (!repoName.isBlank()) {
        repoNameField.setText(repoName);
      }
    }
  }

  private void updateToolName(String newValue) {
    updateBlankField(toolNameField, newValue);
  }

  private void updateToolDescription(String newValue) {
    if (toolDescriptionField.getText().isBlank()) {
      if (!newValue.isBlank()) {
        toolDescriptionField.setText(DEFAULT_REPO_DESCRIPTION + newValue);
      }
    }
  }

  private FileChooser prepareGitExeChooser() {
    FileChooser result =
        DepanFxSceneControls.prepareFileChooser(gitExeField);
    result.getExtensionFilters().add(EXE_FILTER);
    result.setSelectedExtensionFilter(EXE_FILTER);
    return result;
  }

  private DirectoryChooser prepareRepoDirectoryChooser() {
    return DepanFxSceneControls
        .prepareDirectoryChooser(repoDirectoryField);
  }

  private FileChooser prepareDestinationFileChooser() {
    FileChooser result =
        DepanFxSceneControls.prepareFileChooser(
            destinationField, () -> buildInitialDestinationFile());
    setGitRepoTooldataFilters(result);
    return result;
  }

  private void updateBlankField(TextField updateField, String newValue) {
    DepanFxSceneControls.updateBlankField(updateField, newValue);
  }

  private File buildInitialDestinationFile() {
    return DepanFxWorkspaceFactory.bestDocumentFile(
        DEFAULT_REPO_DESCRIPTION, DepanFxGitRepoData.GIT_REPO_TOOL_EXT,
        workspace, DepanFxGitRepoData.GIT_REPOS_TOOL_PATH,
        DepanFxProjects.getCurrentTools(workspace));
  }
}
