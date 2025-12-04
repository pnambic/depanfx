package com.pnambic.depanfx.nodeview.layouts;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodeview.gui.DepanFxNodeViewPanel;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxRadialLayoutData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxTreeLayoutData;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceRegistryContribution;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;

/**
 * Registers built-in node layouts.
 * - List and Shuffle grid layouts.
 * - Horizontal and Vertical linear layouts.
 * - Radial and Tree hierarchical layouts.
 *
 * The grid and linear layouts have no options
 * and the layout action is immediate.
 *
 * The hierarchical layouts lead to dialogs.
 * These layouts need to choose
 * at least the hierarchy's relationships,
 * and often have additional options.
 */
@Configuration
public class DepanFxNodeLayoutConfiguration {

  public static final String GRID_LAYOUT = "Grid Layout";

  public static final String SHUFFLE_GRID = "Shuffle Grid";

  private static final String HORIZONTAL_LAYOUT = "Horizontal Layout";

  private static final String VERTICAL_LAYOUT = "Vertical Layout";

  private static final String STACKED_LAYOUT = "Stacked Layout";

  public static final String RADIAL_LAYOUT_LABEL = "Radial Layout";

  public static final String RADIAL_LAYOUT_KEY = "Radial Layout";

  public static final String TREE_LAYOUT_LABEL = "Tree Layout";

  public static final String TREE_LAYOUT_KEY = "Tree Layout";

  @Autowired
  public DepanFxNodeLayoutConfiguration() {
  }

  @Bean
  public DepanFxNodeLayoutRegistry.Contribution gridLayoutContribution() {
    return new GridLayoutContribution();
  }

  @Bean
  public DepanFxNodeLayoutRegistry.Contribution shuffleLayoutContribution() {
    return new ShuffleLayoutContribution();
  }

  @Bean
  public DepanFxNodeLayoutRegistry.Contribution horizontalLayoutContribution() {
    return new LinearLayoutContribution(
        HORIZONTAL_LAYOUT, LinearLayoutRunner.LayoutDirection.HORIZONTAL);
  }

  @Bean
  public DepanFxNodeLayoutRegistry.Contribution verticalLayoutContribution() {
    return new LinearLayoutContribution(
        VERTICAL_LAYOUT, LinearLayoutRunner.LayoutDirection.VERTICAL);
  }

  @Bean
  public DepanFxNodeLayoutRegistry.Contribution stackedLayoutContribution() {
    return new LinearLayoutContribution(
        STACKED_LAYOUT, LinearLayoutRunner.LayoutDirection.STACKED);
  }

  @Bean
  public DepanFxResourceRegistryContribution<DepanFxRadialLayoutData>
  radialLayoutResourceContribution() {
    return new RadialLayoutResourceContribution();
  }

  @Bean
  public DepanFxResourceRegistryContribution<DepanFxTreeLayoutData>
  treeLayoutResourceContribution() {
    return new TreeLayoutResourceContribution();
  }

  private static class GridLayoutContribution
      implements DepanFxNodeLayoutRegistry.Contribution {

    @Override
    public String getLabel() {
      return GRID_LAYOUT;
    }

    @Override
    public DepanFxResourceFilter getResourceFilter() {
      return null;
    }

    @Override
    public void handleLayout(ActionEvent e, DepanFxNodeViewPanel view) {
      List<GraphNode> updateNodes = view.streamChosenNodes()
          .collect(Collectors.toList());
      view.updateNodeLocations(
          GridLayoutRunner.buildNodeLocations(updateNodes));
    }

    @Override
    public Map<GraphNode, DepanFxNodeLocationData> layoutNodes(
        DepanFxNodeViewPanel view,
        DepanFxWorkspaceResource<?> layoutRsrc,
        Collection<GraphNode> updateNodes) {
      return GridLayoutRunner.buildNodeLocations(updateNodes);
    }

    @Override
    public Map<GraphNode, DepanFxNodeLocationData> layoutNodes(
        DepanFxWorkspaceResource<GraphDocument> graphDocRsrc,
        DepanFxWorkspaceResource<?> layoutRsrc,
        Collection<GraphNode> updateNodes) {
      return GridLayoutRunner.buildNodeLocations(updateNodes);
    }
  }

  private static class ShuffleLayoutContribution
    implements DepanFxNodeLayoutRegistry.Contribution {

    @Override
    public String getLabel() {
      return SHUFFLE_GRID;
    }

    @Override
    public DepanFxResourceFilter getResourceFilter() {
      return null;
    }

    @Override
    public void handleLayout(ActionEvent e, DepanFxNodeViewPanel view) {
      List<GraphNode> updateNodes = view.streamChosenNodes()
          .collect(Collectors.toList());
      Collections.shuffle(updateNodes);
      view.updateNodeLocations(
          GridLayoutRunner.buildNodeLocations(updateNodes));
    }

    @Override
    public Map<GraphNode, DepanFxNodeLocationData> layoutNodes(
        DepanFxNodeViewPanel view,
        DepanFxWorkspaceResource<?> layoutRsrc,
        Collection<GraphNode> updateNodes) {
      return GridLayoutRunner.buildNodeLocations(updateNodes);
    }

    @Override
    public Map<GraphNode, DepanFxNodeLocationData> layoutNodes(
        DepanFxWorkspaceResource<GraphDocument> graphDocRsrc,
        DepanFxWorkspaceResource<?> layoutRsrc,
        Collection<GraphNode> updateNodes) {
      return GridLayoutRunner.buildNodeLocations(updateNodes);
    }
  }

  private static class LinearLayoutContribution
    implements DepanFxNodeLayoutRegistry.Contribution {

    private final String label;

    private final LinearLayoutRunner.LayoutDirection direction;

    private LinearLayoutContribution(
        String label, LinearLayoutRunner.LayoutDirection direction) {
      this.label = label;
      this.direction = direction;
    }

    @Override
    public String getLabel() {
      return label;
    }

    @Override
    public DepanFxResourceFilter getResourceFilter() {
      return null;
    }

    @Override
    public void handleLayout(ActionEvent e, DepanFxNodeViewPanel view) {
      List<GraphNode> updateNodes = view.streamChosenNodes()
          .collect(Collectors.toList());
      view.updateNodeLocations(layoutNodes(updateNodes));
    }

    @Override
    public Map<GraphNode, DepanFxNodeLocationData> layoutNodes(
        DepanFxNodeViewPanel view,
        DepanFxWorkspaceResource<?> layoutRsrc,
        Collection<GraphNode> updateNodes) {
      return layoutNodes(updateNodes);
    }

    @Override
    public Map<GraphNode, DepanFxNodeLocationData> layoutNodes(
        DepanFxWorkspaceResource<GraphDocument> graphDocRsrc,
        DepanFxWorkspaceResource<?> layoutRsrc,
        Collection<GraphNode> updateNodes) {
      return layoutNodes(updateNodes);
    }

    private Map<GraphNode, DepanFxNodeLocationData> layoutNodes(
        Collection<GraphNode> updateNodes) {
      return LinearLayoutRunner.buildNodeLocations(
          updateNodes, direction);
    }
  }

  private class RadialLayoutResourceContribution
      extends DepanFxResourceRegistryContribution.Principal<DepanFxRadialLayoutData>
      implements DepanFxResourceRegistryContribution.Dialog<DepanFxRadialLayoutData> {

    public RadialLayoutResourceContribution() {
      super(
          RADIAL_LAYOUT_LABEL,
          DepanFxRadialLayoutData.class,
          DepanFxRadialLayoutData.RADIAL_LAYOUT_TOOL_EXT,
          RADIAL_LAYOUT_KEY);
    }

    @Override
    public void runDialog(
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        DepanFxWorkspaceResource<DepanFxRadialLayoutData> layoutRsrc) {
      DepanFxRadialLayoutToolDialog.runEditDialog(layoutRsrc, dialogRunner);
    }
  }

  private class TreeLayoutResourceContribution
      extends DepanFxResourceRegistryContribution.Principal<DepanFxTreeLayoutData>
      implements DepanFxResourceRegistryContribution.Dialog<DepanFxTreeLayoutData> {

    public TreeLayoutResourceContribution() {
      super(
          TREE_LAYOUT_LABEL,
          DepanFxTreeLayoutData.class,
          DepanFxTreeLayoutData.TREE_LAYOUT_TOOL_EXT,
          TREE_LAYOUT_KEY);
    }

    @Override
    public void runDialog(
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        DepanFxWorkspaceResource<DepanFxTreeLayoutData> wkspRsrc) {
      DepanFxTreeLayoutToolDialog.runEditDialog(wkspRsrc, dialogRunner);
    }
  }
}
