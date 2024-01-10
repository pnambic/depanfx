package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.export.AbstractCsvExporter;
import com.pnambic.depanfx.nodelist.export.ExportColumn;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumn;
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
@FxmlView("export-flat-section-csv-dialog.fxml")
public class DepanFxExportFlatSectionDialog extends DepanFxBaseExportSectionDialog {

  private DepanFxNodeListViewer listViwer;

  @Autowired
  public DepanFxExportFlatSectionDialog(DepanFxWorkspace workspace) {
    super(workspace);
  }

  public static Dialog<DepanFxExportFlatSectionDialog> runExportDialog(
      DepanFxFlatSection flatSection, DepanFxNodeListViewer listViwer) {
    Dialog<DepanFxExportFlatSectionDialog> dlg =
        listViwer.buildDialog(DepanFxExportFlatSectionDialog.class);
    dlg.getController().setFlatSectionInfo(flatSection, listViwer);
    dlg.runDialog("Export Flat Section");
    return dlg;
  }

  public void setFlatSectionInfo(
      DepanFxFlatSection flatSection, DepanFxNodeListViewer listViwer) {
    this.listViwer = listViwer;
    super.setSectionDoc(flatSection);
  }

  @Override
  protected AbstractCsvExporter getCsvExporter(
      List<ExportColumn> exportColumns) {
    return new FlatSectionCsvExporter(exportColumns);
  }

  @Override
  protected Collection<GraphNode> getExportRoots() {
    return getSection().getSectionNodes().getNodes();
  }

  @Override
  protected Stream<DepanFxNodeListColumn> streamColumns() {
    return listViwer.getColumns().stream();
  }

  @Override
  protected GraphDocument getGraphDoc() {
    return listViwer.getGraphDoc();
  }

  /**
   * Specializes the abstract exporter to output the supplied rows.
   */
  private class FlatSectionCsvExporter extends AbstractCsvExporter {

    public FlatSectionCsvExporter(List<ExportColumn> exportColumns) {
      super(exportColumns);
    }

    @Override
    protected void writeData(Collection<GraphNode> exportRoots)
        throws IOException {
      for (GraphNode root : exportRoots) {
        writeRow(root);
      }
    }
  }
}
