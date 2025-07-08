package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxFlatSection;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSection;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSectionBuiltIns;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSectionItem;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxFlatSectionData;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;

public class DepanFxNodeListRootItem extends DepanFxNodeListItem {

  private boolean sectionLoaded = false;

  public DepanFxNodeListRootItem(DepanFxNodeListRoot rootInfo) {
    super(rootInfo);
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

  @Override
  public ContextMenu getNodeContextMenu(Scene scene,
      DepanFxNodeListTableAdapter tableAdapter, GraphNode node) {
    // The root is never displayed in the table.
    return null;
  }

  private ObservableList<TreeItem<DepanFxNodeListMember>> buildChildren() {

    DepanFxNodeListRoot root = (DepanFxNodeListRoot) getValue();

    ObservableList<TreeItem<DepanFxNodeListMember>> result =
        FXCollections.observableArrayList();

    DepanFxNodeList baseNodes = root.getNodeList();

    for (DepanFxNodeListSection section : root.getSections()) {
      DepanFxNodeListSectionItem sectionItem =
          section.buildSectionItem(baseNodes);
      result.add(sectionItem);

      DepanFxNodeList sectionNodes = section.getSectionNodes();
      baseNodes = DepanFxNodeLists.remove(baseNodes, sectionNodes);
    }

    // If any nodes are left, drop them in a special section.
    if (!baseNodes.getNodes().isEmpty()) {

      // Capture a snapshot of baseNodes.
      DepanFxNodeList remainer = baseNodes;
      getBuiltinSimpleSectionResource(root.getWorkspace())
          .map(r -> new DepanFxFlatSection(r))
          .map(s -> s.buildSectionItem(remainer))
          .ifPresent(result::add);;
    }
    return result;
  }

  private static Optional<DepanFxWorkspaceResource<DepanFxFlatSectionData>>
  getBuiltinSimpleSectionResource(DepanFxWorkspace workspace) {

    return DepanFxProjects.getBuiltIn(
        workspace, DepanFxFlatSectionData.class,
        DepanFxNodeListSectionBuiltIns.SIMPLE_SECTION_TOOL_PATH);
  }
}
