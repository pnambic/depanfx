package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.nodelist.tooldata.DepanFxFlatSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderBy;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderDirection;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

@Component
@FxmlView("flat-section-tool-dialog.fxml")
public class DepanFxFlatSectionToolDialog {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxFlatSectionToolDialog.class.getName());

  private static final ExtensionFilter FLAT_SECTION_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Flat Sections", DepanFxFlatSectionData.FLAT_SECTION_TOOL_EXT);

  private final DepanFxWorkspace workspace;

  private Optional<DepanFxWorkspaceResource> optFlatSectionRsrc;

  @FXML
  private TextField sectionLabelField;

  @FXML
  private CheckBox displayNodeCountField;

  @FXML
  private ComboBox<OrderBy> orderByField;

  @FXML
  private ComboBox<OrderDirection> orderDirectionField;

  @FXML
  private TextField toolNameField;

  @FXML
  private TextField toolDescriptionField;

  @FXML
  private TextField destinationField;

  @Autowired
  public DepanFxFlatSectionToolDialog(DepanFxWorkspace workspace) {
    this.workspace = workspace;
  }

  public static Dialog<DepanFxFlatSectionToolDialog> runEditDialog(
      DepanFxWorkspaceResource sectionDataRsrc,
      DepanFxDialogRunner dialogRunner, String title) {
    Dialog<DepanFxFlatSectionToolDialog> dlg =
        dialogRunner.createDialogAndParent(DepanFxFlatSectionToolDialog.class);
    dlg.getController().setWorkspaceResource(sectionDataRsrc);
    dlg.runDialog(title);
    return dlg;
  }

  public static Dialog<DepanFxFlatSectionToolDialog> runCreateDialog(
      DepanFxFlatSectionData sectionData,
      DepanFxDialogRunner dialogRunner, String title) {
    Dialog<DepanFxFlatSectionToolDialog> dlg =
        dialogRunner.createDialogAndParent(DepanFxFlatSectionToolDialog.class);
    dlg.getController().setTooldata(sectionData);
    dlg.runDialog(title);
    return dlg;
  }

  public static void setFlatSectionTooldataFilters(FileChooser result) {
    result.getExtensionFilters().add(FLAT_SECTION_FILTER);
    result.setSelectedExtensionFilter(FLAT_SECTION_FILTER);
  }

  @FXML
  public void initialize() {
    orderByField.getItems().add(OrderBy.NODE_LEAF);
    orderByField.getItems().add(OrderBy.NODE_KEY);
    orderByField.getItems().add(OrderBy.NODE_ID);

    orderDirectionField.getItems().add(OrderDirection.FORWARD);
    orderDirectionField.getItems().add(OrderDirection.REVERSE);
  }

  public void setTooldata(DepanFxFlatSectionData sectionData) {
    toolNameField.setText(sectionData.getToolName());
    toolDescriptionField.setText(sectionData.getToolDescription());

    sectionLabelField.setText(sectionData.getSectionLabel());
    displayNodeCountField.setSelected(sectionData.displayNodeCount());

    orderByField.setValue(sectionData.getOrderBy());
    orderDirectionField.setValue(sectionData.getOrderDirection());
  }

  public Optional<DepanFxFlatSectionData> getTooldata() {
    return optFlatSectionRsrc
        .map(DepanFxWorkspaceResource::getResource)
        .map(DepanFxFlatSectionData.class::cast);
  }

  public void setWorkspaceResource(DepanFxWorkspaceResource flatSectionRsrc) {
    this.optFlatSectionRsrc = Optional.of(flatSectionRsrc);
    setTooldata(((DepanFxFlatSectionData) flatSectionRsrc.getResource()));
    destinationField.setText(
        flatSectionRsrc.getDocument().getMemberPath().toString());
  }

  public Optional<DepanFxWorkspaceResource> getWorkspaceResource() {
    return optFlatSectionRsrc;
  }

  @FXML
  private void handleCancel() {
    closeDialog();
    optFlatSectionRsrc = Optional.empty();
  }

  @FXML
  private void handleConfirm() {
    closeDialog();

    DepanFxFlatSectionData flatSectionData = new DepanFxFlatSectionData(
        toolNameField.getText(), toolDescriptionField.getText(),
        sectionLabelField.getText(), displayNodeCountField.isSelected(),
        orderByField.getValue(), orderDirectionField.getValue());

    File dstDocFile = new File(destinationField.getText());
    optFlatSectionRsrc = workspace.toProjectDocument(dstDocFile.toURI())
        .flatMap(d -> saveDocument(d, flatSectionData));
  }

  @FXML
  private void openDestinationChooser() {
    FileChooser fileChooser = prepareDestinationFileChooser();
    File selectedFile =
        fileChooser.showSaveDialog(destinationField.getScene().getWindow());
    if (selectedFile != null) {
      destinationField.setText(selectedFile.getAbsolutePath());
    }
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

  private FileChooser prepareDestinationFileChooser() {
    FileChooser result =
        DepanFxSceneControls.prepareFileChooser(
            destinationField, () -> buildInitialDestinationFile());
    setFlatSectionTooldataFilters(result);
    return result;
  }

  private File buildInitialDestinationFile() {
    return DepanFxWorkspaceFactory.bestDocumentFile(
        toolNameField.getText(), DepanFxFlatSectionData.FLAT_SECTION_TOOL_EXT,
        workspace, DepanFxNodeListSectionData.SECTIONS_TOOL_PATH,
        DepanFxProjects.getCurrentTools(workspace));
  }
}
