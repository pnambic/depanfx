package com.pnambic.depanfx.nodeview.layouts;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodeview.gui.DepanFxNodeViewPanel;
import com.pnambic.depanfx.nodeview.layouts.DepanFxNodeLayoutRegistry.Contribution;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxRadialLayoutData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxTreeLayoutData;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceRegistry;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
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

@Configuration
public class DepanFxNodeLayoutConfiguration {

  public static final String GRID_LAYOUT = "Grid Layout";

  public String RADIAL_LAYOUT_LABEL = "Radial Layout";

  public String RADIAL_LAYOUT_KEY = "Radial Layout";

  public String TREE_LAYOUT_LABEL = "Tree Layout";

  public String TREE_LAYOUT_KEY = "Tree Layout";

  @Autowired
  public DepanFxNodeLayoutConfiguration() {
  }

  @Bean
  public Contribution gridLayoutContribution() {
    return new GridLayoutContribution();
  }

  @Bean
  public Contribution shuffleLayoutContribution() {
    return new ShuffleLayoutContribution();
  }

  @Bean
  public DepanFxResourceRegistry.Contribution radialLayoutResourceContribution() {
    return new RadialLayoutResourceContribution();
  }

  @Bean
  public DepanFxResourceRegistry.Contribution treeLayoutResourceContribution() {
    return new TreeLayoutResourceContribution();
  }

  private static class GridLayoutContribution implements Contribution {

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

  private static class ShuffleLayoutContribution implements Contribution {

    @Override
    public String getLabel() {
      return "Shuffle Grid";
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

  private class RadialLayoutResourceContribution
      extends DepanFxResourceRegistry.Principal<DepanFxRadialLayoutData>{

    public RadialLayoutResourceContribution() {
      super(
          RADIAL_LAYOUT_LABEL,
          DepanFxRadialLayoutData.class,
          DepanFxRadialLayoutData.RADIAL_LAYOUT_TOOL_EXT,
          RADIAL_LAYOUT_KEY);
    }

    @Override
    protected void runDialog(
        DepanFxDialogRunner dialogRunner,
        Map<?, ?> loadContext,
        DepanFxWorkspaceResource<DepanFxRadialLayoutData> wkspRsrc) {
      DepanFxRadialLayoutToolDialog.runEditDialog(wkspRsrc, dialogRunner);
    }
  }

  private class TreeLayoutResourceContribution
      extends DepanFxResourceRegistry.Principal<DepanFxTreeLayoutData>{

    public TreeLayoutResourceContribution() {
      super(
          TREE_LAYOUT_LABEL,
          DepanFxTreeLayoutData.class,
          DepanFxTreeLayoutData.TREE_LAYOUT_TOOL_EXT,
          TREE_LAYOUT_KEY);
    }

    @Override
    protected void runDialog(
        DepanFxDialogRunner dialogRunner,
        Map<?, ?> loadContext,
        DepanFxWorkspaceResource<DepanFxTreeLayoutData> wkspRsrc) {
      DepanFxTreeLayoutToolDialog.runEditDialog(wkspRsrc, dialogRunner);
    }
  }
}
