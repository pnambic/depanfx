package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.export.AbstractCsvExporter;
import com.pnambic.depanfx.nodelist.export.ExportColumn;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumn;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Component
@FxmlView("export-tree-section-csv-dialog.fxml")
public class DepanFxExportTreeSectionDialog
    extends DepanFxBaseExportSectionDialog {

  @Autowired
  public DepanFxExportTreeSectionDialog(DepanFxWorkspace workspace) {
    super(workspace);
  }

  public static Dialog<DepanFxExportTreeSectionDialog> runExportDialog(
      DepanFxTreeSection treeSection, DepanFxDialogRunner dialogRunner) {
    Dialog<DepanFxExportTreeSectionDialog> dlg =
        dialogRunner.createDialogAndParent(DepanFxExportTreeSectionDialog.class);
    dlg.getController().setTreeSectionDoc(treeSection);
    dlg.runDialog("Export Tree Section");
    return dlg;
  }

  public void setTreeSectionDoc(DepanFxTreeSection treeSection) {
    super.setSectionDoc(treeSection);
  }

  @Override
  protected Collection<GraphNode> getExportRoots() {
    return getTreeSection().getTreeModel().getRoots();
  }

  @Override
  protected Stream<DepanFxNodeListColumn> streamColumns() {
    return getTreeSection().getColumns().stream();
  }

  @Override
  protected AbstractCsvExporter getCsvExporter(
      List<ExportColumn> exportColumns) {
    return new TreeSectionCsvExporter(
        exportColumns, getTreeSection().getTreeModel());
  }

  @Override
  protected GraphDocument getGraphDoc() {
    return
        (GraphDocument) getTreeSection().getTreeModel().getGraphDocResource()
        .getResource();
  }

  private DepanFxTreeSection getTreeSection() {
    return (DepanFxTreeSection) getSection();
  }

  /**
   * Specializes the abstract exporter to output all rows and their
   * tree based descendants.
   */
  private class TreeSectionCsvExporter extends AbstractCsvExporter {

    private final DepanFxTreeModel depanFxTreeModel;

    public TreeSectionCsvExporter(
        List<ExportColumn> exportColumns, DepanFxTreeModel depanFxTreeModel) {
      super(exportColumns);
      this.depanFxTreeModel = depanFxTreeModel;
    }

    @Override
    protected void writeData(Collection<GraphNode> exportRoots)
        throws IOException {
      for (GraphNode root : exportRoots) {
        writeRow(root);
        writeChildren(root);
      }
    }

    /**
     * Export by depth first traversal of the tree model.
     */
    private void writeChildren(GraphNode node) throws IOException {
      for (GraphNode child : depanFxTreeModel.getMembers(node)) {
        writeRow(child);
        writeChildren(child);
      }
    }
  }
}
