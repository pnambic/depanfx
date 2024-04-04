package com.pnambic.depanfx.git.gui;

import com.pnambic.depanfx.git.tooldata.DepanFxGitRepoData;
import com.pnambic.depanfx.perspective.DepanFxBaseToolDialog;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

@Component
@FxmlView("git-repo-tool-dialog.fxml")
public class DepanFxGitRepoToolDialog
    extends DepanFxBaseToolDialog<DepanFxGitRepoData> {

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

  @FXML
  private TextField gitExeField;

  @FXML
  private TextField repoDirectoryField;

  @FXML
  private TextField repoNameField;

  @Autowired
  public DepanFxGitRepoToolDialog(DepanFxWorkspace workspace) {
    super(workspace, DepanFxGitRepoData.class);
  }

  /**
   * Modify an existing git repo tooldata with the git repo tool dialog.
   */
  public static Dialog<DepanFxGitRepoToolDialog> runEditDialog(
      DepanFxProjectDocument projDoc,
      DepanFxGitRepoData repoData,
      DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runEditDialog(
        projDoc, repoData, dialogRunner,
        DepanFxGitRepoToolDialog.class,
        "Edit git Repository");
  }

  /**
   * Create a new git repo tooldata with the git repo tool dialog.
   */
  public static Dialog<DepanFxGitRepoToolDialog> runCreateDialog(
      DepanFxGitRepoData repoData, DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runCreateDialog(
        repoData, dialogRunner,
        DepanFxGitRepoToolDialog.class,
        "Create git Repository");
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

  @Override
  public void setTooldata(DepanFxGitRepoData repoData) {
    super.setTooldata(repoData);

    gitExeField.setText(repoData.getGitExe());
    repoDirectoryField.setText(repoData.getGitRepoPath());
    repoNameField.setText(repoData.getGitRepoName());
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

  private void updateRepoNameFromDir(String newValue) {
    updateBlankField(repoNameField, newValue);
  }

  private void updateToolName(String newValue) {
    updateBlankField(toolNameField, newValue);
  }

  private void updateToolDescription(String newValue) {
    updateBlankField(repoNameField, DEFAULT_REPO_DESCRIPTION + newValue);
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

  /////////////////////////////////////
  // Tool Dialog protected overrides

  @Override
  protected DepanFxGitRepoData prepareResult() {
    return new DepanFxGitRepoData(
            toolNameField.getText(), toolDescriptionField.getText(),
            gitExeField.getText(), repoNameField.getText(),
            repoDirectoryField.getText());
  }

  @Override
  protected File buildInitialDestinationFile() {
    return DepanFxWorkspaceFactory.bestDocumentFile(
        DEFAULT_REPO_DESCRIPTION, DepanFxGitRepoData.GIT_REPO_TOOL_EXT,
        getWorkspace(), DepanFxGitRepoData.GIT_REPOS_TOOL_PATH,
        DepanFxProjects.getCurrentTools(getWorkspace()));
  }

  @Override
  protected void setColumnTooldataFilters(FileChooser result) {
    DepanFxGitRepoToolDialog.setGitRepoTooldataFilters(result);
  }

  @Override
  protected String getInputCheckFailureText() {
    return  "Git Repository Graph Save Confirmation Error";
  }
}
