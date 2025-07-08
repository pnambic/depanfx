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
import com.pnambic.depanfx.nodelist.tooldata.DepanFxFlatSectionData;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;

public class DepanFxFlatSectionItem extends DepanFxNodeListSectionItem {

  public static final String SELECT_FLAT_SECTION = "Select Flat Section...";

  public static final String EDIT_FLAT_SECTION = "Edit Flat Section...";

  private boolean sectionLoaded = false;

  public DepanFxFlatSectionItem(DepanFxFlatSection section) {
    super(section);
  }

  @Override
  public ObservableList<TreeItem<DepanFxNodeListMember>> getChildren() {
    if (!sectionLoaded) {
      sectionLoaded = true;
      super.getChildren().setAll(buildChildren());
    }

    return super.getChildren();
  }

  @Override // DepanFxNodeListMember
  public ContextMenu getNodeContextMenu(
      Scene scene,
      DepanFxNodeListTableAdapter tableAdapter,
      GraphNode node) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem(SELECT_FLAT_SECTION,
        e -> openFlatSectionFinder(scene, tableAdapter));
    builder.appendActionItem(EDIT_FLAT_SECTION,
        e -> openFlatSectionEditor(tableAdapter));

    builder.appendSubMenu(
        buildNewSectionMenu(scene, tableAdapter, getSection()));
    builder.appendSeparator();
    builder.appendActionItem(
        EXPORT_TO_CSV,
        e -> runExportToCsvAction(tableAdapter));
    return builder.build();
  }

  private ObservableList<TreeItem<DepanFxNodeListMember>> buildChildren() {
    DepanFxNodeListSection section = getSection();

    Collection<GraphNode> nodes = section.getSectionNodes().getNodes();

    List<TreeItem<DepanFxNodeListMember>> result =
        new ArrayList<>(nodes.size());
    nodes.stream()
        .filter(Objects::nonNull)  // [9-Dec-2023] should not happen, but avoids a crash
        .map(section::buildNodeItem)
        .forEach(result::add);
    section.sortTreeItems(result);

    return FXCollections.observableList(result);
  }

  private void openFlatSectionFinder(
      Scene scene, DepanFxNodeListTableAdapter tableAdapter) {
    prepareSectionChooser(
        tableAdapter, DepanFxFlatSectionToolDialog.FLAT_SECTION_RSRC_FILTER)
        .showOpenDialog(scene)
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(p -> tableAdapter.getWorkspace().getWorkspaceResource(
            p, DepanFxFlatSectionData.class))
        .ifPresent(d -> tableAdapter.updateSection(getSection(), d));
  }

  private void openFlatSectionEditor(
      DepanFxNodeListTableAdapter tableAdapter) {

    if (getSection() instanceof DepanFxFlatSection section) {
      DepanFxFlatSectionToolDialog.runEditDialog(
          section.getSectionResource(), tableAdapter.getDialogRunner())
          .getController()
          .getToolResource()
          .ifPresent(d -> updateSectionDataResource(tableAdapter, d));
    }
  }

  private void runExportToCsvAction(
      DepanFxNodeListTableAdapter tableAdapter) {

    if (getSection() instanceof DepanFxFlatSection section) {
      DepanFxExportFlatSectionDialog.runExportDialog(section, tableAdapter);
    }
  }
}
