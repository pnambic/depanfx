package com.pnambic.depanfx.nodeview.layouts;

import com.pnambic.depanfx.graph.model.GraphNode;
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
      DepanFxWorkspaceResource layoutRsrc,
      DepanFxWorkspaceResource graphDocRsrc,
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

    DepanFxWorkspaceResource graphDocRsrc = view.getGraphDocRsrc();
    List<GraphNode> updateNodes =
        view.streamChosenNodes().collect(Collectors.toList());

    view.updateNodeLocations(layoutNodes(
        layoutDlg.getController().getWorkspaceResource().get(),
        graphDocRsrc, updateNodes));
  }

  private Map<GraphNode, DepanFxNodeLocationData> buildNodeLocations(
      DepanFxWorkspaceResource graphDocRsrc,
      List<GraphNode> updateNodes,
      DepanFxLinkMatcher linkMatcher) {
    DepanFxTreeModelBuilder builder = new DepanFxTreeModelBuilder(linkMatcher);
    DepanFxTreeModel treeModel =
        builder.traverseGraph(graphDocRsrc, updateNodes);

    DryRunLayoutRunner dryRunLayout = new DryRunLayoutRunner(treeModel);
    dryRunLayout.layoutNodes(updateNodes);
    int circumference = dryRunLayout.getLeafCount();
    double radiansPerLeaf = 2.0 * Math.PI / circumference;

    RadialLayoutRunner radialLayout =
        new RadialLayoutRunner(treeModel, radiansPerLeaf);
    radialLayout.layoutNodes(updateNodes);

    // Radial is naturally centered.
    // TODO:  cluster the orphans.
    return radialLayout.getPositions(updateNodes);
  }
}
