/*
 * Copyright 2024 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pnambic.depanfx.nodefilters.gui;

import com.pnambic.depanfx.graph.model.GraphModel;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodefilters.model.DepanFxBaseFilter;
import com.pnambic.depanfx.nodefilters.model.DepanFxNodeFiltersRegistry;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxSequenceFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.FilterMergeMode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListConfiguration;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListSelection;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableCommands;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableController;
import com.pnambic.depanfx.nodelist.gui.DepanFxSaveNodeListDialog;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.perspective.DepanFxWorkspaceDialog;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxTreeColumnBinder;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInProject;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.ComboBoxTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.stage.Stage;

@Component
@FxmlView("node-view-node-filters-dialog.fxml")
public class DepanFxNodeViewNodeFiltersDialog extends DepanFxWorkspaceDialog {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeViewNodeFiltersDialog.class);

  public static final String EDIT_NODE_FILTERS = "Node Filters...";

  private static final String SELECT_NODE_FILTER = "Select Filter...";

  private static final String SAVE_NODE_FILTER = "Save Filter...";

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxNodeFiltersRegistry nodeFiltersRegistry;

  private final DepanFxNodeFiltersDialogRegistry nodeFiltersDialogRegistry;

  /**
   * Place holder for filters context menu.
   */
  @FXML
  private Label filtersCommands;

  /**
   * Place holder for node table context menu.
   */
  @FXML
  private Label nodeTableCommands;

  /**
   * Let FXML place the table.  Other behavior is implemented by the
   * {@link TreeTableView}.
   */
  @FXML
  private TreeTableView<DepanFxNodeFiltersTableMember> nodeFilterTable;

  private DepanFxNodeFiltersRootMember nodeFilterRoot;

  /**
   * Let FXML place the table.  Other behavior is implemented by the
   * {@link DepanFxNodeViewNodeFiltersDialog}.
   */
  @FXML
  private TreeTableView<DepanFxNodeListMember> nodeSelectTable;

  private DepanFxNodeListTableViewData tableView;

  private DepanFxNodeListTableController tableControl;

  private Consumer<DepanFxNodeList> onUpdate;

  private DepanFxProjectDocument destDoc;

  private DepanFxNodeList sourceNodes;

  @Autowired
  public DepanFxNodeViewNodeFiltersDialog(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxNodeFiltersRegistry nodeFiltersRegistry,
      DepanFxNodeFiltersDialogRegistry nodeFiltersDialogRegistry) {
    super(workspace);
    this.dialogRunner = dialogRunner;
    this.nodeFiltersRegistry = nodeFiltersRegistry;
    this.nodeFiltersDialogRegistry = nodeFiltersDialogRegistry;
  }

  /**
   * Node Selection Editor is a modeless dialog coupled to the graph view.
   */
  public static Stage runEditDialog(
      DepanFxDialogRunner dialogRunner,
      DepanFxProjectDocument destDoc,
      DepanFxNodeListTableViewData tableView,
      DepanFxNodeList sourceNodes,
      Consumer<DepanFxNodeList> onUpdate) {

    Dialog<DepanFxNodeViewNodeFiltersDialog> dlg =
        dialogRunner.createDialogAndParent(
            DepanFxNodeViewNodeFiltersDialog.class);
    dlg.getController().setDestinationDocument(destDoc);
    dlg.getController().setTableView(tableView);
    dlg.getController().setSourceNodes(sourceNodes);
    dlg.getController().setOnUpdate(onUpdate);
    return dlg.runModeless(EDIT_NODE_FILTERS);
  }

  @Override // DepanFxWorkspaceDialog
  public Scene getScene() {
    return nodeFilterTable.getScene();
  }

  @FXML
  public void initialize() {
    filtersCommands.setContextMenu(buildFiltersCommandMenu());
    nodeFilterTable.setContextMenu(buildFilterTableMenu());

    nodeFilterRoot = new DepanFxNodeFiltersRootMember(workspace);
    nodeFilterTable.setRoot(
        new DepanFxNodeFiltersRootItem(nodeFilterRoot, nodeFiltersDialogRegistry));

    DepanFxTreeColumnBinder<DepanFxNodeFiltersTableMember> columnBinder =
        new DepanFxTreeColumnBinder<>(nodeFilterTable);

    TreeTableColumn<DepanFxNodeFiltersTableMember, String> labelColumn =
        columnBinder.next();
    labelColumn.setCellValueFactory(
        f -> getColumnInfo(f.getValue()).getToolNameProperty());
    labelColumn.setCellFactory(
        l -> new DepanFxNodeFiltersTableCell(
                getWorkspace(), dialogRunner, nodeFiltersDialogRegistry));

    TreeTableColumn<DepanFxNodeFiltersTableMember, Boolean> closureColumn =
        columnBinder.next();
    closureColumn.setCellValueFactory(
        f -> getColumnInfo(f.getValue()).getUseClosureProperty());
    closureColumn.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(
        i -> getColumnInfo(nodeFilterTable.getTreeItem(i.intValue()))
                .getUseClosureProperty()));

    TreeTableColumn<DepanFxNodeFiltersTableMember, FilterMergeMode> mergeColumn =
        columnBinder.next();
    mergeColumn.setCellValueFactory(
        f -> getColumnInfo(f.getValue()).getMergeModeProperty());
    mergeColumn.setCellFactory(
        ComboBoxTreeTableCell.forTreeTableColumn(
            FilterMergeMode.class.getEnumConstants()));

    TreeTableColumn<DepanFxNodeFiltersTableMember, String> descrColumn =
        columnBinder.next();
    descrColumn.setCellValueFactory(
        f -> getColumnInfo(f.getValue()).getToolDescriptionProperty());
    descrColumn.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());

    // Size descrColumn to remaining room
    descrColumn.prefWidthProperty().bind(
        nodeFilterTable.widthProperty()
            .subtract(labelColumn.widthProperty())
            .subtract(closureColumn.widthProperty())
            .subtract(mergeColumn.widthProperty())
            .subtract(1));
  }

  public void setDestinationDocument(DepanFxProjectDocument destDoc) {
    this.destDoc = destDoc;
  }

  public void setSourceNodes(DepanFxNodeList filteredNodes) {
    if (tableView == null) {
      Optional<DepanFxWorkspaceResource<DepanFxNodeListTableViewData>> optFlatView =
          ((DepanFxBuiltInProject) workspace.getBuiltInProject())
              .getResource(DepanFxNodeListConfiguration.FLAT_TABLE_VIEW_PATH);
      optFlatView.ifPresent(r -> tableView = r.getResource());
    }

    this.sourceNodes = filteredNodes;

    tableControl = buildTable(filteredNodes);
    nodeTableCommands.setContextMenu(buildNodeTableCommandMenu());
  }

  public void setTableView(DepanFxNodeListTableViewData tableView) {
    this.tableView = tableView;
    if (tableControl != null) {
      tableControl.setTableView(tableView);
    }
  }

  public void setOnUpdate(Consumer<DepanFxNodeList> onUpdate) {
    this.onUpdate = onUpdate;
  }

  @FXML
  public void handleUpdateSelection() {
    onUpdate.accept(tableControl.getSelection());
  }

  @FXML
  public void handleSaveSelection() {

    DepanFxSaveNodeListDialog.runSaveNodeList(
        dialogRunner,
        workspace.addScratchResource(tableControl.getSelection()));
  }

  @FXML
  public void loadNodeFilter() {
    nodeFilterChooser()
        .ifPresent(this::updateFilterTableRoot);
  }

  @FXML
  public void handleSaveFilters() {
    DepanFxBaseFilterData saveFilter = prepareResult();
    nodeFiltersDialogRegistry.runSaveFilters(dialogRunner, saveFilter);
  }

  @FXML
  public void handleEvaluate() {
    DepanFxBaseFilterData filterData = prepareResult();
    GraphModel graphModel =
        sourceNodes.getGraphDocResource().getResource().getGraph();
    Collection<GraphNode> targetNodes = graphModel.getGraphNodes();

    DepanFxBaseFilter<?> filter =
        nodeFiltersRegistry.buildFilter(filterData, graphModel, targetNodes);

    Collection<GraphNode> resultNodes =
        filter.computeNodes(sourceNodes.getNodes());
    DepanFxNodeList results =
        DepanFxNodeLists.buildRelatedNodeList(sourceNodes, resultNodes);

    tableControl = buildTable(results);
    nodeTableCommands.setContextMenu(buildNodeTableCommandMenu());
  }

  /////////////////////////////////////

  private Optional<DepanFxWorkspaceResource<DepanFxBaseFilterData>>
      nodeFilterChooser() {

    return DepanFxNodeFiltersChooser.runNodeFiltersFinder(
        workspace, dialogRunner, getScene(), nodeFiltersDialogRegistry);
  }

  private ContextMenu buildFiltersCommandMenu() {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem(SELECT_NODE_FILTER, e -> loadNodeFilter());
    builder.appendActionItem(SAVE_NODE_FILTER, e -> handleSaveFilters());
    return builder.build();
  }

  private DepanFxNodeListTableController buildTable(
      DepanFxNodeList tableNodes) {
    DepanFxNodeListSelection nodeSelection =
        DepanFxNodeListSelection.forNodes(tableNodes.getNodes());
    nodeSelection.doSelectAllAction();

    return new DepanFxNodeListTableController(
        workspace, dialogRunner, tableNodes, nodeSelection,
        tableView, nodeSelectTable);
  }

  private ContextMenu buildNodeTableCommandMenu() {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    DepanFxNodeListTableCommands commands = tableControl.buildTableCommands();
    commands.addSelectItems(builder);
    commands.addTableViewItems(builder);
    return builder.build();
  }

  private ContextMenu buildFilterTableMenu() {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    Scene scene = getScene();
    nodeFiltersDialogRegistry.appendAddFilters(
        builder, workspace, dialogRunner, scene, this::onAddFilter);

    return builder.build();
  }

  private void onAddFilter(DepanFxBaseFilterData filterData) {
    nodeFilterRoot.addFilter(filterData);
  }

  private void updateFilterTableRoot(
      DepanFxWorkspaceResource<? extends DepanFxBaseFilterData> filterRsrc) {
    DepanFxBaseFilterData resource = filterRsrc.getResource();

    // For sequence, put items in list
    if (resource instanceof DepanFxSequenceFilterData seqData) {
      List<? extends DepanFxBaseFilterData> seqFilters =
          seqData.streamFilters().collect(Collectors.toList());
      nodeFilterRoot.setAll(seqFilters);
      setDestinationDocument(filterRsrc.getDocument());
      return;
    }

    // For sequence, put items in list
    nodeFilterRoot.set(resource);
    setDestinationDocument(filterRsrc.getDocument());
  }

  private DepanFxNodeFiltersTableColumns getColumnInfo(
      TreeItem<DepanFxNodeFiltersTableMember> memberItem) {

    return (DepanFxNodeFiltersTableColumns) memberItem.getValue();
  }

  private DepanFxBaseFilterData prepareResult() {
    ObservableList<TreeItem<DepanFxNodeFiltersTableMember>> children =
        nodeFilterTable.getRoot().getChildren();

    List<DepanFxBaseFilterData> filters = children.stream()
        .flatMap(i -> prepareFilterData(i).stream())
        .collect(Collectors.toList());

    if (filters.size() == 1) {
      return filters.getFirst();
    }

    // Package an list as a sequence filter.
    String nameBase = tableControl.getGraphDoc().getGraphName();
    return new DepanFxSequenceFilterData(
        String.format("%s Filter", nameBase),
        String.format("Filter for %s graph.", nameBase),
        FilterMergeMode.REPLACE, filters, false);
  }

  private Optional<DepanFxBaseFilterData> prepareFilterData(
      TreeItem<DepanFxNodeFiltersTableMember> item) {
    if (item.getValue() instanceof DepanFxNodeFiltersDataProvider provider) {
      return Optional.of(provider.prepareFilterData());
    }
    LOG.info("Unrecognized item value type {} on filter tree.",
        item.getValue().getClass().getName());
    return Optional.empty();
  }
}
