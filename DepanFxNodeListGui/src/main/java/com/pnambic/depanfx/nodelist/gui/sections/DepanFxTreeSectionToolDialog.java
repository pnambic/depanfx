package com.pnambic.depanfx.nodelist.gui.sections;

import com.pnambic.depanfx.edgematchers.gui.DepanFxEdgeMatcherDialogRegistry;
import com.pnambic.depanfx.edgematchers.gui.DepanFxLinkMatcherChooser;
import com.pnambic.depanfx.edgematchers.gui.DepanFxLinkMatcherChooser.LinkMatcherControl;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxContainerOrder;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderBy;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxTreeSectionData;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@DepanFxFxmlDialog
@FxmlView("tree-section-tool-dialog.fxml")
public class DepanFxTreeSectionToolDialog
    extends DepanFxBaseSectionToolDialog<DepanFxTreeSectionData> {

  private static final ExtensionFilter TREE_SECTION_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Tree Sections", DepanFxTreeSectionData.TREE_SECTION_TOOL_EXT);

  public static final DepanFxResourceFilter TREE_SECTION_RSRC_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Tree Sections",
          DepanFxTreeSectionData.TREE_SECTION_TOOL_EXT,
          DepanFxTreeSectionData.class);

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxEdgeMatcherDialogRegistry matcherDialogRegistry;

  @FXML
  private TextField linkMatcherResourceField;

  @FXML
  private CheckBox inferMissingParentsField;

  @FXML
  private ComboBox<OrderBy> orderByField;

  @FXML
  private ComboBox<DepanFxContainerOrder> containerOrderField;

  private LinkMatcherControl linkMatcherControl;

  @Autowired
  public DepanFxTreeSectionToolDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner,
      DepanFxEdgeMatcherDialogRegistry matcherDialogRegistry) {
    super(workspace, DepanFxTreeSectionData.class);
    this.dialogRunner = dialogRunner;
    this.matcherDialogRegistry = matcherDialogRegistry;
  }

  public static Dialog<DepanFxTreeSectionToolDialog> runEditDialog(
      DepanFxWorkspaceResource<DepanFxTreeSectionData> sectionRsrc,
      DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runEditDialog(
        sectionRsrc, dialogRunner,
        DepanFxTreeSectionToolDialog.class,
        DepanFxTreeSection.EDIT_TREE_SECTION_DATA);
  }

  public static Dialog<DepanFxTreeSectionToolDialog> runCreateDialog(
      DepanFxWorkspaceResource<DepanFxTreeSectionData> sectionRsrc,
      DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runCreateDialog(
        sectionRsrc, dialogRunner,
        DepanFxTreeSectionToolDialog.class,
        DepanFxTreeSection.NEW_TREE_SECTION_DATA);
  }

  public static void setTreeSectionTooldataFilters(FileChooser result) {
    result.getExtensionFilters().add(TREE_SECTION_FILTER);
    result.setSelectedExtensionFilter(TREE_SECTION_FILTER);
  }

  @FXML
  @Override // DepanFxBaseSectionToolDialog
  public void initialize() {
    super.initialize();

    linkMatcherControl = new DepanFxLinkMatcherChooser.LinkMatcherControl(
        getWorkspace(), dialogRunner, matcherDialogRegistry, linkMatcherResourceField);

    populateOrderBy(orderByField);

    containerOrderField.getItems().add(DepanFxContainerOrder.FIRST);
    containerOrderField.getItems().add(DepanFxContainerOrder.LAST);
    containerOrderField.getItems().add(DepanFxContainerOrder.MIXED);
  }

  @Override
  public void setToolResource(
      DepanFxWorkspaceResource<DepanFxTreeSectionData> sectionRsrc) {
    super.setToolResource(sectionRsrc);

    DepanFxTreeSectionData sectionData = sectionRsrc.getResource();
    linkMatcherControl.setLinkMatcherResource(sectionData.getLinkMatcherRsrc());

    inferMissingParentsField.setSelected(sectionData.inferMissingParents());

    orderByField.setValue(sectionData.getOrderBy());
    containerOrderField.setValue(sectionData.getContainerOrder());
  }

  /////////////////////////////////////
  // Tool Dialog protected overrides

  @Override
  protected DepanFxTreeSectionData prepareResult() {
    return new DepanFxTreeSectionData(
        getToolName(),
        getToolDescription(),
        getSectionLabel(),
        displayNodeCount(),
        linkMatcherControl.getLinkMatcherResource(),
        inferMissingParentsField.isSelected(),
        orderByField.getValue(),
        containerOrderField.getValue(),
        getOrderDirection());
  }

  @Override
  protected File buildInitialDestinationFile() {
    return buildToolInitialDestination(
        DepanFxTreeSectionData.TREE_SECTION_TOOL_EXT,
        DepanFxNodeListSectionData.SECTIONS_TOOL_PATH);
  }

  @Override
  protected void setTooldataFilters(FileChooser chooser) {
    DepanFxTreeSectionToolDialog.setTreeSectionTooldataFilters(chooser);
  }

  @Override
  protected String getInputCheckFailureText() {
    return  "Tree Section Save Confirmation Error";
  }
}
