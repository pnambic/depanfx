package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
import com.pnambic.depanfx.perspective.DepanFxBaseToolDialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.MessageFormat;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@Component
@FxmlView("save-node-view-dialog.fxml")
public class DepanFxSaveNodeViewDialog
    extends DepanFxBaseToolDialog<DepanFxNodeViewData> {

  private static final String EXT = DepanFxNodeViewData.NODE_VIEW_TOOL_EXT;

  private static final ExtensionFilter EXT_FILTER =
      DepanFxSceneControls.buildExtFilter("Node View", EXT);

  @FXML
  private Label nodeViewDetailsLabel;

  private DepanFxNodeViewData viewDoc;

  @Autowired
  public DepanFxSaveNodeViewDialog(DepanFxWorkspace workspace) {
    super(workspace, DepanFxNodeViewData.class);
  }

  @Override
  public void setTooldata(DepanFxNodeViewData viewDoc) {
    super.setTooldata(viewDoc);
    this.viewDoc = viewDoc;
    nodeViewDetailsLabel.setText(buildDetailsLabel(viewDoc));
  }

  private String buildDetailsLabel(DepanFxNodeViewData viewDoc) {
    String graphSource =
        viewDoc.getGraphDocRsrc().getDocument().getMemberName();
    int nodeCount = viewDoc.getViewNodes().size();
    return MessageFormat.format("Node view from {0} with {1} nodes.",
        graphSource, nodeCount);
  }

  /////////////////////////////////////
  // Tool Dialog protected overrides

  @Override
  protected DepanFxNodeViewData prepareResult() {
    return DepanFxNodeViews.updateNameDescr(
        viewDoc, getToolName(), getToolDescription());
  }

  @Override
  protected File buildInitialDestinationFile() {
    return buildAnalysisInitialDestination(EXT);
  }

  @Override
  protected void setTooldataFilters(FileChooser result) {
    result.getExtensionFilters().add(EXT_FILTER);
    result.setSelectedExtensionFilter(EXT_FILTER);
  }

  @Override
  protected String getInputCheckFailureText() {
    return  "Node View Save Confirmation Error";
  }
}
