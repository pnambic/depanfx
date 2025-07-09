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

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListItem;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.gui.sections.folds.DepanFxFoldSectionToolDialog;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherGroup;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxBaseSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxFoldSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxTreeSectionData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceChooser;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilterModel;
import com.pnambic.depanfx.scene.DepanFxMenuBuilder;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.TreeItem;

/**
 * Common behavior for all node list section items.
 */
public abstract class DepanFxNodeListSectionItem extends DepanFxNodeListItem {

  private static final String NODE_LIST_SECTIONS = "Node List Sections";

  public static final String INSERT_SECTION_MENU_LABEL = "Insert Section";

  public static final String SELECT_SECTION = "Select Section...";

  public static final String EXPORT_TO_CSV = "Export to CSV";

  private static final String INSERT_ABOVE_FOLD_SECTION =
      "Insert Fold Section";

  private static final String INSERT_ABOVE_MEMBER_TREE_SECTION =
      "Insert Member Tree Section";

  private boolean sectionLoaded = false;

  public DepanFxNodeListSectionItem(DepanFxNodeListSection section) {
    super(section);
  }

  @Override
  public boolean isLeaf() {
    return false;
  }

  @Override
  public ObservableList<TreeItem<DepanFxNodeListMember>> getChildren() {
    if (!sectionLoaded) {
      sectionLoaded = true;
      super.getChildren().setAll(buildChildren());
    }

    return super.getChildren();
  }

  abstract protected ObservableList<TreeItem<DepanFxNodeListMember>>
  buildChildren();

  protected DepanFxNodeListSection getSection() {
    return (DepanFxNodeListSection) getValue();
  }

  protected DepanFxResourceChooser prepareSectionChooser(
      DepanFxNodeListTableAdapter tableAdapter,
      DepanFxResourceFilterModel rsrcFilter) {
    DepanFxResourceChooser result =
        new DepanFxResourceChooser(
            tableAdapter.getWorkspace(), tableAdapter.getDialogRunner());
    DepanFxResourcePerspectives.prepareResourceFinder(
        result, DepanFxNodeListSectionData.SECTIONS_TOOL_PATH);
    result.getExtensionFilters().add(rsrcFilter);
    result.setSelectedExtensionFilter(rsrcFilter);
    return result;
  }

  protected void updateSectionDataResource(
      DepanFxNodeListTableAdapter tableAdapter,
      DepanFxWorkspaceResource<? extends DepanFxBaseSectionData>  dataRsrc) {
    tableAdapter.updateSection(getSection(), dataRsrc);
  }

  protected Menu buildNewSectionMenu(
      Scene scene,
      DepanFxNodeListTableAdapter tableAdapter,
      DepanFxNodeListSection before) {
    DepanFxMenuBuilder menuBuilder =
        new DepanFxMenuBuilder(INSERT_SECTION_MENU_LABEL);
    menuBuilder.appendActionItem(
        SELECT_SECTION, e -> doSelectSectionAction(
            scene, tableAdapter, before));

    // Could be driven by a registry of section types.
    menuBuilder.appendSeparator();
    menuBuilder.appendActionItem(
        INSERT_ABOVE_MEMBER_TREE_SECTION,
        e -> runInsertMemberTreeSectionAction(tableAdapter, before));
    menuBuilder.appendActionItem(
        INSERT_ABOVE_FOLD_SECTION,
        e -> runInsertFoldSectionAction(tableAdapter, before));

    return menuBuilder.build();
  }

  private void doSelectSectionAction(
      Scene scene,
      DepanFxNodeListTableAdapter tableAdapter,
      DepanFxNodeListSection before) {
    prepareSectionChooser(tableAdapter).showOpenDialog(scene)
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(p ->
            tableAdapter.getWorkspace().getWorkspaceResource(
                  p, DepanFxBaseSectionData.class))
        .ifPresent(r -> tableAdapter.insertSection(before, r));
  }

  private DepanFxResourceChooser prepareSectionChooser(
      DepanFxNodeListTableAdapter tableAdapter) {
    DepanFxResourceFilterModel rsrcFilter =
        new DepanFxResourceFilterModel.Composite(
            NODE_LIST_SECTIONS,
            new DepanFxResourceFilter[] {
                DepanFxFlatSectionToolDialog.FLAT_SECTION_RSRC_FILTER,
                DepanFxFoldSectionToolDialog.FOLD_SECTION_RSRC_FILTER,
                DepanFxTreeSectionToolDialog.TREE_SECTION_RSRC_FILTER
            });
    return prepareSectionChooser(tableAdapter, rsrcFilter);
  }

  private static void runInsertMemberTreeSectionAction(
      DepanFxNodeListTableAdapter tableAdapter,
      DepanFxNodeListSection before) {
    getInitialTreeSectionResource(tableAdapter)
        .ifPresent(r -> tableAdapter.insertSection(before, r));
  }

  private static void runInsertFoldSectionAction(
      DepanFxNodeListTableAdapter tableAdapter,
      DepanFxNodeListSection before) {
    getInitialFoldSectionResource(tableAdapter)
        .ifPresent(r -> tableAdapter.insertSection(before, r));
  }

  private static Optional<DepanFxWorkspaceResource<DepanFxTreeSectionData>>
      getInitialTreeSectionResource(
          DepanFxNodeListTableAdapter tableAdapter) {
    ContextModelId modelId = tableAdapter.getGraphDoc().getContextModelId();

    return DepanFxProjects.getBuiltIn(
        tableAdapter.getWorkspace(), DepanFxTreeSectionData.class,
        c -> isContextModelMatcherResource(c, modelId));
  }

  private static boolean isContextModelMatcherResource(
      DepanFxBuiltInContribution<DepanFxTreeSectionData> contrib,
      ContextModelId modelId) {
    return DepanFxLinkMatcherGroup.isContextModelMatcherResource(
        modelId, contrib.getDocument().getLinkMatcherRsrc());
  }

  private static Optional<DepanFxWorkspaceResource<DepanFxFoldSectionData>>
      getInitialFoldSectionResource(
          DepanFxNodeListTableAdapter tableAdapter) {
    DepanFxFoldSectionData result =
        DepanFxFoldSectionData.emptyFoldSectionData(
            tableAdapter.getWorkspace(), tableAdapter.getGraphDocResource());
    return Optional.of(tableAdapter.getWorkspace().addScratchResource(result));
  }
}
