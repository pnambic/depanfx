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
import com.pnambic.depanfx.scene.DepanFxFxmlDialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@DepanFxFxmlDialog
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
      DepanFxWorkspaceResource<DepanFxRadialLayoutData> radialLayoutRsrc,
      DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runEditDialog(
        radialLayoutRsrc, dialogRunner,
        DepanFxRadialLayoutToolDialog.class,
        "Edit Radial Layout");
  }

  public static Dialog<DepanFxRadialLayoutToolDialog> runCreateDialog(
      DepanFxWorkspaceResource<DepanFxRadialLayoutData> radialLayoutRsrc,
      DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runCreateDialog(
        radialLayoutRsrc, dialogRunner,
        DepanFxRadialLayoutToolDialog.class,
        "New Radial Layout");
  }

  @FXML
  public void initialize() {
    hierarchyMatcherControl =
        new DepanFxLinkMatcherChooser.LinkMatcherControl(
            getWorkspace(), dialogRunner, hierarchyMatcherRsrcField);

  }

  @Override
  public void setToolResource(
      DepanFxWorkspaceResource<DepanFxRadialLayoutData> toolRsrc) {
    super.setToolResource(toolRsrc);

    hierarchyMatcherControl.setLinkMatcherRsrc(
        toolRsrc.getResource().getHierarchyMatcherRsrc());
  }

  @FXML
  private void handleBrowseLinkMatcher() {
    hierarchyMatcherControl.runLinkMatcherFinder();
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
