/*
 * Copyright 2023 The Depan Project Authors
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
package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListGraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxTreeFork;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxCategoryColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxCategoryColumnData.CategoryEntry;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceChooser;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

public class DepanFxCategoryColumn
    extends DepanFxAbstractColumn<DepanFxCategoryColumnData> {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxCategoryColumn.class);

  public static final String EDIT_CATEGORY_COLUMN =
      "Edit Category Column...";

  public static final String NEW_CATEGORY_COLUMN =
      "New Category Column...";

  public static final String SELECT_CATEGORY_COLUMN =
      "Select Category Column...";

  public static final String SAVE_NODE_LISTS =
      "Save Node Lists...";

  private CategoryEditor categories;

  // Menu rendering and availablity varies depending on the "dirty" state.
  private MenuItem selectAction;

  private MenuItem editAction;

  private SeparatorMenuItem saveSeparator;

  private MenuItem saveAction;

  public DepanFxCategoryColumn(
      DepanFxNodeListTableAdapter tableAdapter,
      DepanFxWorkspaceResource<DepanFxCategoryColumnData> columnDataRsrc) {
    super(tableAdapter, columnDataRsrc);
    updateCategories(getColumnData().getCategories());
  }

  public CategoryEditor getCategories() {
    return categories;
  }

  public static void addNewColumnAction(
      DepanFxContextMenuBuilder builder,
      DepanFxDialogRunner dialogRunner,
      DepanFxNodeListTableAdapter tableAdapter) {
    builder.appendActionItem(NEW_CATEGORY_COLUMN,
        e -> openColumnCreate(dialogRunner, tableAdapter));
  }

  @Override
  public String getColumnLabel() {
    return getColumnData().getColumnLabel();
  }

  @Override
  protected double getWidthMs() {
    return getColumnData().getWidthMs();
  }

  @Override
  public ContextMenu buildColumnContextMenu(DepanFxDialogRunner dialogRunner) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    // These actions are disabled is the node list has changes.
    selectAction = builder.appendActionItem(SELECT_CATEGORY_COLUMN,
        e -> openColumnChooser(dialogRunner));
    editAction = builder.appendActionItem(EDIT_CATEGORY_COLUMN,
        e -> openColumnEditor(dialogRunner, tableAdapter));

    // These actions are hidden if the node list is unchanged.
    saveSeparator = builder.appendSeparator();
    saveAction = builder.appendActionItem(
        SAVE_NODE_LISTS, e1 -> runSaveNodeList());

    ContextMenu result = builder.build();
    result.setOnShowing(e -> onColumnMenuShowing());
    return result;
  }

  @Override
  public String toString(DepanFxNodeListGraphNode member) {
    GraphNode graphNode = member.getGraphNode();
    Collection<CategoryEntry> nodeCategories = getCurrentCategories(graphNode);
    int categoryCount = nodeCategories.size();
    if (categoryCount > 1) {
      return String.valueOf(categoryCount);
    }

    if (categoryCount == 1) {
      return nodeCategories.iterator().next().getCategoryLabel();
    }
    // Not in any collections.
    return "";
  }

  @Override
  protected Callback<TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember>,
      TreeTableCell<DepanFxNodeListMember, DepanFxNodeListMember>>
      buildCellFactory() {
    return p -> new DepanFxCategoryColumnCell(this);
  }

  @Override
  protected void refreshColumn() {
    super.refreshColumn();
    updateCategories(getColumnData().getCategories());
  }

  public Collection<CategoryEntry> getCurrentCategories(GraphNode graphNode) {
    return categories.getCurrentCategories(graphNode);
  }

  public void setListMembership(GraphNode graphNode, CategoryEntry entry) {
    categories.setListMembership(graphNode, entry);
  }

  public void setListMembership(
      GraphNode graphNode, Collection<CategoryEntry> entries) {
    categories.setListMembership(graphNode, entries);
  }

  public void setDecendantsCategories(
      DepanFxTreeFork forkItem, Collection<CategoryEntry> updateCategories) {
    forkItem.getDecendants()
        .forEach(n -> categories.setListMembership(n, updateCategories));
    tableAdapter.refreshTableView();
  }

  public void addDecendantsCategories(
      DepanFxTreeFork forkItem, Collection<CategoryEntry> updateCategories) {
    forkItem.getDecendants().stream()
          .forEach(n -> categories.addListMembership(n, updateCategories));
      tableAdapter.refreshTableView();
  }

  public void hoistMemberships(
      DepanFxTreeFork forkItem, Collection<CategoryEntry> updateCategories) {
    Collection<GraphNode> sourceNodes = forkItem.getDecendants();
    DepanFxTreeModel treeModel = forkItem.getTreeModel();

    CategoryWinch winch = new CategoryWinch(
        sourceNodes, categories.getCategoryList(), categories, treeModel);
    winch.hoistCategories();
    tableAdapter.refreshTableView();
  }

  private void onColumnMenuShowing() {
    updateActions();
  }

  private void updateActions() {
    boolean hasEdits = hasNodeListEdits();
    selectAction.setDisable(hasEdits);
    editAction.setDisable(hasEdits);

    saveSeparator.setVisible(hasEdits);
    saveAction.setVisible(hasEdits);
  }

  private boolean hasNodeListEdits() {
    return categories.hasEdits();
  }

  private void runSaveNodeList() {
    // Separate discovery from changes to avoid ConcurrentModificationException
    List<CategoryEntry> updates =
        categories.streamChangedCategories().collect(Collectors.toList());
    updates.forEach(this::saveCategory);
  }

  private static void openColumnCreate(
      DepanFxDialogRunner dialogRunner,
      DepanFxNodeListTableAdapter tableAdapter) {
    DepanFxCategoryColumnData initialData =
        DepanFxCategoryColumnData.buildInitialCategoryColumnData();
    DepanFxWorkspaceResource<DepanFxCategoryColumnData> columnRsrc =
        tableAdapter.getWorkspace().addScratchResource(initialData);
    DepanFxCategoryColumnToolDialog.runCreateDialog(
        columnRsrc, dialogRunner, tableAdapter);
  }

  private void openColumnEditor(
      DepanFxDialogRunner dialogRunner,
      DepanFxNodeListTableAdapter tableAdapter) {
    Dialog<DepanFxCategoryColumnToolDialog> categoryColumnDlg =
        DepanFxCategoryColumnToolDialog.runEditDialog(
            forUpdate(buildEditData()), dialogRunner, tableAdapter);

    categoryColumnDlg.getController().getToolResource()
        .ifPresent(this::updateColumnDataRsrc);
  }

  private DepanFxCategoryColumnData buildEditData() {
    DepanFxCategoryColumnData columnData = getColumnData();
    int widthMs = (int) Math.round(
        column.getWidth() / DepanFxSceneControls.layoutWidthMs(1));

    return new DepanFxCategoryColumnData(
        columnData.getToolName(), columnData.getToolDescription(),
        columnData.getColumnLabel(), widthMs, categories.getCategoryList());
  }

  private void openColumnChooser(DepanFxDialogRunner dialogRunner) {
    DepanFxWorkspace workspace = tableAdapter.getWorkspace();
    Map<?, ?> loadContext = tableAdapter.getLoadContext();
    DepanFxResourceChooser columnChooser =
        prepareChooser(workspace, dialogRunner);
    columnChooser.showOpenDialog(getScene())
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(p -> workspace.getWorkspaceResource(
              p, DepanFxCategoryColumnData.class, loadContext))
        .ifPresent(this::updateColumnDataRsrc);
  }

  private void saveCategory(CategoryEntry entry) {
    DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc =
        entry.getNodeListRsrc();
    DepanFxProjectDocument dstDoc = nodeListRsrc.getDocument();
    DepanFxNodeList updateNodeList = DepanFxNodeLists.buildRelatedNodeList(
        nodeListRsrc.getResource(), categories.getCurrentNodes(entry));

    try {
      saveDocument(dstDoc, updateNodeList)
          .map(nl -> new CategoryEntry(entry.getCategoryLabel(), nl))
          .ifPresent(c -> categories.updateCategory(entry, c));
    } catch (IOException errIo) {
      LOG.error("Unable to save updated node list for {}",
          entry.getCategoryLabel(), errIo);
    }
  }

  private void updateCategories(List<CategoryEntry> categories) {
    this.categories = new CategoryEditor(categories);
  }

  private static DepanFxResourceChooser prepareChooser(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    DepanFxResourceChooser result =
        new DepanFxResourceChooser(workspace, dialogRunner);
    DepanFxResourcePerspectives.prepareResourceFinder(
        result, DepanFxNodeListColumnData.COLUMNS_TOOL_PATH);

    result.getExtensionFilters().add(
        DepanFxCategoryColumnToolDialog.CATEGORY_COLUMN__RSRC_FILTER);
    result.setSelectedExtensionFilter(
        DepanFxCategoryColumnToolDialog.CATEGORY_COLUMN__RSRC_FILTER);
    return result;
  }
}
