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
package com.pnambic.depanfx.nodelist.gui.sections;

import com.pnambic.depanfx.edgematchers.link.DepanFxLinkMatcherGroup;
import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.context.ContextNodeId;
import com.pnambic.depanfx.graph.context.GraphContextKeys;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListGraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.gui.sections.folds.DepanFxFoldSectionToolDialog;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxBaseSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxFoldSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderBy;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderDirection;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.Optional;

import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.TreeItem;

/**
 * Utility methods and classes for node list sections.
 */
public class DepanFxNodeListSections {

  public static final String INSERT_SECTION_MENU_LABEL = "Insert Section";

  public static final String SELECT_SECTION = "Select Section...";

  private static final String INSERT_ABOVE_FOLD_SECTION =
      "Insert Fold Section";

  private static final String INSERT_ABOVE_MEMBER_TREE_SECTION =
      "Insert Member Tree Section";

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeListSections.class);

  private DepanFxNodeListSections() {
    // Prevent instantiation.
  }

  public static String fmtDisplayName(
      String sectionLabel, int listSize, boolean displayNodeCount) {
    if (displayNodeCount) {
      if (listSize == 1) {
        return MessageFormat.format(
            "{0} ({1} node)", sectionLabel, listSize);

      }
      return MessageFormat.format(
          "{0} ({1} nodes)", sectionLabel, listSize);
    }
    return sectionLabel;
  }

  public static Menu newSectionMenu(
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

  private static void doSelectSectionAction(
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

  private static DepanFxResourceChooser prepareSectionChooser(
      DepanFxNodeListTableAdapter tableAdapter) {
    DepanFxResourceFilterModel rsrcFilter =
        new DepanFxResourceFilterModel.Composite(
            "Node List Sections",
            new DepanFxResourceFilter[] {
                DepanFxFlatSectionToolDialog.FLAT_SECTION_RSRC_FILTER,
                DepanFxFoldSectionToolDialog.FOLD_SECTION_RSRC_FILTER,
                DepanFxTreeSectionToolDialog.TREE_SECTION_RSRC_FILTER
            });
    return prepareSectionChooser(tableAdapter, rsrcFilter);
  }

  private static DepanFxResourceChooser prepareSectionChooser(
      DepanFxNodeListTableAdapter tableAdapter,
      DepanFxResourceFilterModel rsrcFilter) {

    DepanFxResourceChooser result = new DepanFxResourceChooser(
        tableAdapter.getWorkspace(), tableAdapter.getDialogRunner());

    DepanFxResourcePerspectives.prepareResourceFinder(
        result, DepanFxNodeListSectionData.SECTIONS_TOOL_PATH);
    result.getExtensionFilters().add(rsrcFilter);
    result.setSelectedExtensionFilter(rsrcFilter);
    return result;
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

  /**
   * Only use with graph node members.  Should not be used with sections, etc.
   */
  public static abstract class CompareMembers
      implements Comparator<TreeItem<DepanFxNodeListMember>> {

    private final OrderDirection direction;

    public CompareMembers(OrderDirection direction) {
      this.direction = direction;
    }

    @Override
    public int compare(
        TreeItem<DepanFxNodeListMember> itemOne,
        TreeItem<DepanFxNodeListMember> itemTwo) {
      DepanFxNodeListMember memberOne = itemOne.getValue();
      DepanFxNodeListMember memberTwo = itemTwo.getValue();
      return orderMembers(memberOne, memberTwo);
    }

    private int orderMembers(
        DepanFxNodeListMember memberOne, DepanFxNodeListMember memberTwo ) {
      switch (direction) {
      case FORWARD:
        return compareMembers(memberOne, memberTwo);
      case REVERSE:
        return - compareMembers(memberOne, memberTwo);
      }
      LOG.warn(
          "Unrecognize direction for section ordering {}", direction);
      return 0;
    }

    protected abstract int compareMembers(
        DepanFxNodeListMember memberOne, DepanFxNodeListMember memberTwo);
  }

  /**
   * A good choice to implement {@code CompareBySortKey.compareMembers()}.
   */
  public static int compareBySortKey(
      DepanFxNodeListMember memberOne, DepanFxNodeListMember memberTwo) {
    String oneKey = ((DepanFxNodeListGraphNode) memberOne).getSortKey();
    String twoKey = ((DepanFxNodeListGraphNode) memberTwo).getSortKey();
    return oneKey.compareTo(twoKey);
  }

  public static String getSortKey(GraphNode node, OrderBy orderBy) {
    ContextNodeId nodeId = node.getId();
    switch (orderBy) {
    case NODE_ID:
      return GraphContextKeys.toNodeKey(nodeId);
    case NODE_KEY:
      return nodeId.getNodeKey();
    case NODE_LEAF:
      return getLeafSortKey(nodeId.getNodeKey());
    case SIMPLE_NAME:
      return nodeId.getSimpleName();
    }

    // Use the full node key the orderBy value is not known.
    LOG.warn("Unrecognized order by criteria {}", orderBy);
    return GraphContextKeys.toNodeKey(nodeId);
  }

  public static String getLeafSortKey(String nodeKey) {
    return new File(nodeKey).getName();
  }
}
