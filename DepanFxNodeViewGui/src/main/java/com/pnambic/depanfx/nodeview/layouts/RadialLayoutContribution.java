package com.pnambic.depanfx.nodeview.layouts;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcher;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModelBuilder;
import com.pnambic.depanfx.nodeview.gui.DepanFxNodeViewPanel;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxRadialLayoutData;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;

@Component
public class RadialLayoutContribution
    implements DepanFxNodeLayoutRegistry.Contribution {

  public static final double MIN_ARC_SEPARATION =
      4 * RadialLayoutRunner.UNIT;

  public static final double MIN_RADIAL_SEPARATION =
      4 * RadialLayoutRunner.UNIT;

  public static final double MAX_RADIAL_SEPARATION =
      10 * RadialLayoutRunner.UNIT;

  private static final String RADIAL_LAYOUT = "Radial Layout...";

  @Override
  public String getLabel() {
    return RADIAL_LAYOUT;
  }

  @Override
  public DepanFxResourceFilter getResourceFilter() {
    return DepanFxRadialLayoutToolDialog.RADIAL_LAYOUT_RSRC_FILTER;
  }

  @Override
  public Map<GraphNode, DepanFxNodeLocationData> layoutNodes(
      DepanFxWorkspaceResource<?> layoutRsrc,
      DepanFxWorkspaceResource<GraphDocument> graphDocRsrc,
      List<GraphNode> updateNodes) {
    DepanFxRadialLayoutData radialData =
        (DepanFxRadialLayoutData) layoutRsrc.getResource();

    DepanFxLinkMatcherDocument matcherDoc =
        (DepanFxLinkMatcherDocument) radialData.getHierarchyMatcherRsrc().getResource();
    DepanFxLinkMatcher linkMatcher = matcherDoc.getMatcher();

    return buildNodeLocations(graphDocRsrc, updateNodes, linkMatcher);
  }

  @Override
  public void handleLayout(ActionEvent e, DepanFxNodeViewPanel view) {
    DepanFxRadialLayoutData initialData =
        new DepanFxRadialLayoutData(
            "Radial Layout", "Radial layout by membership hierarchy.",
            view.getHierachyMatcherRsrc().get());

    Dialog<DepanFxRadialLayoutToolDialog> layoutDlg =
        DepanFxRadialLayoutToolDialog.runCreateDialog(
            initialData, view.getDialogRunner());

    DepanFxWorkspaceResource<GraphDocument> graphDocRsrc =
        view.getGraphDocRsrc();
    List<GraphNode> updateNodes =
        view.streamChosenNodes().collect(Collectors.toList());

    layoutDlg.getController().getWorkspaceResource()
        .ifPresent(r -> view.updateNodeLocations(
            layoutNodes(r, graphDocRsrc, updateNodes)));
  }

  private Map<GraphNode, DepanFxNodeLocationData> buildNodeLocations(
      DepanFxWorkspaceResource<GraphDocument> graphDocRsrc,
      List<GraphNode> updateNodes,
      DepanFxLinkMatcher linkMatcher) {
    DepanFxTreeModelBuilder builder = new DepanFxTreeModelBuilder(linkMatcher);
    DepanFxTreeModel treeModel =
        builder.traverseGraph(graphDocRsrc, updateNodes);

    DryRunLayoutRunner dryRunLayout = new DryRunLayoutRunner(treeModel);
    dryRunLayout.layoutNodes(updateNodes);

    int rootLevel = RadialLayoutRunner.calcRootLevel(updateNodes);
    int circumference = dryRunLayout.getLeafCount();
    int levels = dryRunLayout.getMaxLevel() + rootLevel;
    double radiansPerLeaf = 2.0 * Math.PI / circumference;
    double radiusTotal = MIN_ARC_SEPARATION / radiansPerLeaf;
    double radiusPerLevel =
        Math.min(MAX_RADIAL_SEPARATION,
        Math.max(MIN_RADIAL_SEPARATION, radiusTotal / levels));

    RadialLayoutRunner radialLayout = new RadialLayoutRunner(
        treeModel,
        RadialLayoutRunner.X_ORIGIN,
        RadialLayoutRunner.Y_ORIGIN,
        RadialLayoutRunner.Z_ORIGIN,
        rootLevel, radiansPerLeaf, radiusPerLevel);
    radialLayout.layoutNodes(updateNodes);

    // Radial is naturally centered.
    // TODO:  cluster the orphans.
    return radialLayout.getPositions(updateNodes);
  }
}
