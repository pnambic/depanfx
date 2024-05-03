package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.perspective.DepanFxBaseDocumentDialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.MessageFormat;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@Component
@FxmlView("save-node-list-dialog.fxml")
public class DepanFxSaveNodeListDialog
    extends DepanFxBaseDocumentDialog<DepanFxNodeList> {

  private static final String EXT = DepanFxNodeList.NODE_LIST_EXT;

  public static final ExtensionFilter EXT_FILTER =
      DepanFxSceneControls.buildExtFilter("Node List", EXT);

  @FXML
  private Label nodeListDetailsLabel;

  @FXML
  private TextField nodeListNameField;

  @FXML
  private TextField nodeListDescriptionField;

  private Optional<DepanFxWorkspaceResource<DepanFxNodeList>> savedRsrc =
      Optional.empty();

  private DepanFxNodeList nodeList;

  @Autowired
  public DepanFxSaveNodeListDialog(DepanFxWorkspace workspace) {
    super(workspace, DepanFxNodeList.class);
  }

  public void setNodeListDoc(DepanFxNodeList nodeList) {
    this.nodeList = nodeList;
    nodeListNameField.setText(nodeList.getNodeListName());
    nodeListDescriptionField.setText(nodeList.getNodeListDescription());
    nodeListDetailsLabel.setText(buildDetailsLabel(nodeList));
  }

  private String buildDetailsLabel(DepanFxNodeList nodeList) {
    String graphSource =
        nodeList.getGraphDocResource().getDocument().getMemberName();
    int nodeCount = nodeList.getNodes().size();
    return MessageFormat.format("Node list from {0} with {1} nodes.",
        graphSource, nodeCount);
  }

  public Optional<DepanFxWorkspaceResource<DepanFxNodeList>> getSavedResource() {
    return savedRsrc;
  }

  /////////////////////////////////////
  // Base Document Dialog protected overrides

  @Override
  protected String getDocumentName() {
    return nodeListNameField.getText();
  }

  @Override
  protected DepanFxNodeList prepareResult() {
    return DepanFxNodeLists.buildNodeList(
        nodeListNameField.getText(), nodeListDescriptionField.getText(),
        nodeList.getGraphDocResource(), nodeList.getNodes());
  }

  @Override
  protected File buildInitialDestinationFile() {
    String docName = DepanFxWorkspaceFactory.buildDocumentTimestampName(
        getDocumentName(), EXT);
    return new File(
        DepanFxProjects.getCurrentAnalyzes(getWorkspace()), docName);
  }

  @Override
  protected void setTooldataFilters(FileChooser chooser) {
    chooser.getExtensionFilters().add(EXT_FILTER);
    chooser.setSelectedExtensionFilter(EXT_FILTER);
  }

  @Override
  protected String getInputCheckFailureText() {
    return "Node List Save Confirmation Error";
  }
}
