package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodeview.jogl.JoglLines;
import com.pnambic.depanfx.nodeview.jogl.JoglShapes;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewCameraData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData.LinkDisplayEntry;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewSceneData;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Tab;

public class DepanFxNodeViewPanel {

  private static final String SELECT_ALL_ITEM = "Select All";

  private static final String CLEAR_SELECTION_ITEM = "Clear Selection";

  private static final String INVERT_SELECTION_ITEM = "Invert Selection";

  private static final String SAVE_NODE_VIEW_ITEM = "Save node view ..";

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeViewPanel.class);

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

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
      DepanFxNodeViewData viewData) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
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
    builder.appendSeparator();
    builder.appendActionItem(
        SAVE_NODE_VIEW_ITEM, e -> runSaveNodeViewDialog());
    return builder.build();
  }

  private void runEditLinkDisplayDialog() {
    DepanFxNodeViewLinkDisplayData linkDisplayData = buildLinkDisplayData();
    Dialog<DepanFxNodeViewLinkDisplayDialog> linkDisplayDgl =
        DepanFxNodeViewLinkDisplayDialog.runEditDialog(
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
    Dialog<DepanFxSaveNodeViewDialog> saveDlg =
        dialogRunner.createDialogAndParent(DepanFxSaveNodeViewDialog.class);
    DepanFxNodeViewData saveView = buildSaveView();
    saveDlg.getController().setNodeViewDoc(saveView);
    saveDlg.runDialog("Save node view");
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
