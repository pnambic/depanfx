package com.pnambic.depanfx.git.gui;

import com.google.common.base.Strings;
import com.pnambic.depanfx.filesystem.context.FileSystemContextDefinition;
import com.pnambic.depanfx.git.builder.GitCommandRunner;
import com.pnambic.depanfx.git.builder.GitLsFileLoader;
import com.pnambic.depanfx.git.tooldata.DepanFxGitRepoData;
import com.pnambic.depanfx.graph_doc.builder.DepanFxGraphModelBuilder;
import com.pnambic.depanfx.graph_doc.builder.SimpleGraphModelBuilder;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.graph_doc.model.GraphModel;
import com.pnambic.depanfx.perspective.DepanFxBaseDocumentDialog;
import com.pnambic.depanfx.perspective.graphdoc.GraphDocumentData;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxFxmlDialog;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.stage.FileChooser;

/**
 * Builds a new graph document from the file system components
 * of a git repository.
 */
@DepanFxFxmlDialog
@FxmlView("new-git-repo-dialog.fxml")
public class DepanFxNewGitRepoDialog
    extends DepanFxBaseDocumentDialog<GraphDocument> {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNewGitRepoDialog.class.getName());

  private final DepanFxDialogRunner dialogRunner;

  private DepanFxWorkspaceResource<DepanFxGitRepoData> repoRsrc;

  @FXML
  private TextField gitRepoNameField;

  @FXML
  private TextInputControl graphNameField;

  @FXML
  private TextInputControl graphDescriptionField;

  @Autowired
  public DepanFxNewGitRepoDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    super(workspace, GraphDocument.class);
    this.dialogRunner = dialogRunner;
  }

  @FXML
  public void initialize() {
    gitRepoNameField.setContextMenu(
        DepanFxGitRepoToolDialogs.buildRepoChoiceMenu(
            workspace, dialogRunner, gitRepoNameField.getScene(),
            () -> { return repoRsrc; }, this::setRepoResource));
    gitRepoNameField.textProperty().addListener(
        (observable, oldValue, newValue) -> updateGraphMetaFromDir(newValue));
  }

  public void setRepoResource(
      DepanFxWorkspaceResource<DepanFxGitRepoData> repoRsrc) {
    this.repoRsrc = repoRsrc;
    gitRepoNameField.setText(repoRsrc.getResource().getToolName());
  }

  @Override
  protected String getInputCheckFailureText() {
    return "Git Repository Graph Confirmation Error";
  }

  @Override
  protected String getDocumentName() {
    return guessBaseDestName();
  }

  @Override
  protected GraphDocument prepareResult() {
    return buildGraphDoc();
  }

  @Override
  protected void setTooldataFilters(FileChooser result) {
    result.getExtensionFilters().add(GraphDocumentData.GRAPH_DOC_FILTER);
    result.setSelectedExtensionFilter(GraphDocumentData.GRAPH_DOC_FILTER);
  }

  @Override
  protected File buildInitialDestinationFile() {
    return buildGraphInitialDestination(GraphDocument.GRAPH_DOC_EXT);
  }

  private void updateGraphMetaFromDir(String newValue) {
    if (graphNameField.getText().isBlank()) {
      String graphName = new File(newValue).getName();
      if (!Strings.isNullOrEmpty(graphName)) {
        graphNameField.setText(graphName + " repo");
      }
    }
    if (graphDescriptionField.getText().isBlank()) {
      String repoLabel = repoRsrc.getResource().getGitRepoPath().toString();
      String descr = "Graph of git repository from " + repoLabel;
      graphDescriptionField.setText(descr);
    }
  }

  private GraphDocument buildGraphDoc() {
    DepanFxGraphModelBuilder modelBuilder = new SimpleGraphModelBuilder();
    GitCommandRunner cmdRunner = new GitCommandRunner(repoRsrc.getResource());
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
    return repoRsrc.getResource().getToolName();
  }
}
