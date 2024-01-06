package com.pnambic.depanfx.git.gui;

import com.google.common.base.Strings;
import com.pnambic.depanfx.filesystem.context.FileSystemContextDefinition;
import com.pnambic.depanfx.git.builder.GitCommandRunner;
import com.pnambic.depanfx.git.builder.GitLsFileLoader;
import com.pnambic.depanfx.git.tooldata.DepanFxGitRepoData;
import com.pnambic.depanfx.graph.model.GraphModel;
import com.pnambic.depanfx.graph_doc.builder.DepanFxGraphModelBuilder;
import com.pnambic.depanfx.graph_doc.builder.SimpleGraphModelBuilder;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
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
  private TextInputControl graphNameField;

  @FXML
  private TextInputControl graphDescriptionField;

  @FXML
  private TextField destinationField;

  private Optional<DepanFxWorkspaceResource> graphDocRsrc = Optional.empty();

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

  public Optional<DepanFxWorkspaceResource> getGraphDocRsrc() {
    return graphDocRsrc;
  }

  @FXML
  public void initialize() {
    gitRepoNameField.setContextMenu(
        DepanFxGitRepoToolDialogs.buildRepoChoiceMenu(
            this, workspace, dialogRunner));
    gitRepoNameField.textProperty().addListener(
        (observable, oldValue, newValue) -> updateGraphMetaFromDir(newValue));
  }

  @FXML
  private void openDestinationChooser() {
    DepanFxGraphDocDialogs.runSaveGraphDocFileChooser(
        destinationField, guessBaseDestName(), workspace);
  }

  @FXML
  private void handleCancel() {
    closeDialog();
    LOG.info("cancelled request");
  }

  @FXML
  private void handleConfirm() {
    closeDialog();

    DepanFxProjectDocument projDoc =
        DepanFxResourcePerspectives.toProjDoc(workspace, destinationField)
        .get();

    try {
      GraphDocument graphDoc = buildGraphDoc();
      graphDocRsrc = workspace.saveDocument(projDoc, graphDoc);
    } catch (Exception errAny) {
      LOG.error("Unable to save {}", projDoc, errAny);
    }
  }

  private void closeDialog() {
    ((Stage) destinationField.getScene().getWindow()).close();
  }

  private void updateGraphMetaFromDir(String newValue) {
    if (graphNameField.getText().isBlank()) {
      String graphName = new File(newValue).getName();
      if (!Strings.isNullOrEmpty(graphName)) {
        graphNameField.setText(graphName + " repo");
      }
    }
    if (graphDescriptionField.getText().isBlank()) {
      String descr = "Graph of git repository from "
          + repoData.getGitRepoPath().toString();
      graphDescriptionField.setText(descr);
    }
  }

  private GraphDocument buildGraphDoc() {
    DepanFxGraphModelBuilder modelBuilder = new SimpleGraphModelBuilder();
    GitCommandRunner cmdRunner = new GitCommandRunner(repoData);
    analyzeRepo(modelBuilder, cmdRunner);

    GraphModel graphModel = modelBuilder.createGraphModel();
    String graphName = graphNameField.getText();
    String graphDescr = graphDescriptionField.getText();

    return new GraphDocument(graphName, graphDescr,
        FileSystemContextDefinition.MODEL_ID, graphModel);
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

  private String guessBaseDestName() {
    String graphName = graphNameField.getText();
    if (!Strings.isNullOrEmpty(graphName)) {
      return graphName;
    }
    return repoData.getToolName();
  }
}
