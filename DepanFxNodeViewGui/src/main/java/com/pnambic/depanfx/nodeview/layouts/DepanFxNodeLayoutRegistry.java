package com.pnambic.depanfx.nodeview.layouts;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodeview.gui.DepanFxNodeViewPanel;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxMenuItemFactory;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.event.ActionEvent;
import javafx.scene.control.Menu;

@Component
public class DepanFxNodeLayoutRegistry {

  public static final String ALL_LAYOUTS = "All Layouts";

  public static final String ALL_LAYOUTS_EXT = "d*lti";

  public static final String ALL_LAYOUTS_GLOB =
      DepanFxSceneControls.buildMatchGlob(ALL_LAYOUTS_EXT);

  public interface Contribution {

    /**
     * Used for menu presentation and sort order.
     */
    String getLabel();

    /**
     * For resource selection choosers.
     */
    DepanFxResourceFilter getResourceFilter();

    /**
     * Some layouts are immediate (e.g. grid).
     * Other layouts might provide need a dialog before proceeding.
     */
    void handleLayout(ActionEvent e, DepanFxNodeViewPanel view);

    /**
     * Implementations will want to cast the supplied layout resource
     * to their underlying data type.
     */
    Map<GraphNode, DepanFxNodeLocationData> layoutNodes(
        DepanFxNodeViewPanel view,
        DepanFxWorkspaceResource<?> layoutRsrc,
        Collection<GraphNode> updateNodes);

    /**
     * Layouts that require additional context from {@code DepanFxNodeViewPanel}
     * (e.g. shift layouts want the current location) may not behave as expected
     * with this interface.
     *
     * Implementations will want to cast the supplied layout resource
     * to their underlying data type.
     */
    Map<GraphNode, DepanFxNodeLocationData> layoutNodes(
        DepanFxWorkspaceResource<GraphDocument> graphDocRsrc,
        DepanFxWorkspaceResource<?> layoutRsrc,
        Collection<GraphNode> updateNodes);
  }

  private final List<Contribution> layoutContribs;

  @Autowired
  public DepanFxNodeLayoutRegistry(List<Contribution> layoutContribs) {
    this.layoutContribs = layoutContribs;
  }

  /**
   * Apply every contribution that matches this document's file name extension.
   */
  public void popuplateLayoutMenu(
      Menu menu, Predicate<Contribution> layoutFilter,
      DepanFxNodeViewPanel view) {

    DepanFxMenuItemFactory menuFactory = new DepanFxMenuItemFactory(menu);
    ordered(layoutFilter)
        .forEach(c -> menuFactory.appendActionItem(
            c.getLabel(), e -> c.handleLayout(e, view)));
  }

  public List<DepanFxResourceFilter> getOpenFilters(
      Predicate<Contribution> layoutFilter) {
    return ordered(layoutFilter)
        .map(c -> c.getResourceFilter())
        .filter(f -> f != null)
        .collect(Collectors.toList());
  }

  /**
   * Build a composite resource filter from the resource filters included
   * in each layout contribution.
   */
  public DepanFxResourceFilter buildAllLayoutsFilter(
      Predicate<Contribution> layoutFilter) {
    List<Class<?>> resourceTypes = ordered(layoutFilter)
        .filter(c -> c.getResourceFilter() != null)
        .flatMap(c -> c.getResourceFilter().streamTypes())
        .collect(Collectors.toList());
    return new DepanFxResourceFilter(
        ALL_LAYOUTS,
        Collections.singletonList(ALL_LAYOUTS_GLOB),
        resourceTypes);
  }

  public Map<GraphNode, DepanFxNodeLocationData> layoutNodes(
      DepanFxNodeViewPanel view,
      DepanFxWorkspaceResource<?> layoutRsrc,
      Collection<GraphNode> updateNodes) {
    return findLayoutContribution(layoutRsrc)
        .map(l -> l.layoutNodes(view, layoutRsrc, updateNodes))
        .orElse(Collections.emptyMap());
    }

  public Map<GraphNode, DepanFxNodeLocationData> layoutNodes(
      DepanFxWorkspaceResource<GraphDocument> graphDocRsrc,
      DepanFxWorkspaceResource<?> layoutRsrc,
      Collection<GraphNode> updateNodes) {
    return findLayoutContribution(layoutRsrc)
        .map(l -> l.layoutNodes(graphDocRsrc, layoutRsrc, updateNodes))
        .orElse(Collections.emptyMap());
    }

  private Optional<Contribution> findLayoutContribution(
      DepanFxWorkspaceResource<?> layoutRsrc) {
    return layoutContribs.stream()
        .map(this::examine)
        .filter(c -> c.getResourceFilter() != null)
        .filter(c -> c.getResourceFilter().matchDocument(layoutRsrc.getResource()))
        .findFirst();
  }

  private Stream<Contribution> ordered(
      Predicate<? super Contribution> resourceFilter) {
    return layoutContribs.stream()
        .filter(resourceFilter)
        .sorted((a, b) -> compare(a, b));
  }

  private int compare(Contribution one, Contribution two) {
    return one.getLabel().compareTo(two.getLabel());
  }

  private <T> T examine(T item) {
    return item;
  }
}
