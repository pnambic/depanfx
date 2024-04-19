package com.pnambic.depanfx.nodelist.gui.sections;

import com.pnambic.depanfx.nodelist.gui.link.DepanFxLinkMatcherChooser;
import com.pnambic.depanfx.nodelist.gui.link.DepanFxLinkMatcherChooser.LinkMatcherControl;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListSectionData.OrderBy;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxTreeSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxTreeSectionData.ContainerOrder;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@Component
@FxmlView("tree-section-tool-dialog.fxml")
public class DepanFxTreeSectionToolDialog
    extends DepanFxBaseSectionToolDialog<DepanFxTreeSectionData> {

  private static final ExtensionFilter TREE_SECTION_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Tree Sections", DepanFxTreeSectionData.TREE_SECTION_TOOL_EXT);

  private final DepanFxDialogRunner dialogRunner;

  @FXML
  private TextField linkMatcherResourceField;

  @FXML
  private CheckBox inferMissingParentsField;

  @FXML
  private ComboBox<OrderBy> orderByField;

  @FXML
  private ComboBox<ContainerOrder> containerOrderField;

  private LinkMatcherControl linkMatcherControl;

  @Autowired
  public DepanFxTreeSectionToolDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    super(workspace, DepanFxTreeSectionData.class);
    this.dialogRunner = dialogRunner;
  }

  public static Dialog<DepanFxTreeSectionToolDialog> runEditDialog(
      DepanFxProjectDocument projDoc,
      DepanFxTreeSectionData sectionData,
      DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runEditDialog(
        projDoc, sectionData, dialogRunner,
        DepanFxTreeSectionToolDialog.class,
        DepanFxTreeSection.EDIT_TREE_SECTION_DATA);
  }

  public static Dialog<DepanFxTreeSectionToolDialog> runCreateDialog(
      DepanFxTreeSectionData sectionData, DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runCreateDialog(
        sectionData, dialogRunner,
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
        getWorkspace(), dialogRunner, linkMatcherResourceField);

    populateOrderBy(orderByField);

    containerOrderField.getItems().add(ContainerOrder.FIRST);
    containerOrderField.getItems().add(ContainerOrder.LAST);
    containerOrderField.getItems().add(ContainerOrder.MIXED);
  }

  @Override // DepanFxBaseSectionToolDialog
  public void setTooldata(DepanFxTreeSectionData sectionData) {
    super.setTooldata(sectionData);
    linkMatcherControl.setLinkMatcherRsrc(sectionData.getLinkMatcherRsrc());

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
