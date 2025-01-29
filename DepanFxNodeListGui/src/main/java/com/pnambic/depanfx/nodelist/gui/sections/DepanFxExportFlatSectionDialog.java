package com.pnambic.depanfx.nodelist.gui.sections;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.export.AbstractCsvExporter;
import com.pnambic.depanfx.nodelist.export.ExportColumn;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumn;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxFxmlDialog;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@DepanFxFxmlDialog
@FxmlView("export-flat-section-csv-dialog.fxml")
public class DepanFxExportFlatSectionDialog
    extends DepanFxBaseExportSectionDialog {

  private DepanFxNodeListTableAdapter tableAdapter;

  @Autowired
  public DepanFxExportFlatSectionDialog(DepanFxWorkspace workspace) {
    super(workspace);
  }

  public static Dialog<DepanFxExportFlatSectionDialog> runExportDialog(
      DepanFxFlatSection flatSection, DepanFxNodeListTableAdapter tableAdapter) {
    Dialog<DepanFxExportFlatSectionDialog> dlg =
        tableAdapter.getDialogRunner()
            .createDialogAndParent(DepanFxExportFlatSectionDialog.class);
    dlg.getController().setFlatSectionInfo(flatSection, tableAdapter);
    dlg.runDialog("Export Flat Section");
    return dlg;
  }

  public void setFlatSectionInfo(
      DepanFxFlatSection flatSection, DepanFxNodeListTableAdapter tableAdapter) {
    this.tableAdapter = tableAdapter;
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
    return tableAdapter.streamColumns();
  }

  @Override
  protected GraphDocument getGraphDoc() {
    return tableAdapter.getGraphDoc();
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
