/*
 * Copyright 2025 The Depan Project Authors
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
package com.pnambic.depanfx.nodelist.gui.nodefilters;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphModel;
import com.pnambic.depanfx.nodefilters.gui.DepanFxNodeFiltersChooser;
import com.pnambic.depanfx.nodefilters.gui.DepanFxNodeFiltersDialogRegistry;
import com.pnambic.depanfx.nodefilters.model.DepanFxBaseFilter;
import com.pnambic.depanfx.nodefilters.model.DepanFxNodeFiltersRegistry;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.FilterMergeMode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListSelection;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxMenuBuilder;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;

public class FilterMenu {

  public static final String REPLACE_MODE_ITEM = "Replace";

  private static final String UNION_MODE_ITEM = "Union";

  private static final String INTERSECT_MODE_ITEM = "Intersect";

  private static final String SUBTRACT_MODE_ITEM = "Subtract (A - B)";

  private static final String REDUCED_MODE_ITEM = "Reduced (B - A)";

  private static final String KEEP_MODE_ITEM = "Keep";

  private static final String SELECT_FILTER_ITEM = "Select Filter...";

  private static final String MERGE_MODE_ITEM = "Merge Mode";

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private final Scene scene;

  private final DepanFxNodeFiltersRegistry filterRegistry;

  private final DepanFxNodeFiltersDialogRegistry filterDialogRegistry;

  private final GraphModel graphModel;

  private final DepanFxNodeListSelection nodeSelection;

  private ToggleGroup mergeModeGroup = new ToggleGroup();

  public FilterMenu(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      Scene scene,
      DepanFxNodeFiltersRegistry filterRegistry,
      DepanFxNodeFiltersDialogRegistry filterDialogRegistry,
      GraphModel graphModel,
      DepanFxNodeListSelection nodeSelection) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
    this.scene = scene;
    this.filterRegistry = filterRegistry;
    this.filterDialogRegistry = filterDialogRegistry;
    this.graphModel = graphModel;
    this.nodeSelection = nodeSelection;
  }

  public void addSelectFilterAction(DepanFxMenuBuilder menuBuilder) {
    menuBuilder.appendActionItem(
        SELECT_FILTER_ITEM, e -> runSelectFilterAction());
  }

  public void addMergeModeMenu(DepanFxMenuBuilder menuBuilder) {
    menuBuilder.appendSubMenu(buildMergeModeMenu());
  }

  private Menu buildMergeModeMenu() {

    buildMergeModeEntry(REPLACE_MODE_ITEM, FilterMergeMode.REPLACE);
    buildMergeModeEntry(UNION_MODE_ITEM, FilterMergeMode.UNION);
    buildMergeModeEntry(INTERSECT_MODE_ITEM, FilterMergeMode.INTERSECT);
    buildMergeModeEntry(SUBTRACT_MODE_ITEM, FilterMergeMode.A_SUB_B);
    buildMergeModeEntry(REDUCED_MODE_ITEM, FilterMergeMode.B_SUB_A);
    buildMergeModeEntry(KEEP_MODE_ITEM, FilterMergeMode.KEEP);

    DepanFxMenuBuilder mergeModeBuilder =
        new DepanFxMenuBuilder(MERGE_MODE_ITEM);
    mergeModeGroup.getToggles().stream()
        .map(t -> (RadioMenuItem) t)
        .forEach(mergeModeBuilder::appendMenuItem);

    // Set default selection
    getMergeMode(FilterMergeMode.REPLACE)
        .ifPresent(mergeModeGroup::selectToggle);

    return mergeModeBuilder.build();
  }

  private void runSelectFilterAction() {
    DepanFxNodeFiltersChooser.runNodeFiltersFinder(
        workspace, dialogRunner, scene, filterDialogRegistry)
        .ifPresent(filterRsrc -> evaluate(filterRsrc.getResource()));
  }

  public void evaluate(DepanFxBaseFilterData filterData) {
    List<GraphNode> sourceNodes = nodeSelection.streamChosenNodes()
        .collect(Collectors.toList());
    Collection<GraphNode> targetNodes = graphModel.getGraphNodes();

    DepanFxBaseFilter<?> filter =
        filterRegistry.buildFilter(filterData, graphModel, targetNodes);

    Collection<GraphNode> filterNodes = filter.computeNodes(sourceNodes);

    // Use selected merge mode to combine result with source nodes
    Collection<GraphNode> resultNodes = DepanFxBaseFilter.mergeNodes(
        getMergeMode(), sourceNodes, filterNodes);

    nodeSelection.doSelectGraphNodesAction(resultNodes);
  }

  protected Optional<RadioMenuItem> getMergeMode(FilterMergeMode mergeMode) {
    return mergeModeGroup.getToggles().stream()
        .filter(t -> t.getUserData().equals(mergeMode))
        .map(t -> (RadioMenuItem) t)
        .findFirst();
  }

  protected FilterMergeMode getMergeMode() {
    return (FilterMergeMode) mergeModeGroup.getSelectedToggle().getUserData();
  }

  private void buildMergeModeEntry(
      String entryLabel, FilterMergeMode entryMergeMode) {
    RadioMenuItem result = new RadioMenuItem(entryLabel);
    result.setToggleGroup(mergeModeGroup);
    result.setUserData(entryMergeMode);
  }
}
