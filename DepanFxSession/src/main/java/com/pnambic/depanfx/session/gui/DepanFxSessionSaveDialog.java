package com.pnambic.depanfx.session.gui;

import com.pnambic.depanfx.persistence.PersistDocumentTransport;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.persistence.plugins.GraphNodePersistencePluginRegistry;
import com.pnambic.depanfx.perspective.DepanFxDialogChecks;
import com.pnambic.depanfx.perspective.DepanFxProctor;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneController;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.session.core.DepanFxSession;
import com.pnambic.depanfx.session.plugins.DepanFxSceneViewerRegistry;
import com.pnambic.depanfx.session.tooldata.DepanFxProjectData;
import com.pnambic.depanfx.session.tooldata.DepanFxSceneData;
import com.pnambic.depanfx.session.tooldata.DepanFxSessionData;
import com.pnambic.depanfx.session.viewdata.DepanFxBaseViewerData;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

@Component
@FxmlView("session-save-dialog.fxml")
public class DepanFxSessionSaveDialog {

  private static final String DEPAN_FX_SESSION_LABEL = "DepanFX Session";

  public static final String YAML_EXT = "yml";

  private static final ExtensionFilter YAML_FILTER =
      DepanFxSceneControls.buildExtFilter("Session", YAML_EXT);

  private static final Class<?>[] ALLOWED_TYPES = null;

  private final DepanFxSession session;

  private final DepanFxSceneViewerRegistry viewerRegistry;

  private final GraphNodePersistencePluginRegistry graphNodeRegistry;

  @FXML
  private Label sessionDetailsLabel;

  @FXML
  private TextField destinationField;

  @Autowired
  public DepanFxSessionSaveDialog(
      DepanFxSession session, DepanFxSceneViewerRegistry viewerRegistry,
      GraphNodePersistencePluginRegistry graphNodeRegistry) {
    this.session = session;
    this.viewerRegistry = viewerRegistry;
    this.graphNodeRegistry = graphNodeRegistry;
  }

  public static Dialog<DepanFxSessionSaveDialog> runSaveSessionDialog(
      DepanFxDialogRunner dialogRunner) {
    Dialog<DepanFxSessionSaveDialog> dlg =
        dialogRunner.createDialogAndParent(DepanFxSessionSaveDialog.class);
    dlg.runDialog("Save Session");
    return dlg;
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
  }

  @FXML
  private void handleConfirm() {
    DepanFxProctor proctor = new DepanFxProctor.Simple();
    DepanFxDialogChecks.checkDestinationFile(
        proctor, destinationField.getText());
    if (DepanFxResourcePerspectives.errorAlert(
        proctor, "Session Sava Confirmation Error")) {
      return;
    }

    closeDialog();

    File dstFile = new File(destinationField.getText());
    DepanFxSessionData sessionData = buildSessionData(session);
    saveSessionData(dstFile, sessionData);
  }

  private void closeDialog() {
    ((Stage) destinationField.getScene().getWindow()).close();
  }

  private FileChooser prepareFileChooser() {
    String baseName = DEPAN_FX_SESSION_LABEL;
    FileChooser result =
        DepanFxSceneControls.prepareFileChooser(
            destinationField,
            () -> new File(
                buildTimestampName(baseName, YAML_EXT)));
    result.getExtensionFilters().add(YAML_FILTER);
    result.setSelectedExtensionFilter(YAML_FILTER);
  
    return result;
  }

  private String buildTimestampName(String prefix, String ext) {
    return DepanFxWorkspaceFactory.buildDocumentTimestampName(prefix, ext);
  }

  private DepanFxSessionData buildSessionData(DepanFxSession session) {
    List<DepanFxProjectData> projectInfo = buildSessionProjects(session);
    Collection<DepanFxSceneData> sceneInfo = buildSessionScenes(session);
    return new DepanFxSessionData(
        "DepanFX Session", "DepanFX session.", projectInfo, sceneInfo);
  }

  private List<DepanFxProjectData> buildSessionProjects(
      DepanFxSession session) {
    DepanFxProjectTree builtInTree = session.getWorkspace().getBuiltInProjectTree();

    return session.getWorkspace().getProjectList().stream()
        .filter(p -> p != builtInTree)
        .map(this::buildProjectData)
        .toList();
  }

  private DepanFxProjectData buildProjectData(DepanFxProjectTree tree) {
    return new DepanFxProjectData(
      tree.getMemberName(), "DepanFX project.", tree.getMemberPath());
  }

  private Collection<DepanFxSceneData> buildSessionScenes(
      DepanFxSession session) {
    return session.getScenes().stream()
        .map(c -> buildSceneData(c))
        .collect(Collectors.toList());
  }

  private DepanFxSceneData buildSceneData(DepanFxSceneController scene) {
    List<DepanFxBaseViewerData> viewersInfo = scene.streamViewers()
        .flatMap(v -> viewerRegistry.getViewerData(v).stream())
        .collect(Collectors.toList());

    return new DepanFxSceneData("DepanFX", "DepanFX scene.", viewersInfo);
  }

  private void saveSessionData(File dstFile, DepanFxSessionData sectionInfo) {

    PersistDocumentTransportBuilder transportBuilder = 
        new PersistDocumentTransportBuilder();
    transportBuilder.addImplicitCollection(DepanFxSessionData.class, "projects");
    transportBuilder.addImplicitCollection(DepanFxSessionData.class, "scenes");
    transportBuilder.addAllowedType(ALLOWED_TYPES);
    graphNodeRegistry.applyExtensions(
        transportBuilder, DepanFxWorkspaceResource.class);

    PersistDocumentTransport transport =
        transportBuilder.buildDocumentXmlPersist();

    transport.addContextValue(DepanFxWorkspace.class, this);

    try (Writer saver = openForSave(dstFile)) {
      transport.save(saver, sectionInfo);
    } catch (Exception errAny) {
      throw new RuntimeException(
          "Unable to save section " + sectionInfo.getToolName(),
          errAny);
    }
  }

  private Writer openForSave(File dstFile) throws IOException {
    return new FileWriter(dstFile);
  }
}
