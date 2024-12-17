package com.pnambic.depanfx.nodelist.gui.sections;

import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxFlatSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListSectionData.OrderBy;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@Component
@FxmlView("flat-section-tool-dialog.fxml")
public class DepanFxFlatSectionToolDialog
    extends DepanFxBaseSectionToolDialog<DepanFxFlatSectionData> {

  private static final ExtensionFilter FLAT_SECTION_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Flat Sections", DepanFxFlatSectionData.FLAT_SECTION_TOOL_EXT);

  public static final DepanFxResourceFilter FLAT_SECTION_RSRC_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Flat Sections",
          DepanFxFlatSectionData.FLAT_SECTION_TOOL_EXT,
          DepanFxFlatSectionData.class);

  @FXML
  private ComboBox<OrderBy> orderByField;

  @Autowired
  public DepanFxFlatSectionToolDialog(DepanFxWorkspace workspace) {
    super(workspace, DepanFxFlatSectionData.class);
  }

  public static Dialog<DepanFxFlatSectionToolDialog> runEditDialog(
      DepanFxWorkspaceResource<DepanFxFlatSectionData> flatSectionRsrc,
      DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runEditDialog(
        flatSectionRsrc, dialogRunner,
        DepanFxFlatSectionToolDialog.class,
        DepanFxFlatSection.EDIT_FLAT_SECTION_DATA);
  }

  public static Dialog<DepanFxFlatSectionToolDialog> runCreateDialog(
      DepanFxWorkspaceResource<DepanFxFlatSectionData> flatSectionRsrc,
      DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runCreateDialog(
        flatSectionRsrc, dialogRunner,
        DepanFxFlatSectionToolDialog.class,
        DepanFxFlatSection.EDIT_FLAT_SECTION_DATA);
  }

  public static void setFlatSectionTooldataFilters(FileChooser result) {
    result.getExtensionFilters().add(FLAT_SECTION_FILTER);
    result.setSelectedExtensionFilter(FLAT_SECTION_FILTER);
  }

  @Override
  @FXML
  public void initialize() {
    super.initialize();
    populateOrderBy(orderByField);
  }

  @Override
  public void setToolResource(
      DepanFxWorkspaceResource<DepanFxFlatSectionData> sectionRsrc) {
    super.setToolResource(sectionRsrc);

    orderByField.setValue(sectionRsrc.getResource().getOrderBy());
  }

  /////////////////////////////////////
  // Tool Dialog protected overrides

  @Override
  protected DepanFxFlatSectionData prepareResult() {
    return new DepanFxFlatSectionData(
        getToolName(), getToolDescription(),
        getSectionLabel(), displayNodeCount(),
        orderByField.getValue(), getOrderDirection());
  }

  @Override
  protected File buildInitialDestinationFile() {
    return buildToolInitialDestination(
        DepanFxFlatSectionData.FLAT_SECTION_TOOL_EXT,
        DepanFxNodeListSectionData.SECTIONS_TOOL_PATH);
  }

  @Override
  protected void setTooldataFilters(FileChooser chooser) {
    DepanFxFlatSectionToolDialog.setFlatSectionTooldataFilters(chooser);
  }

  @Override
  protected String getInputCheckFailureText() {
    return "Section Save Confirmation Error";
  }
}
