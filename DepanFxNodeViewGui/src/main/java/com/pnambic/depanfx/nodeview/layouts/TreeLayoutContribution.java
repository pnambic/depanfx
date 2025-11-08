package com.pnambic.depanfx.nodeview.layouts;

import com.pnambic.depanfx.edgematchers.link.DepanFxLinkMatchersRegistry;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxBaseMatcherDocument;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcher;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModelBuilder;
import com.pnambic.depanfx.nodeview.gui.DepanFxNodeViewPanel;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxTreeLayoutData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxTreeLayoutData.Direction;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;

@Component
public class TreeLayoutContribution
    implements DepanFxNodeLayoutRegistry.Contribution {

  public static final String TREE_LAYOUT = "Tree Layout...";

  public static final double MIN_SPACE_PER_LEAF =
      5 * DirectLayoutRunner.UNIT;

  public static final double MIN_SPACE_PER_LEVEL =
      3 * DirectLayoutRunner.UNIT;

  public static final double TARGET_ASPECT_RATIO = 3.0d;

  public static final double TARGET_TREE_HIEGHT =
      MIN_SPACE_PER_LEAF * 20;

  public static final double TARGET_TREE_WIDTH =
      TARGET_TREE_HIEGHT / TARGET_ASPECT_RATIO;

  private final DepanFxLinkMatchersRegistry matcherRegistry;

  @Autowired
  public TreeLayoutContribution(DepanFxLinkMatchersRegistry matcherRegistry) {
    this.matcherRegistry = matcherRegistry;
  }

  @Override
  public String getLabel() {
    return TREE_LAYOUT;
  }

  @Override
  public DepanFxResourceFilter getResourceFilter() {
    return DepanFxTreeLayoutToolDialog.TREE_LAYOUT_RSRC_FILTER;
  }

  @Override
  public Map<GraphNode, DepanFxNodeLocationData> layoutNodes(
      DepanFxNodeViewPanel view,
      DepanFxWorkspaceResource<?> layoutRsrc,
      Collection<GraphNode> updateNodes) {
    return layoutNodes(view.getGraphDocRsrc(), layoutRsrc, updateNodes);
  }


  @Override
  public Map<GraphNode, DepanFxNodeLocationData> layoutNodes(
      DepanFxWorkspaceResource<GraphDocument> graphDocRsrc,
      DepanFxWorkspaceResource<?> layoutRsrc,
      Collection<GraphNode> updateNodes) {
    DepanFxTreeLayoutData treeData =
        (DepanFxTreeLayoutData) layoutRsrc.getResource();

    DepanFxBaseMatcherDocument matcherDoc =
        treeData.getHierarchyMatcherRsrc().getResource();
    DepanFxLinkMatcher linkMatcher = matcherRegistry.buildMatcher(matcherDoc);

    return buildNodeLocations(
        graphDocRsrc, updateNodes, treeData.getDirection(), linkMatcher);
  }

  @Override
  public void handleLayout(ActionEvent e, DepanFxNodeViewPanel view) {
    DepanFxTreeLayoutData initialData =
        new DepanFxTreeLayoutData(
            "Tree layout", "Tree layout by membership hierarchy.",
            DepanFxTreeLayoutData.Direction.RIGHT,
            view.getHierachyMatcherRsrc().get());
    DepanFxWorkspaceResource<DepanFxTreeLayoutData> layoutRsrc =
        view.getWorkspace().addScratchResource(initialData);

    Dialog<DepanFxTreeLayoutToolDialog> layoutDlg =
        DepanFxTreeLayoutToolDialog.runCreateDialog(
            layoutRsrc, view.getDialogRunner());

    List<GraphNode> updateNodes =
        view.streamChosenNodes().collect(Collectors.toList());

    layoutDlg.getController().getToolResource()
        .ifPresent(r ->
            view.updateNodeLocations(layoutNodes(view, r, updateNodes)));
  }

  private Map<GraphNode, DepanFxNodeLocationData> buildNodeLocations(
      DepanFxWorkspaceResource<GraphDocument> graphDocRsrc,
      Collection<GraphNode> updateNodes,
      Direction direction,
      DepanFxLinkMatcher linkMatcher) {
    DepanFxTreeModelBuilder builder = new DepanFxTreeModelBuilder(linkMatcher);
    DepanFxTreeModel treeModel =
        builder.traverseGraph(graphDocRsrc, updateNodes);

    DryRunLayoutRunner dryRunLayout = new DryRunLayoutRunner(treeModel);
    dryRunLayout.layoutNodes(updateNodes);
    int leafs = dryRunLayout.getLeafCount();
    int levels = dryRunLayout.getMaxLevel();

    TreeLayoutRunner treeLayout =
        calcDirectionRunner(direction, treeModel, leafs, levels);
    treeLayout.layoutNodes(updateNodes);

    // TODO:  center the tree, cluster the orphans.
    return treeLayout.getPositions(updateNodes);
  }

  private TreeLayoutRunner calcDirectionRunner(
      DepanFxTreeLayoutData.Direction direction,
      DepanFxTreeModel treeModel,
      int leafs, int levels) {
    switch (direction) {
      case RIGHT:
        return calcBaseRight(treeModel, leafs, levels);
      case LEFT:
        return calcBaseLeft(treeModel, leafs, levels);
      case UP:
        return calcBaseUp(treeModel, leafs, levels);
      case DOWN:
        return calcBaseDown(treeModel, leafs, levels);
    }
    return null;
  }

  protected TreeLayoutRunner calcBaseRight(
      DepanFxTreeModel treeModel, int leafs, int levels) {

    double spacePerLeaf =
        Math.max(MIN_SPACE_PER_LEAF, TARGET_TREE_HIEGHT / leafs);

    double spacePerLevel =
        Math.max(MIN_SPACE_PER_LEVEL, TARGET_TREE_WIDTH / levels);

    // The tree runner assigns leafs from the bottom left heading upward
    // and to the right.
    double xBase = DirectLayoutRunner.X_ORIGIN -
        spacePerLevel * ((levels / 2.0d) - 0.5d);
    double yBase = DirectLayoutRunner.Y_ORIGIN -
        spacePerLeaf * ((leafs / 2.0d) - 0.5d);

    return new TreeLayoutRunner.Horizontal(
        treeModel, xBase, yBase, DirectLayoutRunner.Z_ORIGIN,
        spacePerLevel, spacePerLeaf);
  }

  protected TreeLayoutRunner calcBaseLeft(DepanFxTreeModel treeModel, int leafs, int levels) {

    double spacePerLeaf =
        Math.max(MIN_SPACE_PER_LEAF, TARGET_TREE_HIEGHT / leafs);

    double spacePerLevel =
        Math.max(MIN_SPACE_PER_LEVEL, TARGET_TREE_WIDTH / levels);

    // The tree runner assigns leafs from the bottom left heading upward
    // and to the right.
    double xBase = DirectLayoutRunner.X_ORIGIN +
        spacePerLevel * ((levels / 2.0d) - 0.5d);
    double yBase = DirectLayoutRunner.Y_ORIGIN -
        spacePerLeaf * ((leafs / 2.0d) - 0.5d);

    return new TreeLayoutRunner.Horizontal(
        treeModel, xBase, yBase, DirectLayoutRunner.Z_ORIGIN,
        -spacePerLevel, spacePerLeaf);
  }

  protected TreeLayoutRunner calcBaseUp(DepanFxTreeModel treeModel, int leafs, int levels) {

    double spacePerLeaf =
        Math.max(MIN_SPACE_PER_LEAF, TARGET_TREE_HIEGHT / leafs);

    double spacePerLevel =
        Math.max(MIN_SPACE_PER_LEVEL, TARGET_TREE_WIDTH / levels);

    // The tree runner assigns leafs from the bottom left heading upward
    // and to the right.
    double xBase = DirectLayoutRunner.X_ORIGIN -
        spacePerLevel * ((leafs / 2.0d) - 0.5d);
    double yBase = DirectLayoutRunner.Y_ORIGIN -
        spacePerLeaf * ((levels / 2.0d) - 0.5d);

    return new TreeLayoutRunner.Vertical(
        treeModel, xBase, yBase, DirectLayoutRunner.Z_ORIGIN,
        spacePerLevel, spacePerLeaf);
  }

  protected TreeLayoutRunner calcBaseDown(DepanFxTreeModel treeModel, int leafs, int levels) {

    double spacePerLeaf =
        Math.max(MIN_SPACE_PER_LEAF, TARGET_TREE_HIEGHT / leafs);

    double spacePerLevel =
        Math.max(MIN_SPACE_PER_LEVEL, TARGET_TREE_WIDTH / levels);

    // The tree runner assigns leafs from the bottom left heading upward
    // and to the right.
    double xBase = DirectLayoutRunner.X_ORIGIN -
        spacePerLevel * ((leafs / 2.0d) - 0.5d);
    double yBase = DirectLayoutRunner.Y_ORIGIN +
        spacePerLeaf * ((levels / 2.0d) - 0.5d);

    return new TreeLayoutRunner.Vertical(
        treeModel, xBase, yBase, DirectLayoutRunner.Z_ORIGIN,
        spacePerLevel, -spacePerLeaf);
  }
}
