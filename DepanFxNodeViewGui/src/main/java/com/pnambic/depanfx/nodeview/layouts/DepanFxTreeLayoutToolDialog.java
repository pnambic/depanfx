package com.pnambic.depanfx.nodeview.layouts;

import com.pnambic.depanfx.nodelist.gui.link.DepanFxLinkMatcherChooser;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLayoutData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxTreeLayoutData;
import com.pnambic.depanfx.perspective.DepanFxBaseToolDialog;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
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

  private DepanFxWorkspaceResource hierarchyMatcherRsrc;

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
    hierarchyMatcherRsrcField.setContextMenu(buildHierarchyMatcherMenu());
  }

  @Override // DepanFxBaseColumnToolDialog
  public void setTooldata(DepanFxTreeLayoutData treeLayoutData) {
    super.setTooldata(treeLayoutData);

    setHierarchyMatcherRsrc(treeLayoutData.getHierarchyMatcherRsrc());
  }

  private ContextMenu buildHierarchyMatcherMenu() {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem("Select Link Matcher...",
        e -> runLinkMatcherFinder());
    return builder.build();
  }

  private void runLinkMatcherFinder() {
    DepanFxLinkMatcherChooser.runLinkMatcherFinder(
        getWorkspace(), dialogRunner,
        hierarchyMatcherRsrcField.getScene())
        .ifPresent(this::setHierarchyMatcherRsrc);
  }

  private void setHierarchyMatcherRsrc(
      DepanFxWorkspaceResource hierarchyMatcherRsrc) {
    this.hierarchyMatcherRsrc = hierarchyMatcherRsrc;
    hierarchyMatcherRsrcField.setText(
        getHierarchyMatcherRsrcName(hierarchyMatcherRsrc));
  }

  private String getHierarchyMatcherRsrcName(DepanFxWorkspaceResource rsrc ) {
    if (rsrc != null) {
      return rsrc.getDocument().getMemberPath().toString();
    }
    // Let the text input field show a prompt text.
    return null;
  }

  /////////////////////////////////////
  // Tool Dialog protected overrides

  @Override
  protected DepanFxTreeLayoutData prepareResult() {
    return new DepanFxTreeLayoutData(
        getToolName(), getToolDescription(), hierarchyMatcherRsrc);
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
