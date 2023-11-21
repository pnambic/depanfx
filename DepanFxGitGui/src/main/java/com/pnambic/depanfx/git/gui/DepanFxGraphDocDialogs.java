package com.pnambic.depanfx.git.gui;

import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.io.File;

import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class DepanFxGraphDocDialogs {

  // For Graph documents
  public static final String DGI_EXT = "dgi";

  private static final ExtensionFilter DGI_FILTER =
      new ExtensionFilter("Graph Info (*.dgi)", "*." + DGI_EXT);

  public static void runOpenGraphDocFileChooser(
      TextField graphDocumentField, DepanFxWorkspace workspace) {
    FileChooser fileChooser =
        prepareGraphDocFileChooser(graphDocumentField, workspace);
    File selectedFile =
        fileChooser.showOpenDialog(graphDocumentField.getScene().getWindow());
    if (selectedFile != null) {
      graphDocumentField.setText(selectedFile.getAbsolutePath());
    }
  }

  public static void runSaveGraphDocFileChooser(
      TextField graphDocumentField,
      String baseGraphName,
      DepanFxWorkspace workspace) {
    FileChooser fileChooser = prepareSaveGraphDocFileChooser(
        graphDocumentField, baseGraphName, workspace);
    File selectedFile =
        fileChooser.showSaveDialog(graphDocumentField.getScene().getWindow());
    if (selectedFile != null) {
      graphDocumentField.setText(selectedFile.getAbsolutePath());
    }
  }

  private static FileChooser prepareSaveGraphDocFileChooser(
      TextField graphDocumentField,
      String baseGraphName,
      DepanFxWorkspace workspace) {
    return DepanFxSceneControls.prepareFileChooser(graphDocumentField,
        () -> buildDestinationName(workspace, baseGraphName));
  }

  private static File buildDestinationName(
      DepanFxWorkspace wksp, String baseName) {
    String graphFilename =
        DepanFxWorkspaceFactory.buildDocumentTimestampName(baseName, DGI_EXT);
    return new File(DepanFxProjects.getCurrentGraphs(wksp), graphFilename);
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
  private static FileChooser prepareGraphDocFileChooser(
      TextField graphDocumentField, DepanFxWorkspace workspace) {
    FileChooser result =
        DepanFxSceneControls.prepareFileChooser(graphDocumentField);
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
}
