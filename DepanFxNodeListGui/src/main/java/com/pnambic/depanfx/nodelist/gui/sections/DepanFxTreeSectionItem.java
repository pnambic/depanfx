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
package com.pnambic.depanfx.nodelist.gui.sections;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxTreeSectionData;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel;
import com.pnambic.depanfx.scene.DepanFxMenuItemFactory;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;

/**
 * Provides top-level rendering of a tree section.
 * Used as the mechanism to provide {@link DepanFxTreeFork} elements with
 * the section's {@link DepanFxTreeModel}.
 */
public class DepanFxTreeSectionItem
    extends DepanFxNodeListSectionItem {

  public static final String SELECT_TREE_SECTION = "Select Tree Section...";

  public static final String EDIT_TREE_SECTION = "Edit Tree Section...";

  public DepanFxTreeSectionItem(DepanFxTreeSection section) {
    super(section);
  }

  @Override // DepanFxNodeListMember
  public void fillNodeContextMenu(
      ContextMenu contextMenu,
      Scene scene,
      DepanFxNodeListTableAdapter tableAdapter,
      GraphNode node) {
    DepanFxMenuItemFactory builder =
        new DepanFxMenuItemFactory(contextMenu.getItems());
    builder.appendActionItem(SELECT_TREE_SECTION,
        e -> openTreeSectionFinder(scene, tableAdapter));
    builder.appendActionItem(EDIT_TREE_SECTION,
        e -> openTreeSectionEditor(tableAdapter));

    builder.appendSubMenu(
        buildNewSectionMenu(scene, tableAdapter, getSection()));

    builder.appendSeparator();
    builder.appendActionItem(
        EXPORT_TO_CSV,
        e -> runExportToCsvAction(tableAdapter));
  }

  @Override
  protected ObservableList<TreeItem<DepanFxNodeListMember>> buildChildren() {
    DepanFxNodeListSection section = getSection();

    DepanFxTreeModel treeModel = ((DepanFxTreeSection) section).getTreeModel();
    Collection<GraphNode> nodes = treeModel.getRoots();

    List<TreeItem<DepanFxNodeListMember>> result =
        new ArrayList<>(nodes.size());
    nodes.stream()
        .map(section::buildNodeItem)
        .forEach(result::add);
    section.sortTreeItems(result);

    return FXCollections.observableList(result);
  }

  private void openTreeSectionFinder(
      Scene scene, DepanFxNodeListTableAdapter tableAdapter) {

    prepareSectionChooser(
        tableAdapter, DepanFxTreeSectionToolDialog.TREE_SECTION_RSRC_FILTER)
        .showOpenDialog(scene)
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(p -> tableAdapter.getWorkspace().getWorkspaceResource(
            p, DepanFxTreeSectionData.class))
        .ifPresent(d -> updateSectionDataResource(tableAdapter, d));
  }

  private void openTreeSectionEditor(
      DepanFxNodeListTableAdapter tableAdapter) {

    if (getSection() instanceof DepanFxTreeSection section) {
      DepanFxTreeSectionToolDialog.runEditDialog(
          section.getSectionResource(), tableAdapter.getDialogRunner())
          .getController()
          .getToolResource()
          .ifPresent(d -> updateSectionDataResource(tableAdapter, d));
    }
  }

  private void runExportToCsvAction(
      DepanFxNodeListTableAdapter tableAdapter) {
    if (getSection() instanceof DepanFxTreeSection section) {

      DepanFxExportTreeSectionDialog.runExportDialog(
          section, tableAdapter.getDialogRunner());
    }
  }
}
