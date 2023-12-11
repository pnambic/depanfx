package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderBy;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderDirection;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxTreeSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxTreeSectionData.ContainerOrder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectResource;
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
@FxmlView("tree-section-tool-dialog.fxml")
public class DepanFxTreeSectionToolDialog {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxTreeSectionToolDialog.class.getName());

  private static final ExtensionFilter TREE_SECTION_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Tree Sections", DepanFxTreeSectionData.TREE_SECTION_TOOL_EXT);

  private static final ExtensionFilter LINK_MATCHER_FILTER = null;

  private final DepanFxWorkspace workspace;

  // Holds the link matcher reference.
  private DepanFxTreeSectionData sectionData;

  private Optional<DepanFxWorkspaceResource> optTreeSectionRsrc;

  @FXML
  private TextField sectionLabelField;

  @FXML
  private CheckBox displayNodeCountField;

  @FXML
  private CheckBox inferMissingParentsField;

  @FXML
  private ComboBox<OrderBy> orderByField;

  @FXML
  private ComboBox<ContainerOrder> containerOrderField;

  @FXML
  private ComboBox<OrderDirection> orderDirectionField;

  @FXML
  private TextField linkMatcherResourceField;

  @FXML
  private TextField toolNameField;

  @FXML
  private TextField toolDescriptionField;

  @FXML
  private TextField destinationField;

  @Autowired
  public DepanFxTreeSectionToolDialog(DepanFxWorkspace workspace) {
    this.workspace = workspace;
  }

  public static void runEditDialog(
      DepanFxWorkspaceResource wkspRsrc,
      DepanFxDialogRunner dialogRunner, String title) {
    Dialog<DepanFxTreeSectionToolDialog> dlg =
        dialogRunner.createDialogAndParent(DepanFxTreeSectionToolDialog.class);
    dlg.getController().setWorkspaceResource(wkspRsrc);
    dlg.runDialog(title);
  }

  public static void runCreateDialog(
      DepanFxTreeSectionData toolData,
      DepanFxDialogRunner dialogRunner, String title) {
    Dialog<DepanFxTreeSectionToolDialog> dlg =
        dialogRunner.createDialogAndParent(DepanFxTreeSectionToolDialog.class);
    dlg.getController().setTooldata(toolData);
    dlg.runDialog(title);
  }

  public static void setTreeSectionTooldataFilters(FileChooser result) {
    result.getExtensionFilters().add(TREE_SECTION_FILTER);
    result.setSelectedExtensionFilter(TREE_SECTION_FILTER);
  }

  @FXML
  public void initialize() {
    orderByField.getItems().add(OrderBy.NODE_LEAF);
    orderByField.getItems().add(OrderBy.NODE_KEY);
    orderByField.getItems().add(OrderBy.NODE_ID);

    containerOrderField.getItems().add(ContainerOrder.FIRST);
    containerOrderField.getItems().add(ContainerOrder.LAST);
    containerOrderField.getItems().add(ContainerOrder.MIXED);

    orderDirectionField.getItems().add(OrderDirection.FORWARD);
    orderDirectionField.getItems().add(OrderDirection.REVERSE);
  }

  public void setTooldata(DepanFxTreeSectionData sectionData) {
    toolNameField.setText(sectionData.getToolName());
    toolDescriptionField.setText(sectionData.getToolDescription());

    sectionLabelField.setText(sectionData.getSectionLabel());
    displayNodeCountField.setSelected(sectionData.displayNodeCount());

    linkMatcherResourceField.setText(
        sectionData.getLinkMatcherRsrc(workspace).getDocument().toString());
    inferMissingParentsField.setSelected(sectionData.inferMissingParents());

    orderByField.setValue(sectionData.getOrderBy());
    containerOrderField.setValue(sectionData.getContainerOrder());
    orderDirectionField.setValue(sectionData.getOrderDirection());

    this.sectionData = sectionData;
  }

  public Optional<DepanFxTreeSectionData> getTooldata() {
    return optTreeSectionRsrc.map(DepanFxWorkspaceResource::getResource)
      .map(DepanFxTreeSectionData.class::cast);
  }

  public void setWorkspaceResource(DepanFxWorkspaceResource treeSectionRsrc) {
    this.optTreeSectionRsrc = Optional.of(treeSectionRsrc);
    setTooldata(((DepanFxTreeSectionData) treeSectionRsrc.getResource()));
    destinationField.setText(
        treeSectionRsrc.getDocument().getMemberPath().toString());
  }

  public Optional<DepanFxWorkspaceResource> getWorkspaceResource() {
    return optTreeSectionRsrc;
  }

  @FXML
  private void handleCancel() {
    closeDialog();
    optTreeSectionRsrc = Optional.empty();
  }

  @FXML
  private void handleConfirm() {
    closeDialog();

    DepanFxProjectResource linkMatcherRsrc =
        DepanFxProjectResource.fromWorkspaceResource(
            sectionData.getLinkMatcherRsrc(workspace));
    DepanFxTreeSectionData treeSectionData = new DepanFxTreeSectionData(
        toolNameField.getText(), toolDescriptionField.getText(),
        sectionLabelField.getText(), displayNodeCountField.isSelected(),
        linkMatcherRsrc , inferMissingParentsField.isSelected(),
        orderByField.getValue(), containerOrderField.getValue(),
        orderDirectionField.getValue());

    File dstDocFile = new File(destinationField.getText());
    optTreeSectionRsrc = workspace.toProjectDocument(dstDocFile.toURI())
        .flatMap(d -> saveDocument(d, treeSectionData));
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

  private FileChooser prepareLinkMatcherChooser() {
    FileChooser result =
        DepanFxSceneControls.prepareFileChooser(linkMatcherResourceField);
    result.getExtensionFilters().add(LINK_MATCHER_FILTER);
    result.setSelectedExtensionFilter(LINK_MATCHER_FILTER);
    return result;
  }

  private FileChooser prepareDestinationFileChooser() {
    FileChooser result =
        DepanFxSceneControls.prepareFileChooser(
            destinationField, () -> buildInitialDestinationFile());
    setTreeSectionTooldataFilters(result);
    return result;
  }

  private File buildInitialDestinationFile() {
    return DepanFxWorkspaceFactory.bestDocumentFile(
        toolNameField.getText(), DepanFxTreeSectionData.TREE_SECTION_TOOL_EXT,
        workspace, DepanFxNodeListSectionData.SECTIONS_TOOL_PATH,
        DepanFxProjects.getCurrentTools(workspace));
  }
}
