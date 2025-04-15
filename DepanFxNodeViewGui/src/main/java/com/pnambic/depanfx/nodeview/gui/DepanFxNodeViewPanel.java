/*
 * Copyright 2024 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.context.GraphContextKeys;
import com.pnambic.depanfx.graph.info.GraphNodeInfo;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxCompositeInfoRegistry;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry.Contribution;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.graph_doc.model.GraphModel;
import com.pnambic.depanfx.jogl.JoglMouseActionListener;
import com.pnambic.depanfx.nodefilters.gui.DepanFxNodeViewNodeFiltersDialog;
import com.pnambic.depanfx.nodefilters.model.DepanFxNodeFiltersRegistry;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListSelection;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableCommands;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListViewBuiltIns;
import com.pnambic.depanfx.nodelist.gui.DepanFxSaveNodeListDialog;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherGroup;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.nodeview.jogl.JoglPane;
import com.pnambic.depanfx.nodeview.jogl.JoglShapes;
import com.pnambic.depanfx.nodeview.layouts.DepanFxLayoutsChooser;
import com.pnambic.depanfx.nodeview.layouts.DepanFxNodeLayoutRegistry;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewCameraData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData.LinkDisplayEntry;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewSceneData;
import com.pnambic.depanfx.perspective.DepanFxProctor;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.scene.DepanFxSceneService;
import com.pnambic.depanfx.scene.DepanFxSceneViewer;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class DepanFxNodeViewPanel implements DepanFxSceneViewer {

  private static final String NODE_SELECTION_ITEM = "Node Selection...";

  private static final String FILTER_SELECTION_ITEM = "Filter Selection...";

  private static final String EDGE_DISPLAY = "Edge Display";

  private static final String SELECT_EDGE_DISPLAY = "Select Edge Display...";

  private static final String EDGE_VISIBLITY = "Edge Visibility";

  private static final String ALL_EDGES_VISIBLE = "Show All Edges";

  private static final String NO_EDGES_VISIBLE = "Hide All Edges";

  private static final String INVERT_EDGES_VISIBLE = "Invert Visible Edges";

  private static final String MORE_EDGE_VIBILITY = "More Edge Visibility...";

  private static final String NODE_DISPLAY = "Node Display";

  private static final String SELECT_NODE_DISPLAY = "Select Node Display...";

  private static final String NODE_VISIBLITY = "Node Visibility";

  private static final String ALL_NODES_VISIBLE = "Show All Nodes";

  private static final String NO_NODES_VISIBLE = "Hide All Nodes";

  private static final String INVERT_NODES_VISIBLE = "Invert Visible Nodes";

  private static final String MORE_NODE_VIBILITY = "More Node Visibility...";

  private static final String SAVE_NODE_VIEW_ITEM = "Save Node View...";

  private static final String LAYOUT_NODES = "Layout Nodes";

  private static final String SELECT_LAYOUT = "Select Layout...";

  private static final String TAKE_SCREENSHOT = "Take Screenshot";

  private static final String ALL_GRAPHIC_LABEL = "All Images";

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeViewPanel.class);

  private static final String[] ALL_GRAPHIC_EXTS =
      { "*.png", "*.jpg", "*.gif", "*.bmp", "*.*" };

  private static final ExtensionFilter ALL_GRAPHICS_FILTER =
      new ExtensionFilter(ALL_GRAPHIC_LABEL, Arrays.asList(ALL_GRAPHIC_EXTS));

  private static final String PNG_EXT = "png";

  private final DepanFxWorkspace workspace;

  private final DepanFxNodeLayoutRegistry layoutRegistry;

  private final DepanFxNodeFiltersRegistry filterRegistry;

  private final DepanFxInfoRegistry panelInfoRegistry;

  private DepanFxWorkspaceResource<DepanFxNodeViewData> nodeViewRsrc;

  private DepanFxNodeViewData viewData;

  private Collection<GraphNode> viewNodes;

  private Map<GraphNode, DepanFxNodeLocationData> nodeLocations;

  private Map<GraphNode, Collection<GraphNodeInfo.Listener>> updateListeners =
      new HashMap<>();

  private JoglPane joglPane;

  private DepanFxNodeListSelection nodeSelection;

  /////////////////////////////////////
  // Node display state

  private NodeDisplayController nodeDisplay;

  /////////////////////////////////////
  // Link display state

  /**
   * If {@code true}, the current edge display settings have not be saved
   * to a resource.  Therefore, the panel state cannot be saved.
   */
  private boolean linkDisplayDirty;

  private EdgeDisplayController edgeDisplay;

  /**
   * Open supplemental windows (e.g. edit edge display).
   */
  private List<Stage> sideViews = new ArrayList<Stage>();

  // Only need to do this once.
  private DepanFxNodeList viewNodesAsNodeList;

  private DepanFxDialogRunner dialogRunner;

  public DepanFxNodeViewPanel(
      DepanFxWorkspace workspace,
      DepanFxNodeLayoutRegistry layoutRegistry,
      DepanFxNodeFiltersRegistry filterRegistry,
      DepanFxInfoRegistry infoRegistry,
      DepanFxWorkspaceResource<DepanFxNodeViewData> nodeViewRsrc) {
    this.workspace = workspace;
    this.layoutRegistry = layoutRegistry;
    this.filterRegistry = filterRegistry;
    this.nodeViewRsrc = nodeViewRsrc;
    this.panelInfoRegistry = preparePanelInfoRegistry(infoRegistry);

    // Unpack the interesting parts of the view data.
    this.viewData = nodeViewRsrc.getResource();
    this.viewNodes = viewData .getViewNodes();
    this.nodeLocations = viewData.getNodeLocations();

    // Handle node selections.
    this.nodeSelection = DepanFxNodeListSelection.forNodes(viewNodes);
    nodeSelection.setOnSelectionChange(
        (n, b) -> onSelectionChange(n, b));

    this.viewNodesAsNodeList = buildViewNodesAsNodeList();
  }

  @Override // DepanFxSceneViewer
  public Tab getSceneTab(DepanFxSceneService sceneSrvc) {
    dialogRunner = sceneSrvc.getDialogRunner();
    joglPane = createJoglPane();
    populateJoglPane();

    Tab result = new Tab(nodeViewRsrc.getResource().getToolName(), joglPane);

    result.setOnSelectionChanged(new EventHandler<Event>() {

      @Override
      public void handle(Event event) {
        if (result.isSelected()) {
          joglPane.activate();
          showSideViews();
        } else {
          joglPane.release();
          hideSideViews();
        }
      }
    });

    result.setContextMenu(buildViewContextMenu());
    return result;
  }

  @Override // DepanFxSceneViewer
  public void closeTab() {
    joglPane.close();
    closeSideViews();
  }

  public DepanFxWorkspace getWorkspace() {
    return workspace;
  }

  public DepanFxWorkspaceResource<DepanFxNodeViewData> getViewDataRsrc() {
    return nodeViewRsrc;
  }

  public DepanFxWorkspaceResource<GraphDocument> getGraphDocRsrc() {
    return viewData.getGraphDocRsrc();
  }

  public GraphDocument getGraphDoc() {
    return viewData.getGraphDocRsrc().getResource();
  }

  public String getToolName() {
    return viewData.getToolName();
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
    return nodeSelection.streamChosenNodes();
  }

  public DepanFxInfoRegistry getInfoRegistry() {
    return panelInfoRegistry;
  }

  public Optional<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
      getHierachyMatcherRsrc() {
    ContextModelId modelId = getGraphDoc().getContextModelId();
    return DepanFxLinkMatcherGroup.getMemberMatcherRsrc(workspace, modelId);
  }

  public DepanFxNodeList buildSelectedAsNodeList() {
    List<GraphNode> selectedNodes = nodeSelection.streamSelectedNodes()
        .collect(Collectors.toList());
    return DepanFxNodeLists.buildNodeList(
        getToolName() + " selection",
        "Node selected from " + getToolName() + ".",
        getGraphDocRsrc(), selectedNodes);
  }

  /////////////////////////////////////
  // for Edge Display Integration

  public void revertLinkDisplay() {
    edgeDisplay.revertLinkDisplay();
    // reverting should not change the state of dirty
  }

  public void setLinkDisplayResource(
      DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData> displayRsrc) {
    edgeDisplay.setLinkDisplayResource(displayRsrc);
    linkDisplayDirty =
        DepanFxWorkspaceResource.isSavedResource(displayRsrc, workspace);
  }

  /////////////////////////////////////
  // For NodeListTable integration

  public DepanFxNodeListSelection getNodeSelection() {
    return nodeSelection;
  }

  /**
   * Provides the full set of view nodes as a node list.
   */
  public DepanFxNodeList getNodeSelectionAsNodeList() {
    return viewNodesAsNodeList;
  }

  /**
   * Provides the full set of view nodes as a node list.
   */
  public DepanFxNodeList getViewNodesAsNodeList() {
    return viewNodesAsNodeList;
  }

  /////////////////////////////////////
  // Actions

  /**
   * If selected nodes includes all elements of the selection, leave
   * the selected node unchanged.  Otherwise, change the selected nodes
   * to the supplied selection.
   */
  public void doReviseSelectionAction(Collection<Object> selection) {
    Stream<GraphNode> choice = selection.stream()
        .filter(n -> n instanceof GraphNode)
        .map(GraphNode.class::cast);

    if (nodeSelection.streamUnselectedOf(choice).findAny().isPresent()) {
      Stream<GraphNode> process = selection.stream()
          .filter(n -> n instanceof GraphNode)
          .map(GraphNode.class::cast);
      nodeSelection.doSelectGraphNodesAction(process, true);
    }
  }

  public Map<GraphNode, DepanFxNodeLocationData> getNodeLocations(
      Stream<GraphNode> nodes) {
    return nodes
        .filter(nodeLocations::containsKey)
        .collect(Collectors.toMap(Function.identity(), nodeLocations::get));
  }

  public DepanFxNodeLocationData getNodeLocation(GraphNode node) {
    return nodeLocations.get(node);
  }

  public void addLocationListener(
      GraphNode node, GraphNodeInfo.Listener listener) {
    Collection<GraphNodeInfo.Listener> listeners =
        updateListeners.computeIfAbsent(node, n -> new ArrayList<>(2));
    listeners.add(listener);
  }

  public void removeLocationListener(
      GraphNode node, GraphNodeInfo.Listener listener) {
    Collection<GraphNodeInfo.Listener> listeners =
        updateListeners.getOrDefault(node, Collections.emptyList());
    listeners.remove(listener);
  }

  public void updateNodeLocation(
      GraphNode node, DepanFxNodeLocationData location) {
    LOG.info("Updating location of node {}", node);
    updateViewNodeLocation(node, location);
  }

  public void updateNodeLocations(
      Map<GraphNode, DepanFxNodeLocationData> locations) {
    LOG.info("Updating location of {} nodes", locations.size());
    locations.entrySet().stream()
        .forEach(e -> updateViewNodeLocation(e.getKey(), e.getValue()));
  }

  public void updateEdgeDisplayByMatcher(
      DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> matcherRsrc,
      LinkDisplayEntry displayEntry) {
    edgeDisplay.updateEdgeDisplayByMatcher(matcherRsrc, displayEntry);

    linkDisplayDirty = true;
  }

  /**
   * Take a snaphot of the given view. Ask the user a filename, and use
   * this filename to determine which type of file format has to be used.
   * PNG format is used as default.
   *
   * @param view the view to capture.
   */
  public void takeScreenshot() {
    // make the screenshot first, so that the overlapping file selection window
    // does not Interfere with the process of taking the screenshot
    // (apparently, otherwise, it does)
    BufferedImage screenshot = joglPane.takeScreenshot();

    // TODO: A real dialog with options for size, format, etc.
    // Ask the user a filename where to save the screenshot.
    String baseName = viewData.getToolName() + " snapshot";
    String screenshopName = DepanFxWorkspaceFactory
        .buildDocumentTimestampName(baseName, PNG_EXT);

    File initDst = new File(screenshopName);
    FileChooser dstDlg = DepanFxSceneControls.prepareFileChooser(initDst);
    dstDlg.getExtensionFilters().add(ALL_GRAPHICS_FILTER);
    dstDlg.setSelectedExtensionFilter(ALL_GRAPHICS_FILTER);

    File dstFile = dstDlg.showSaveDialog(joglPane.getScene().getWindow());
    if (dstFile == null) {
      LOG.info("User cancelled save of image");
      return;
    }

    // check if the file has an extension. otherwise, use .png as default
    // extension.
    String dstName = dstFile.getName();
    if (dstName.lastIndexOf('.') == -1) {
      dstName = dstName + ".png";
      dstFile = new File(dstFile.getParentFile(), dstName);
    }

    String typeSuffix = dstName.substring(dstName.lastIndexOf('.') + 1);
    try {
      // finally, write the image on a file.
      ImageIO.write(screenshot, typeSuffix, dstFile);
      LOG.info("Image saved to {}", dstName);
    } catch (IOException errIo) {
      LOG.error("Failure saving image {}", dstName, errIo);
    }
  }

  /////////////////////////////////////

  private DepanFxInfoRegistry preparePanelInfoRegistry(
      DepanFxInfoRegistry infoRegistry) {
    List<Contribution> contribs = new ArrayList<>();
    contribs.add(new NodePositionInfoContribution(this));

    return DepanFxCompositeInfoRegistry.buildInfoRegistry(
        infoRegistry, contribs );
  }

  /////////////////////////////////////
  // For NodeListTable integration

  private void runNodeSelectionDialog() {
    DepanFxWorkspaceResource<DepanFxNodeListTableViewData> tableViewRsrc =
        DepanFxProjects.getBuiltIn(
            workspace, DepanFxNodeListTableViewData.class,
            DepanFxNodeListViewBuiltIns.MEMBER_TABLE_VIEW_PATH).get();

    Stage nodeSelectDialog = DepanFxNodeViewNodeSelectDialog.runEditDialog(
        getDialogRunner(), this, tableViewRsrc);

    sideViews.add(nodeSelectDialog);
    nodeSelectDialog.setOnCloseRequest(
        e -> sideViews.remove(nodeSelectDialog));
  }

  /**
   * Provides the full set of view nodes as a node list.
   */
  private DepanFxNodeList buildViewNodesAsNodeList() {
    return DepanFxNodeLists.buildNodeList(
        getToolName() + " nodes",
        "Nodes from " + getToolName(),
        getGraphDocRsrc(),
        new ArrayList<>(viewNodes));
  }

  private void onSelectionChange(GraphNode node, boolean value) {
    LOG.info("Node {} selection {}",
        GraphContextKeys.toNodeKey(node.getId()), value);
    if (viewNodes.contains(node)) {
      JoglShapes.updateSelection(joglPane, node, value);
    }
  }

  private void runFilterSelectionDialog() {

    DepanFxWorkspaceResource<DepanFxNodeListTableViewData> tableViewRsrc =
        DepanFxProjects.getBuiltIn(
            workspace,  DepanFxNodeListTableViewData.class,
            DepanFxNodeListViewBuiltIns.FLAT_TABLE_VIEW_PATH).get();
    DepanFxNodeList filteredNodes =
        getNodeSelection().getSelection(getNodeSelectionAsNodeList());

    Stage filterSelctionDialog = DepanFxNodeViewNodeFiltersDialog.runEditDialog(
        getDialogRunner(), panelInfoRegistry, tableViewRsrc, filteredNodes,
        nl -> nodeSelection.doSelectGraphNodesAction(nl.getNodes()));

     sideViews.add(filterSelctionDialog);
     filterSelctionDialog.setOnCloseRequest(
         e -> sideViews.remove(filterSelctionDialog));
  }

  /////////////////////////////////////
  // Subwindow management

  /**
   * Release all resources for the tab.
   */

  private void showSideViews() {
    sideViews.forEach(e -> e.show());
  }

  private void hideSideViews() {
    sideViews.forEach(e -> e.hide());
  }

  private void closeSideViews() {
    sideViews.forEach(e -> e.close());
  }

  private Menu buildEdgeDisplayMenu() {
    Menu result = new Menu(EDGE_DISPLAY);

    ObservableList<MenuItem> items = result.getItems();
    items.add(DepanFxContextMenuBuilder.createActionItem(
        SELECT_EDGE_DISPLAY, e -> doSelectEdgeDisplayAction()));

    items.add(new SeparatorMenuItem());
    items.add(DepanFxContextMenuBuilder.createActionItem(
        DepanFxNodeViewLinkDisplayDialog.EDIT_LINK_DISPLAY_ITEM,
        e -> runEditLinkDisplayDialog()));
    return result;
  }

  private void doSelectEdgeDisplayAction() {
    DepanFxLinkDisplayDataChooser
        .runLinkDisplayFinder(
            workspace, getDialogRunner(), joglPane.getScene())
        .ifPresent(this::setLinkDisplayResource);
  }

  private void runEditLinkDisplayDialog() {
    Stage edgeDisplayDialog =
        DepanFxNodeViewLinkDisplayDialog.runEditDialog(
            edgeDisplay, getDialogRunner());

    sideViews.add(edgeDisplayDialog);
    edgeDisplayDialog.setOnCloseRequest(
        e -> sideViews.remove(edgeDisplayDialog));
  }

  private Menu buildNodeDisplayMenu() {
    Menu result = new Menu(NODE_DISPLAY);

    ObservableList<MenuItem> items = result.getItems();
    items.add(DepanFxContextMenuBuilder.createActionItem(
        SELECT_NODE_DISPLAY, e -> doSelectNodeDisplayAction()));

    items.add(new SeparatorMenuItem());
    items.add(DepanFxContextMenuBuilder.createActionItem(
        DepanFxNodeViewNodeDisplayDialog.EDIT_NODE_DISPLAY_ITEM,
        e -> runEditNodeDisplayDialog()));

    return result;
  }

  private void doSelectNodeDisplayAction() {
    DepanFxNodeDisplayDataChooser
        .runNodeDisplayFinder(
            workspace, getDialogRunner(), joglPane.getScene())
        .ifPresent(nodeDisplay::setNodeDisplayResource);
  }

  private void runEditNodeDisplayDialog() {
    Stage nodeDisplayDialog =
        DepanFxNodeViewNodeDisplayDialog.runEditDialog(
            nodeDisplay, getDialogRunner());

    sideViews.add(nodeDisplayDialog);
    nodeDisplayDialog.setOnCloseRequest(
        e -> sideViews.remove(nodeDisplayDialog));
  }

  public void doSelectAllAction() {
    nodeSelection.doSelectAllAction();
  }

  public void doClearSelectionAction() {
    nodeSelection.doClearSelectionAction();
  }

  public void doInvertSelectionAction() {
    nodeSelection.doInvertSelectionAction();
  }

  /////////////////////////////////////
  // Menus and UX

  private ContextMenu buildViewContextMenu() {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem(
        DepanFxNodeListTableCommands.SELECT_ALL_ITEM,
        e -> doSelectAllAction());
    builder.appendActionItem(
        DepanFxNodeListTableCommands.CLEAR_SELECTION_ITEM,
        e -> doClearSelectionAction());
    builder.appendActionItem(
        DepanFxNodeListTableCommands.INVERT_SELECTION_ITEM,
        e -> doInvertSelectionAction());
    builder.appendActionItem(
        NODE_SELECTION_ITEM,
        e -> runNodeSelectionDialog());
    builder.appendActionItem(
        FILTER_SELECTION_ITEM,
        e -> runFilterSelectionDialog());

    builder.appendSeparator();
    builder.appendSubMenu(buildEdgeDisplayMenu());
    Menu edgeVizMenu = new Menu(EDGE_VISIBLITY);
    builder.appendSubMenu(edgeVizMenu);

    builder.appendSeparator();
    builder.appendSubMenu(buildNodeDisplayMenu());
    Menu nodeVizMenu = new Menu(NODE_VISIBLITY);
    builder.appendSubMenu(nodeVizMenu);

    builder.appendSeparator();
    builder.appendSubMenu(buildLayoutNodesMenu());

    builder.appendSeparator();
    builder.appendActionItem(TAKE_SCREENSHOT, e -> takeScreenshot());

    builder.appendSeparator();
    builder.appendActionItem(
        DepanFxSaveNodeListDialog.SAVE_NODE_LIST,
        e -> runSaveNodeListDialog());
    builder.appendActionItem(
        SAVE_NODE_VIEW_ITEM, e -> runSaveNodeViewDialog());

    ContextMenu result = builder.build();
    result.setOnShowing(e -> {
      populateEdgeVisibilityMenu(edgeVizMenu);
      populateNodeVisibilityMenu(nodeVizMenu);
    });
    return result;
  }

  /////////////////////////////////////
  // Edge Visibility Menu

  private void populateEdgeVisibilityMenu(Menu vizMenu) {
    ObservableList<MenuItem> items = vizMenu.getItems();
    items.clear();

    // Toggles for each (non-zero) matcher
    edgeDisplay.streamAvailableMatchers()
        .filter(d -> edgeDisplay.getVisiblityMatcherEdgeCount(d) > 0)
        .forEach(m -> items.add(buildEdgeVisibleItem(m)));

    // Add one for the remainders
    items.add(buildEgdeVisibleItem(
        edgeDisplay.getRemainderLabel(),
        edgeDisplay.getRemainderVisibility(),
        edgeDisplay.getRemainderCount(),
        e -> doToggleRemainderVisibleAction()));

    items.add(new SeparatorMenuItem());
    items.add(DepanFxContextMenuBuilder.createActionItem(
        ALL_EDGES_VISIBLE, e -> doAllEdgesVisibleAction()));
    items.add(DepanFxContextMenuBuilder.createActionItem(
        NO_EDGES_VISIBLE, e -> doNoEdgesVisibleAction()));
    items.add(DepanFxContextMenuBuilder.createActionItem(
        INVERT_EDGES_VISIBLE, e -> doInvertEdgesVisibleAction()));

    items.add(new SeparatorMenuItem());
    items.add(DepanFxContextMenuBuilder.createActionItem(
        MORE_EDGE_VIBILITY, e -> runEditVisibleEdgesDialog()));
  }

  private void doAllEdgesVisibleAction() {
    edgeDisplay.forEachAvailableMatchers(
        m -> edgeDisplay.setMatcherVisibility(m, true));
    edgeDisplay.setRemainderVisibility(true);
  }

  private void doNoEdgesVisibleAction() {
    edgeDisplay.forEachAvailableMatchers(
        m -> edgeDisplay.setMatcherVisibility(m, false));
    edgeDisplay.setRemainderVisibility(false);
  }

  private void doInvertEdgesVisibleAction() {
    edgeDisplay.forEachAvailableMatchers(
        m -> {
          boolean isVisible = edgeDisplay.getMatcherVisibility(m);
          edgeDisplay.setMatcherVisibility(m, !isVisible);
        });
    doToggleRemainderVisibleAction();
  }

  private MenuItem buildEdgeVisibleItem(
      DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> matcherRsrc) {
    String label = matcherRsrc.getResource().getToolName();
    boolean isVisible = edgeDisplay.getMatcherVisibility(matcherRsrc);
    int edgeCount = edgeDisplay.getVisiblityMatcherEdgeCount(matcherRsrc);
    return buildEgdeVisibleItem(label, isVisible, edgeCount,
        e -> setMatcherVisible(matcherRsrc, !isVisible));
  }

  private MenuItem buildEgdeVisibleItem(
      String label, boolean isVisible, int edgeCount,
      EventHandler<ActionEvent> handler) {
    String itemLabel = String.format("%s (%,d)", label, edgeCount);
    CheckMenuItem result = new CheckMenuItem(itemLabel);
    result.setSelected(isVisible);
    result.setOnAction(handler);
    return result;
  }

  private void doToggleRemainderVisibleAction() {
    boolean isVisible = edgeDisplay.getRemainderVisible();
    edgeDisplay.setRemainderVisibility(!isVisible);
  }

  private void setMatcherVisible(
      DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> matcherRsrc,
      boolean isVisible) {
    edgeDisplay.setMatcherVisibility(matcherRsrc, isVisible);
  }

  private void runEditVisibleEdgesDialog() {

    DepanFxNodeViewEdgeVisibilityDialog.runVisibilityDialog(
        getDialogRunner(), edgeDisplay)
        .getController()
        .getToolResource()
        .ifPresent(edgeDisplay::setVisibiltyResource);
  }

  /////////////////////////////////////
  // Node Visibility Menu

  private void populateNodeVisibilityMenu(Menu vizMenu) {
    ObservableList<MenuItem> items = vizMenu.getItems();
    items.clear();

    // Toggles for each (non-zero) matcher
    nodeDisplay.streamAvailableResources()
        .filter(d -> nodeDisplay.getVisiblityFilterNodeCount(d) > 0)
        .forEach(r -> items.add(buildNodeVisibleItem(r)));

    // Add one for the remainders
    items.add(buildEgdeVisibleItem(
        edgeDisplay.getRemainderLabel(),
        edgeDisplay.getRemainderVisibility(),
        edgeDisplay.getRemainderCount(),
        e -> doToggleRemainderVisibleAction()));

    items.add(new SeparatorMenuItem());
    items.add(DepanFxContextMenuBuilder.createActionItem(
        ALL_NODES_VISIBLE, e -> doAllNodesVisibleAction()));
    items.add(DepanFxContextMenuBuilder.createActionItem(
        NO_NODES_VISIBLE, e -> doNoNodeVisibleAction()));
    items.add(DepanFxContextMenuBuilder.createActionItem(
        INVERT_NODES_VISIBLE, e -> doInvertNodesVisibleAction()));

    items.add(new SeparatorMenuItem());
    items.add(DepanFxContextMenuBuilder.createActionItem(
        MORE_NODE_VIBILITY, e -> runEditVisibleNodesDialog()));
  }

  private void doAllNodesVisibleAction() {
    nodeDisplay.forEachAvailablityFilter(
        f -> nodeDisplay.setFilterVisibility(f, true));
    nodeDisplay.setRemainderVisibility(true);
  }

  private void doNoNodeVisibleAction() {
    nodeDisplay.forEachAvailablityFilter(
        f -> nodeDisplay.setFilterVisibility(f, false));
    nodeDisplay.setRemainderVisibility(false);
  }

  private void doInvertNodesVisibleAction() {
    nodeDisplay.forEachAvailablityFilter(f -> {
          boolean isVisible = nodeDisplay.getFilterVisibility(f);
          nodeDisplay.setFilterVisibility(f, !isVisible);
        });
    doToggleRemainderVisibleAction();
  }

  private void runEditVisibleNodesDialog() {
    DepanFxNodeViewNodeVisibilityDialog.runVisibilityDialog(
        getDialogRunner(), nodeDisplay)
        .getController()
        .getToolResource()
        .ifPresent(nodeDisplay::setVisiblityResource);
  }

  private MenuItem buildNodeVisibleItem(
      DepanFxWorkspaceResource<DepanFxBaseFilterData> filterRsrc) {
    DepanFxBaseFilterData filterInfo = filterRsrc.getResource();
    String label = filterInfo.getToolName();
    boolean isVisible = nodeDisplay.getFilterVisibility(filterRsrc);
    int nodeCount = nodeDisplay.getVisiblityFilterNodeCount(filterRsrc);
    return buildEgdeVisibleItem(label, isVisible, nodeCount,
        e -> setFilterVisible(filterRsrc, !isVisible));
  }

  private void setFilterVisible(
      DepanFxWorkspaceResource<DepanFxBaseFilterData> filterRsrc,
      boolean isVisible) {
    nodeDisplay.setFilterVisibility(filterRsrc, isVisible);
  }

  /////////////////////////////////////
  // Layouts Menu

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
    DepanFxLayoutsChooser
        .runLayoutFinder(
            workspace, getDialogRunner(), joglPane.getScene(), layoutRegistry)
        .ifPresent(this::layoutNodes);
  }

  private void layoutNodes(DepanFxWorkspaceResource<?> layoutRsrc) {
    List<GraphNode> updateNodes =
        streamChosenNodes().collect(Collectors.toList());
    updateNodeLocations(
        layoutRegistry.layoutNodes(this, layoutRsrc, updateNodes));
  }

  private void runSaveNodeListDialog() {
    DepanFxSaveNodeListDialog.runSaveNodeList(
        getDialogRunner(),
        workspace.addScratchResource(buildSelectedAsNodeList()))
        .map(r -> r.getResource().getNodes())
        .ifPresent(nodeSelection::doSelectGraphNodesAction);
  }

  private void runSaveNodeViewDialog() {
    DepanFxProctor proctor = new DepanFxProctor.Simple();
    checkInput(proctor);
    if (DepanFxResourcePerspectives.errorAlert(
        proctor, "Unable to save node view settings")) {
      return;
    }
    DepanFxNodeViewData saveView = buildSaveView();
    DepanFxWorkspaceResource<DepanFxNodeViewData> updateRsrc =
        DepanFxWorkspaceResource.forUpdate(nodeViewRsrc, saveView);

    DepanFxResourcePerspectives.runEditDialog(
        updateRsrc, getDialogRunner(),
        DepanFxSaveNodeViewDialog.class, "Save node view")
        .getController()
        .getToolResource()
        .ifPresent(this::updateSavedResource);
  }

  private void updateSavedResource(
      DepanFxWorkspaceResource<DepanFxNodeViewData> viewDataRsrc) {
    // The underlying data (e.g. node lists and locations) stays the same
    // but the access paths change if a new resource was saved.
    this.nodeViewRsrc = viewDataRsrc;
    this.viewData = viewDataRsrc.getResource();
  }

  /////////////////////////////////////
  // Render

  private void checkInput(DepanFxProctor proctor) {
    if (linkDisplayDirty) {
      proctor.addError("Unsaved edge display",
          "The edge display property settings have been applied"
          + " but they have not been saved to a resource file."
          + "  Use the Edge Display Edit window"
          + " to save the current settings.");
    }
  }

  private DepanFxNodeViewData buildSaveView() {
    DepanFxNodeViewData result = new DepanFxNodeViewData(
        viewData.getToolName(), viewData.getToolDescription(),
        viewData.getGraphDocRsrc(), viewNodes, nodeLocations,
        viewData.getNodeDisplay(), viewData.getEdgeDisplay(),

        buildSceneData(),

        nodeDisplay.forUpdateAvailableFilterResource(),
        nodeDisplay.forUpdateVisibleFilterResource(),
        nodeDisplay.getNodeDisplayResource(),
        nodeDisplay.getRemainderVisibility(),
        nodeDisplay.getRemainderDisplay(),

        edgeDisplay.forUpdateAvailableMatcherSequenceDoc(),
        edgeDisplay.forUpdateVisibleMatcherSequenceDoc(),
        edgeDisplay.getLinkDisplayResource(),
        edgeDisplay.getRemainderVisible(),
        edgeDisplay.getRemainderLabel(),
        edgeDisplay.getRemainderDisplay());
    return result;
  }

  private DepanFxNodeViewSceneData buildSceneData() {
    DepanFxNodeViewCameraData cameraInfo =
        joglPane.getCameraData();
    DepanFxNodeViewSceneData result = new DepanFxNodeViewSceneData(
        viewData.getSceneData().getBackgroundColor(), cameraInfo);
    return result;
  }

  private JoglPane createJoglPane() {
    DepanFxNodeViewCameraData cameraInfo =
        viewData.getSceneData().getCameraInfo();
    JoglPane result = JoglPane.createJoglPane(cameraInfo, getDialogRunner());
    result.addMouseActionListener(new ViewMouseActionListener());
    return result;
  }

  private void populateJoglPane() {
    GraphModel model = getGraphDoc().getGraph();
    nodeDisplay = NodeDisplayController.of(
        joglPane, viewData,
        filterRegistry.buildFilterFactory(model, model.getGraphNodes()));
    nodeDisplay.setNodeDisplayResource(viewData.getNodeDisplayDocRsrc());

    viewNodes.forEach(this::installShape);

    edgeDisplay = EdgeDisplayController.of(joglPane, viewData);

    linkDisplayDirty = false;

    getViewEdges().forEach(edgeDisplay::installEdge);
  }

  private void updateViewNodeLocation(
      GraphNode node, DepanFxNodeLocationData location) {
    if (viewNodes.contains(node)) {
      JoglShapes.updateLocation(joglPane, node, location);
      nodeLocations.put(node, location);
      fireLocationUpdateEvent(node, location);
    }
  }

  private void fireLocationUpdateEvent(
      GraphNode node, DepanFxNodeLocationData location) {
    Collection<GraphNodeInfo.Listener> listeners =
        updateListeners.getOrDefault(node, Collections.emptyList());
    listeners.forEach(l -> l.updateInfo(node, location));
  }

  private void installShape(GraphNode node) {

    DepanFxNodeLocationData location = nodeLocations.get(node);
    if (location == null) {
      return;
    }
    nodeDisplay.installNode(node, location);
  }


  /**
   * Provide edges that have both the head and tail
   * in the set of {@code #viewNodes}.
   */
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

  private class ViewMouseActionListener implements JoglMouseActionListener {

    @Override
    public void mouseDolly(double deltaX, double deltaY, double deltaZ) {
      DepanFxNodeViewCameraData cameraInfo = joglPane.getCameraData();
      double viewScale = cameraInfo.zoom * cameraInfo.cameraZ * 2;
      LOG.debug("view scale {}", viewScale);
      joglPane.dolly(viewScale * deltaX, viewScale * deltaY, deltaZ);
    }

    @Override
    public void moveSelection(double deltaX, double deltaY, double deltaZ) {
      long selectCount = streamChosenNodes().count();
      LOG.info("Selection move {} nodes: x:{}, y:{}, z:{}",
          selectCount, deltaX, deltaY, deltaZ);
      DepanFxNodeViewCameraData cameraInfo = joglPane.getCameraData();
      double viewScale = cameraInfo.zoom * cameraInfo.cameraZ * 2;
      Map<GraphNode, DepanFxNodeLocationData> moveLocation = new HashMap<>();
      streamChosenNodes()
          .filter(nodeLocations::containsKey)
          .forEach(n -> {
            DepanFxNodeLocationData shiftLoc =
                DepanFxNodeLocationData.shift(nodeLocations.get(n),
                    deltaX * viewScale, deltaY * viewScale, deltaZ);
            moveLocation.put(n, shiftLoc);
          });
      updateNodeLocations(moveLocation);
    }

    @Override
    public void rotateCamera(double f, double g, double h) {
    }

    @Override
    public void setSelection(List<Object> selection) {
      // Used for lookups, so a set is appropriate here.
      Collection<GraphNode> nodes = new HashSet<>(selection.size());
      streamNodes(selection).forEach(nodes::add);
      nodeSelection.doSelectGraphNodesAction(nodes);
    }

    @Override
    public void reduceSelection(List<Object> reduction) {
      nodeSelection.doSelectGraphNodesAction(streamNodes(reduction), false);
    }

    @Override
    public void extendSelection(List<Object> extension) {
      nodeSelection.doSelectGraphNodesAction(streamNodes(extension), true);
    }

    @Override
    public void reviseSelection(List<Object> selection) {
      doReviseSelectionAction(selection);
    }

    private Stream<GraphNode> streamNodes(List<?> source) {
      return source.stream()
          .filter(o -> o instanceof GraphNode)
          .map(GraphNode.class::cast);
    }
  }
}
