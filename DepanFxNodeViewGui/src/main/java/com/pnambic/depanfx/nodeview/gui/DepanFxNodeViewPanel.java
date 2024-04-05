package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherGroup;
import com.pnambic.depanfx.nodeview.jogl.JoglLines;
import com.pnambic.depanfx.nodeview.jogl.JoglShapes;
import com.pnambic.depanfx.nodeview.layouts.DepanFxNodeLayoutRegistry;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewCameraData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData.LinkDisplayEntry;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewSceneData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceChooser;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;

public class DepanFxNodeViewPanel {

  private static final String SELECT_ALL_ITEM = "Select All";

  private static final String CLEAR_SELECTION_ITEM = "Clear Selection";

  private static final String INVERT_SELECTION_ITEM = "Invert Selection";

  private static final String SAVE_NODE_VIEW_ITEM = "Save node view...";

  private static final String LAYOUT_NODES = "Layout Nodes";

  private static final String SELECT_LAYOUT = "Select Layout...";

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeViewPanel.class);

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxNodeLayoutRegistry layoutRegistry;

  private final DepanFxNodeViewData viewData;

  private Collection<GraphNode> viewNodes;

  private Map<GraphNode, DepanFxNodeLocationData> nodeLocations;

  private Map<GraphNode, DepanFxNodeDisplayData> nodeDisplay;

  private Map<GraphEdge, DepanFxLineDisplayData> edgeDisplay;

  private Map<GraphNode, BooleanProperty> nodesCheckBoxStates;

  private DepanFxJoglView joglView;

  public DepanFxNodeViewPanel(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxNodeLayoutRegistry layoutRegistry,
      DepanFxNodeViewData viewData) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
    this.layoutRegistry = layoutRegistry;
    this.viewData = viewData;

    // Unpack the interesting parts of the view data.
    this.viewNodes = viewData.getViewNodes();
    this.nodeLocations = viewData.getNodeLocations();
    this.nodeDisplay = viewData.getNodeDisplay();
    this.edgeDisplay = viewData.getEdgeDisplay();

    nodesCheckBoxStates = buildNodesCheckBoxStates(viewNodes);
  }

  public Tab createWorkspaceTab(String tabTitle) {
    joglView = createJoglView();
    Tab result = new Tab(tabTitle, joglView);

    result.setOnSelectionChanged(new EventHandler<Event>() {

      @Override
      public void handle(Event event) {
        if (result.isSelected()) {
          joglView.activate();
        } else {
          joglView.release();
        }
      }
    });

    result.setOnClosed(new EventHandler<Event>() {

      @Override
      public void handle(Event event) {
        joglView.close();
      }
    });

    result.setContextMenu(buildViewContextMenu());
    return result;
  }

  public DepanFxWorkspace getWorkspace() {
    return workspace;
  }

  public DepanFxWorkspaceResource getGraphDocRsrc() {
    return viewData.getGraphDocRsrc();
  }

  public GraphDocument getGraphDoc() {
    return (GraphDocument) viewData.getGraphDocRsrc().getResource();
  }

  public ContextModelId getContextModelId() {
    return getGraphDoc().getContextModelId();
  }

  public <T> Dialog<T> buildDialog(Class<T> controllerType) {
    return dialogRunner.createDialogAndParent(controllerType);
  }

  public DepanFxDialogRunner getDialogRunner() {
    return dialogRunner;
  }

  /**
   * Might be selected nodes, or might by all nodes.
   */
  public Stream<GraphNode> streamChosenNodes() {
    // TODO: Check for selected nodes
    return viewNodes.stream();
  }

  public Optional<DepanFxWorkspaceResource> getHierachyMatcherRsrc() {
    ContextModelId modelId = getGraphDoc().getContextModelId();
    return DepanFxProjects.getBuiltIn(
        workspace, DepanFxLinkMatcherDocument.class,
        c -> this.byMemberLinkMatcherDoc(c, modelId));
  }

  private boolean byMemberLinkMatcherDoc(
      DepanFxBuiltInContribution contrib, Object modelId) {
    DepanFxLinkMatcherDocument linkMatchDoc =
        (DepanFxLinkMatcherDocument) contrib.getDocument();
    if (!linkMatchDoc.getMatchGroups()
        .contains(DepanFxLinkMatcherGroup.MEMBER)) {
      return false;
    }
    // [29-Nov-2023] Kludge for matches any, actual matcher provided later.
    if (linkMatchDoc.getModelId() == null) {
      return true;
    }
    return linkMatchDoc.getModelId().equals(modelId);
  }

  public void doSelectAllAction() {
    doSelectGraphNodesAction(viewNodes, true);
  }

  public void doClearSelectionAction() {
    doSelectGraphNodesAction(viewNodes, false);
  }

  public void doInvertSelectionAction() {
    viewNodes.stream()
        .forEach(this::doInvertGraphNodeAction);
  }

  public void doSelectGraphNodesAction(
      Collection<GraphNode> nodes, boolean value) {
    nodes.stream().forEach(n -> setSelectGraphNode(n, value));
  }

  public void doSelectGraphNodeAction(GraphNode node, boolean value) {
    setSelectGraphNode(node, value);
  }

  /**
   * Since the previous state may have been unknown, provide the final
   * state for interested parties.
   */
  public boolean doInvertGraphNodeAction(GraphNode node) {
    return invertSelectGraphNode(node);
  }

  public void updateNodeLocations(
      Map<GraphNode, DepanFxNodeLocationData> locations) {
    LOG.info("Updating location of {} nodes", locations.size());
    locations.entrySet().stream()
        .forEach(e -> updateNodeLocation(e.getKey(), e.getValue()));
  }

  /////////////////////////////////////
  // Internal

  private ContextMenu buildViewContextMenu() {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem(
        SELECT_ALL_ITEM, e -> doSelectAllAction());
    builder.appendActionItem(
        CLEAR_SELECTION_ITEM, e -> doClearSelectionAction());
    builder.appendActionItem(
        INVERT_SELECTION_ITEM, e -> doInvertSelectionAction());

    builder.appendSeparator();
    builder.appendActionItem(
        DepanFxNodeViewLinkDisplayDialog.EDIT_LINK_DISPLAY,
        e -> runEditLinkDisplayDialog());
    // PENDING: builder.appendActionItem(
    //     EDIT_NODE_DISPLAY, e -> runEditLinkDisplayDialog());

    builder.appendSeparator();
    builder.appendSubMenu(buildLayoutNodesMenu());

    builder.appendSeparator();
    builder.appendActionItem(
        SAVE_NODE_VIEW_ITEM, e -> runSaveNodeViewDialog());
    return builder.build();
  }

  private Menu buildLayoutNodesMenu() {
    Menu result = new Menu(LAYOUT_NODES);

    ObservableList<MenuItem> items = result.getItems();
    items.add(DepanFxContextMenuBuilder.createActionItem(
        SELECT_LAYOUT, e -> doSelectLayoutAction()));

    items.add(new SeparatorMenuItem());
    layoutRegistry.popuplateLayoutMenu(result, c -> true, this);
    return result;
  }

  private void doSelectLayoutAction() {
    DepanFxResourceChooser rsrcChooser =
        new DepanFxResourceChooser(workspace, dialogRunner);

    DepanFxResourcePerspectives.prepareResourceFinder(
        rsrcChooser, DepanFxNodeViewData.NODE_VIEW_TOOL_PATH);

    ObservableList<DepanFxResourceFilter> filters =
        rsrcChooser.getExtensionFilters();

    filters.addAll(layoutRegistry.getOpenFilters(c -> true));
    DepanFxResourceFilter allLayoutsFilter =
        layoutRegistry.buildAllLayoutsFilter(c -> true);
    filters.add(allLayoutsFilter);
    rsrcChooser.setSelectedExtensionFilter(allLayoutsFilter);

    rsrcChooser.showOpenDialog(joglView.getScene())
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(m -> workspace.getWorkspaceResource(m, "Node Layout"))
        .ifPresent(this::layoutNodes);
  }

  private void layoutNodes(DepanFxWorkspaceResource wkspRsrc) {
    List<GraphNode> updateNodes =
        streamChosenNodes().collect(Collectors.toList());
    updateNodeLocations(
        layoutRegistry.layoutNodes(wkspRsrc, getGraphDocRsrc(), updateNodes));
  }

  private void runEditLinkDisplayDialog() {
    DepanFxNodeViewLinkDisplayData linkDisplayData = buildLinkDisplayData();
    DepanFxNodeViewLinkDisplayDialog.runEditDialog(
        viewData.getLinkDisplayDocRsrc().getDocument(),
        linkDisplayData, dialogRunner);

    // TODO: apply any outstanding changes from the dialog.
    // However, most changes should be live modifications.
  }

  private DepanFxNodeViewLinkDisplayData buildLinkDisplayData() {
    DepanFxNodeViewLinkDisplayData linkDisplayData =
        (DepanFxNodeViewLinkDisplayData) viewData.getLinkDisplayDocRsrc().getResource();
    List<LinkDisplayEntry> linkDisplayEntries =
        linkDisplayData.streamLinkDisplay().collect(Collectors.toList());
    return new DepanFxNodeViewLinkDisplayData(
        viewData.getToolName(), viewData.getToolDescription(),
        linkDisplayData.getContextModelId(), linkDisplayEntries);
  }

  private void runSaveNodeViewDialog() {
    DepanFxNodeViewData saveView = buildSaveView();

    DepanFxResourcePerspectives.runCreateDialog(
        saveView, dialogRunner,
        DepanFxSaveNodeViewDialog.class, "Save node view");
  }

  /////////////////////////////////////
  // Render

  private DepanFxNodeViewData buildSaveView() {
    DepanFxNodeViewData result = new DepanFxNodeViewData(
        viewData.getToolName(), viewData.getToolDescription(),
        buildSceneData(),
        viewData.getGraphDocRsrc(), viewData.getLinkDisplayDocRsrc(),
        viewNodes, nodeLocations, nodeDisplay, edgeDisplay);
    return result ;
  }

  private DepanFxNodeViewSceneData buildSceneData() {
    DepanFxNodeViewCameraData cameraInfo =
        joglView.getCameraData();
    DepanFxNodeViewSceneData result = new DepanFxNodeViewSceneData(
        viewData.getSceneData().getBackgroundColor(), cameraInfo);
    return result ;
  }

  private DepanFxJoglView createJoglView() {
    DepanFxNodeViewCameraData cameraInfo =
        viewData.getSceneData().getCameraInfo();
    DepanFxJoglView result =
        DepanFxJoglView.createJoglView(cameraInfo, dialogRunner);
    viewNodes.stream()
        .forEach(n -> installShape(result, n));
    getViewEdges()
        .forEach(e -> installEdge(result, e));
    return result;
  }

  private void updateNodeLocation(
      GraphNode node, DepanFxNodeLocationData location) {
    if (viewNodes.contains(node)) {
      JoglShapes.updateLocation(joglView, node, location);
      nodeLocations.put(node, location);
    }
  }

  private void installShape(DepanFxJoglView view, GraphNode node) {

    DepanFxNodeLocationData location = nodeLocations.get(node);
    if (location == null) {
      return;
    }
    DepanFxNodeDisplayData displayInfo = nodeDisplay.get(node);
    if (displayInfo == null) {
      return;
    }
    JoglShapes.installShape(view, node, location, displayInfo);
  }

  private Stream<GraphEdge> getViewEdges() {
    return getGraphDoc().getGraph().getEdges().stream()
        .map(GraphEdge.class::cast)
        .filter(this::isViewEdge);
  }

  private boolean isViewEdge(GraphEdge edge) {
    if (!viewNodes.contains(edge.getHead())) {
      return false;
    }
    if (!viewNodes.contains(edge.getTail())) {
      return false;
    }
    return true;
  }

  private void installEdge(DepanFxJoglView result, GraphEdge edge) {

    DepanFxNodeViewLinkDisplayData displayInfo =
        (DepanFxNodeViewLinkDisplayData) viewData.getLinkDisplayDocRsrc().getResource();

    displayInfo.getLinkDisplayEnty(edge)
        .ifPresent(l -> JoglLines.installLine(result, edge, l));
  }

  /////////////////////////////////////
  // Selected nodes

  private Map<GraphNode, BooleanProperty>
      buildNodesCheckBoxStates(Collection<GraphNode> nodes) {
    Map<GraphNode, BooleanProperty> result = new HashMap<>();
    nodes.forEach(
      n -> result.put(n, new SimpleBooleanProperty(false)));
    return result;
  }

  private Collection<GraphNode> getSelectedNodes() {
    return nodesCheckBoxStates.entrySet().stream()
        .filter(e -> e.getValue().getValue().booleanValue())
        .map(e -> e.getKey())
        .collect(Collectors.toList());
  }

  private BooleanProperty setSelectGraphNode(GraphNode node, boolean value) {
    BooleanProperty result = nodesCheckBoxStates.get(node);
    result.set(value);
    return result;
  }

  /**
   * Since the previous state may have been unknown, provide the final
   * state for interested parties.
   */
  private boolean invertSelectGraphNode(GraphNode node) {
    BooleanProperty checkedProperty = nodesCheckBoxStates.get(node);
    boolean result = !checkedProperty.get();
    checkedProperty.set(result);
    return result;
  }
}
