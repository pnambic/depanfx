package com.pnambic.depanfx.nodeview.layouts;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodeview.gui.DepanFxNodeViewPanel;
import com.pnambic.depanfx.nodeview.layouts.DepanFxNodeLayoutRegistry.Contribution;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;

@Configuration
public class DepanFxNodeLayoutConfiguration {

  private static final String GRID_LAYOUT = "Grid Layout";

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
        DepanFxWorkspaceResource<?> layoutRsrc,
        DepanFxWorkspaceResource<GraphDocument> graphDocRsrc,
        List<GraphNode> updateNodes) {
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
        DepanFxWorkspaceResource<?> layoutRsrc,
        DepanFxWorkspaceResource<GraphDocument> graphDocRsrc,
        List<GraphNode> updateNodes) {
      return GridLayoutRunner.buildNodeLocations(updateNodes);
    }
  }
}
