package com.pnambic.depanfx.nodeview.layouts;

import com.pnambic.depanfx.nodelist.gui.link.DepanFxLinkMatcherChooser;
import com.pnambic.depanfx.nodelist.gui.link.DepanFxLinkMatcherChooser.LinkMatcherControl;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLayoutData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxRadialLayoutData;
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
@FxmlView("radial-layout-tool-dialog.fxml")
public class DepanFxRadialLayoutToolDialog
    extends DepanFxBaseToolDialog<DepanFxRadialLayoutData> {

  public static final ExtensionFilter RADIAL_LAYOUT_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Radial Layout", DepanFxRadialLayoutData.RADIAL_LAYOUT_TOOL_EXT);

  public static final DepanFxResourceFilter RADIAL_LAYOUT_RSRC_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Radial Layout", DepanFxRadialLayoutData.RADIAL_LAYOUT_TOOL_EXT,
          DepanFxRadialLayoutData.class);

  private final DepanFxDialogRunner dialogRunner;

  @FXML
  private TextField hierarchyMatcherRsrcField;

  private LinkMatcherControl hierarchyMatcherControl;

  @Autowired
  public DepanFxRadialLayoutToolDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    super(workspace, DepanFxRadialLayoutData.class);
    this.dialogRunner = dialogRunner;
  }

  public static Dialog<DepanFxRadialLayoutToolDialog> runEditDialog(
      DepanFxProjectDocument projDoc,
      DepanFxRadialLayoutData radialLayoutData,
      DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runEditDialog(
        projDoc, radialLayoutData, dialogRunner,
        DepanFxRadialLayoutToolDialog.class,
        "Edit Radial Layout");
  }

  public static Dialog<DepanFxRadialLayoutToolDialog> runCreateDialog(
      DepanFxRadialLayoutData radialLayoutData, DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runCreateDialog(
        radialLayoutData, dialogRunner,
        DepanFxRadialLayoutToolDialog.class,
        "New Radial Layout");
  }

  @FXML
  public void initialize() {
    hierarchyMatcherControl =
        new DepanFxLinkMatcherChooser.LinkMatcherControl(
            getWorkspace(), dialogRunner, hierarchyMatcherRsrcField);

  }

  @Override // DepanFxBaseToolDialog
  public void setTooldata(DepanFxRadialLayoutData radialLayoutData) {
    super.setTooldata(radialLayoutData);

    hierarchyMatcherControl.setLinkMatcherRsrc(
        radialLayoutData.getHierarchyMatcherRsrc());
  }

  /////////////////////////////////////
  // Tool Dialog protected overrides

  @Override
  protected DepanFxRadialLayoutData prepareResult() {
    return new DepanFxRadialLayoutData(
        getToolName(), getToolDescription(),
        hierarchyMatcherControl.getLinkMatcherResource());
  }

  @Override
  protected File buildInitialDestinationFile() {
    return buildToolInitialDestination(
        DepanFxRadialLayoutData.RADIAL_LAYOUT_TOOL_EXT,
        DepanFxNodeViewLayoutData.LAYOUT_TOOL_PATH);
  }

  @Override
  protected void setTooldataFilters(FileChooser result) {
    result.getExtensionFilters().add(RADIAL_LAYOUT_FILTER);
    result.setSelectedExtensionFilter(RADIAL_LAYOUT_FILTER);
  }

  @Override
  protected String getInputCheckFailureText() {
    return  "Radial Layout Save Confirmation Error";
  }
}
