package com.pnambic.depanfx.git.gui;

import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceChooser;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class DepanFxGraphDocDialogs {

  public static final DepanFxResourceFilter GRAPH_INFO_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Graph Info",
          GraphDocument.GRAPH_DOC_EXT,
          GraphDocument.class);

  /**
   * The supplied {@code initialPath} should be convertible into a
   * workspace member via {@code toProjectDocument()}.
   */
  public static Optional<DepanFxWorkspaceResource<GraphDocument>>
      runOpenGraphDocChooser(
          DepanFxWorkspace workspace,
          DepanFxDialogRunner dialogRunner,
          Scene scene,
          String initialPath) {

    DepanFxResourceChooser chooser = prepareChooser(
        workspace, dialogRunner, initialPath);
    return chooser.showOpenDialog(scene)
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(p -> workspace.getWorkspaceResource(
            p, GraphDocument.class));
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
        DepanFxWorkspaceFactory.buildDocumentTimestampName(
            baseName, GraphDocument.GRAPH_DOC_EXT);
    return new File(DepanFxProjects.getCurrentGraphs(wksp), graphFilename);
  }

  /**
   * Provides a {@link DepanFxResourceChooser} initialized for working with
   * graph documents.  The supplied text field provides an initial directory
   * and file name if not blank.  The extension filters are configured for
   * graph documents.
   */
  private static DepanFxResourceChooser prepareChooser(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      String initialPath) {
    DepanFxResourceChooser result =
        new DepanFxResourceChooser(workspace, dialogRunner);
    DepanFxResourcePerspectives.prepareResourceFinder(
        result, DepanFxProjects.TOOLS_PATH);
    result.getExtensionFilters().add(GRAPH_INFO_FILTER);
    result.setSelectedExtensionFilter(GRAPH_INFO_FILTER);

    Optional<DepanFxProjectDocument> optDoc =
        workspace.toProjectDocument(Path.of(initialPath).toUri());
    if (optDoc.isPresent()) {
      DepanFxProjectDocument initDoc = optDoc.get();
      result.setInitialResourceName(initDoc.getMemberName());
      initDoc.getParent().ifPresent(result::setInitialContainer);
      return result;
    }

    Optional<DepanFxProjectContainer> optDir =
        workspace.toProjectContainer(Path.of(initialPath).toUri());
    if (optDir.isPresent()) {
      optDir.ifPresent(result::setInitialContainer);
      return result;
    }
    DepanFxProjects.getCurrentGraphsDir(workspace)
        .ifPresent(result::setInitialContainer);
    return result;
  }
}
