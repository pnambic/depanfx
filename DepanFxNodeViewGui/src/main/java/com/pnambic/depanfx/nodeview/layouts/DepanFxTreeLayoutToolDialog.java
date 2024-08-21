package com.pnambic.depanfx.nodeview.layouts;

import com.pnambic.depanfx.nodelist.gui.link.DepanFxLinkMatcherChooser;
import com.pnambic.depanfx.nodelist.gui.link.DepanFxLinkMatcherChooser.LinkMatcherControl;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLayoutData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxTreeLayoutData;
import com.pnambic.depanfx.perspective.DepanFxBaseToolDialog;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@Component
@FxmlView("tree-layout-tool-dialog.fxml")
public class DepanFxTreeLayoutToolDialog
    extends DepanFxBaseToolDialog<DepanFxTreeLayoutData> {

  public static final ExtensionFilter TREE_LAYOUT_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Tree Layout", DepanFxTreeLayoutData.TREE_LAYOUT_TOOL_EXT);

  public static final DepanFxResourceFilter TREE_LAYOUT_RSRC_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Tree Layout", DepanFxTreeLayoutData.TREE_LAYOUT_TOOL_EXT,
          DepanFxTreeLayoutData.class);

  private final DepanFxDialogRunner dialogRunner;

  @FXML
  private TextField hierarchyMatcherRsrcField;

  private LinkMatcherControl hierarchyMatcherControl;

  @Autowired
  public DepanFxTreeLayoutToolDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    super(workspace, DepanFxTreeLayoutData.class);
    this.dialogRunner = dialogRunner;
  }

  public static Dialog<DepanFxTreeLayoutToolDialog> runEditDialog(
      DepanFxProjectDocument projDoc,
      DepanFxTreeLayoutData treeLayoutData,
      DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runEditDialog(
        projDoc, treeLayoutData, dialogRunner,
        DepanFxTreeLayoutToolDialog.class,
        "Edit Tree Layout");
  }

  public static Dialog<DepanFxTreeLayoutToolDialog> runCreateDialog(
      DepanFxTreeLayoutData treeLayoutData, DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runCreateDialog(
        treeLayoutData, dialogRunner,
        DepanFxTreeLayoutToolDialog.class,
        "New Tree Layout");
  }

  @FXML
  public void initialize() {
    hierarchyMatcherControl = new DepanFxLinkMatcherChooser.LinkMatcherControl(
        getWorkspace(), dialogRunner, hierarchyMatcherRsrcField);
  }

  @Override // DepanFxBaseColumnToolDialog
  public void setTooldata(DepanFxTreeLayoutData treeLayoutData) {
    super.setTooldata(treeLayoutData);

    hierarchyMatcherControl.setLinkMatcherRsrc(
        treeLayoutData.getHierarchyMatcherRsrc());
  }

  @FXML
  private void handleBrowseLinkMatcher() {
    hierarchyMatcherControl.runLinkMatcherFinder();
  }

  /////////////////////////////////////
  // Tool Dialog protected overrides

  @Override
  protected DepanFxTreeLayoutData prepareResult() {
    return new DepanFxTreeLayoutData(
        getToolName(), getToolDescription(),
        hierarchyMatcherControl.getLinkMatcherResource());
  }

  @Override
  protected File buildInitialDestinationFile() {
    return buildToolInitialDestination(
        DepanFxTreeLayoutData.TREE_LAYOUT_TOOL_EXT,
        DepanFxNodeViewLayoutData.LAYOUT_TOOL_PATH);
  }

  @Override
  protected void setTooldataFilters(FileChooser result) {
    result.getExtensionFilters().add(TREE_LAYOUT_FILTER);
    result.setSelectedExtensionFilter(TREE_LAYOUT_FILTER);
  }

  @Override
  protected String getInputCheckFailureText() {
    return  "Tree Layout Save Confirmation Error";
  }
}
