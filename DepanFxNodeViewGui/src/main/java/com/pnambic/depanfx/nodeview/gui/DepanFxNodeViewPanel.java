package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.context.GraphContextKeys;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.jogl.JoglMouseActionListener;
import com.pnambic.depanfx.nodefilters.gui.DepanFxNodeViewNodeFiltersDialog;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListConfiguration;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListSelection;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableCommands;
import com.pnambic.depanfx.nodelist.gui.DepanFxSaveNodeListDialog;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherGroup;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxLinkMatcherSequenceDocument;
import com.pnambic.depanfx.nodeview.jogl.JoglPane;
import com.pnambic.depanfx.nodeview.jogl.JoglShapes;
import com.pnambic.depanfx.nodeview.layouts.DepanFxLayoutsChooser;
import com.pnambic.depanfx.nodeview.layouts.DepanFxNodeLayoutRegistry;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeDisplayData;
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
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

public class DepanFxNodeViewPanel {

  private static final String NODE_SELECTION_ITEM = "Node Selection...";

  private static final String FILTER_SELECTION_ITEM = "Filter Selection...";

  private static final String SAVE_NODE_VIEW_ITEM = "Save node view...";

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

  private static final String EDGE_DISPLAY = "Edge Display";

  private static final String SELECT_EDGE_DISPLAY = "Select Edge Display...";

  private static final String EDGE_VISIBLITY = "Edge Visibility";

  private static final String ALL_EDGES_VISIBLE = "Show All Edges";

  private static final String NO_EDGES_VISIBLE = "Hide All Edges";

  private static final String INVERT_EDGES_VISIBLE = "Invert Visible";

  private static final String MORE_EDGE_VIBILITY = "More Visibility...";

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxNodeLayoutRegistry layoutRegistry;

  private final DepanFxNodeViewData viewData;

  private Collection<GraphNode> viewNodes;

  private DepanFxNodeListSelection nodeSelection;

  private Map<GraphNode, DepanFxNodeLocationData> nodeLocations;

  private Map<GraphNode, DepanFxNodeDisplayData> nodeDisplay;

  private JoglPane joglPane;

  /////////////////////////////////////
  // Link display state

  /**
   * If {@code true}, the current edge display settings have not be saved
   * to a resource.  Therefore, the panel state cannot be saved.
   */
  private boolean linkDisplayDirty;

  private DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData>
      linkDisplayRsrc;

  private EdgeDisplayController edgeDisplay;

  /**
   * Open supplemental windows (e.g. edit edge display).
   */
  private List<Stage> sideViews = new ArrayList<Stage>();

  // Only need to do this once.
  private DepanFxNodeList viewNodesAsNodeList;

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

    // Handle node selections.
    this.nodeSelection = DepanFxNodeListSelection.forNodes(viewNodes);
    nodeSelection.setOnSelectionChange(
        (n, b) -> onSelectionChange(n, b));

    this.viewNodesAsNodeList = buildViewNodesAsNodeList();
  }

  public Tab createWorkspaceTab(String tabTitle) {
    joglPane = createJoglPane();
    populateJoglPane();

    Tab result = new Tab(tabTitle, joglPane);

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

    result.setOnClosed(e -> closeTab());
    result.setOnCloseRequest(e -> closeTab());
    return result;
  }

  public DepanFxWorkspace getWorkspace() {
    return workspace;
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

  public Optional<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
      getHierachyMatcherRsrc() {
    ContextModelId modelId = getGraphDoc().getContextModelId();
    return DepanFxLinkMatcherGroup.getMemberMatcherRsrc(workspace, modelId);
  }

  public void revertLinkDisplay() {
    edgeDisplay.revertLinkDisplay();
    // reverting should not change the state of dirty
  }

  public void setLinkDisplayInfo(DepanFxNodeViewLinkDisplayData displayInfo) {
    edgeDisplay.setLinkDisplayInfo(displayInfo);
    linkDisplayDirty = true;
  }

  public void setLinkDisplayResource(
      DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData> displayRsrc) {
    edgeDisplay.setLinkDisplayInfo(displayRsrc.getResource());
    linkDisplayRsrc = displayRsrc;
    linkDisplayDirty = false;
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

  public void updateNodeLocations(
      Map<GraphNode, DepanFxNodeLocationData> locations) {
    LOG.info("Updating location of {} nodes", locations.size());
    locations.entrySet().stream()
        .forEach(e -> updateNodeLocation(e.getKey(), e.getValue()));
  }

  public void updateEdgeDisplayByMatcher(
      DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> matcher,
      LinkDisplayEntry displayEntry) {
    edgeDisplay.updateEdgeDisplayByMatcher(
        matcher.getResource(), displayEntry);

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
  // For NodeListTable integration

  private void runNodeSelectionDialog() {
    String selectName =
        viewData.getToolName() + " nodes." + DepanFxNodeList.NODE_LIST_EXT;

    DepanFxWorkspaceResource<DepanFxNodeListTableViewData> tableViewRsrc =
        DepanFxProjects.getBuiltIn(
            workspace, DepanFxNodeListTableViewData.class,
            DepanFxNodeListConfiguration.MEMBER_TABLE_VIEW_PATH).get();

    Path selectPath = DepanFxProjects.getCurrentAnalysesPath(workspace)
        .map(p -> p.resolve(selectName))
        .get();
     Stage nodeSelectDialog = workspace.getCurrentProject()
        .flatMap(p -> p.asProjectDocument(selectPath))
        .map(d -> DepanFxNodeViewNodeSelectDialog.runEditDialog(
            dialogRunner, this, tableViewRsrc.getResource(), d))
        .get();

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
            DepanFxNodeListConfiguration.FLAT_TABLE_VIEW_PATH).get();
    DepanFxNodeList filteredNodes =
        getNodeSelection().getSelection(getNodeSelectionAsNodeList());

    Stage filterSelctionDialog = DepanFxNodeViewNodeFiltersDialog.runEditDialog(
        dialogRunner, null, tableViewRsrc.getResource(), filteredNodes,
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
  private void closeTab() {
    joglPane.close();
    closeSideViews();
  }

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
        DepanFxNodeViewLinkDisplayDialog.EDIT_LINK_DISPLAY,
        e -> runEditLinkDisplayDialog()));
    return result;
  }

  private void doSelectEdgeDisplayAction() {
    DepanFxLinkDisplayDataChooser
        .runLinkDisplayFinder(
            workspace, dialogRunner, joglPane.getScene())
        .ifPresent(this::setLinkDisplayResource);
  }

  private void runEditLinkDisplayDialog() {
    Stage edgeDisplayDialog =
        DepanFxNodeViewLinkDisplayDialog.runEditDialog(
            this, linkDisplayRsrc.getDocument(),
            edgeDisplay.getLinkDisplayInfo(), dialogRunner);

    sideViews.add(edgeDisplayDialog);
    edgeDisplayDialog.setOnCloseRequest(
        e -> sideViews.remove(edgeDisplayDialog));
  }

  /////////////////////////////////////
  // Menus and UX

  private ContextMenu buildViewContextMenu() {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem(
        DepanFxNodeListTableCommands.SELECT_ALL_ITEM,
        e -> nodeSelection.doSelectAllAction());
    builder.appendActionItem(
        DepanFxNodeListTableCommands.CLEAR_SELECTION_ITEM,
        e -> nodeSelection.doClearSelectionAction());
    builder.appendActionItem(
        DepanFxNodeListTableCommands.INVERT_SELECTION_ITEM,
        e -> nodeSelection.doInvertSelectionAction());
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
    result.setOnShowing(e -> populateEdgeVisibilityMenu(edgeVizMenu));
    return result;
  }

  /////////////////////////////////////
  // Edge Visibility Menu

  private void populateEdgeVisibilityMenu(Menu vizMenu) {
    ObservableList<MenuItem> items = vizMenu.getItems();
    items.clear();

    // Toggles for each (non-zero) matcher
    edgeDisplay.streamVisibilityMatchers()
        .filter(d -> edgeDisplay.getVisiblityMatcherEdgeCount(d) > 0)
        .sorted((a, b) -> a.getToolName().compareTo(b.getToolName()))
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
    edgeDisplay.streamVisibilityMatchers()
        .forEach(m -> edgeDisplay.setMatcherVisibility(m, true));
    edgeDisplay.setRemainderVisibility(true);
  }

  private void doNoEdgesVisibleAction() {
    edgeDisplay.streamVisibilityMatchers()
        .forEach(m -> edgeDisplay.setMatcherVisibility(m, false));
    edgeDisplay.setRemainderVisibility(false);
  }

  private void doInvertEdgesVisibleAction() {
    edgeDisplay.streamVisibilityMatchers()
        .forEach(m -> {
          boolean isVisible = edgeDisplay.getMatcherVisibility(m);
          edgeDisplay.setMatcherVisibility(m, !isVisible);
        });
    doToggleRemainderVisibleAction();
  }

  private MenuItem buildEdgeVisibleItem(DepanFxLinkMatcherDocument matcher) {
    String label = matcher.getToolName();
    boolean isVisible = edgeDisplay.getMatcherVisibility(matcher);
    int edgeCount = edgeDisplay.getVisiblityMatcherEdgeCount(matcher);
    return buildEgdeVisibleItem(label, isVisible, edgeCount,
        e -> setMatcherVisible(matcher, !isVisible));
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
      DepanFxLinkMatcherDocument matcher, boolean isVisible) {
    edgeDisplay.setMatcherVisibility(matcher, isVisible);
  }

  private void runEditVisibleEdgesDialog() {

    Dialog<DepanFxNodeViewEdgeVisibilityDialog> visibilty =
        DepanFxNodeViewEdgeVisibilityDialog.runVisibilityDialog(
            dialogRunner, viewData.getAvailableEdgesDoc(),
            viewData.getVisibleEdgesDoc(), edgeDisplay,
            r -> updateAvailableEdges(r));
    visibilty.getController().getWorkspaceResource()
        .ifPresent(this::updateVisibleEdges);
  }

  private void updateAvailableEdges(
      DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> availableMatchersRsrc) {
    viewData.setAvailableEdgeRsrc(availableMatchersRsrc);
    edgeDisplay.setMatcherVisibility(null, linkDisplayDirty);
  }

  private void updateVisibleEdges(
      DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> visibleMatchersRsrc) {
    viewData.setVisibleEdgeRsrc(visibleMatchersRsrc);

    edgeDisplay.clearMatcherVisibility();
    visibleMatchersRsrc.getResource().streamMatchers()
        .map(r -> r.getResource())
        .forEach(m -> edgeDisplay.setMatcherVisibility(m, true));
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
            workspace, dialogRunner, joglPane.getScene(), layoutRegistry)
        .ifPresent(this::layoutNodes);
  }

  private void layoutNodes(DepanFxWorkspaceResource<?> layoutRsrc) {
    List<GraphNode> updateNodes =
        streamChosenNodes().collect(Collectors.toList());
    updateNodeLocations(
        layoutRegistry.layoutNodes(layoutRsrc, getGraphDocRsrc(), updateNodes));
  }

  private void runSaveNodeListDialog() {
    DepanFxSaveNodeListDialog.runSaveNodeList(
            dialogRunner, buildSelectedAsNodeList())
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

    DepanFxResourcePerspectives.runCreateDialog(
        saveView, dialogRunner,
        DepanFxSaveNodeViewDialog.class, "Save node view");
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
        buildSceneData(),
        viewData.getGraphDocRsrc(), viewData.getAvailableEdgeRsrc(),
        viewData.getVisibleEdgeRsrc(), linkDisplayRsrc,
        viewNodes, nodeLocations, nodeDisplay,
        edgeDisplay.getEdgeDisplay(),
        edgeDisplay.getRemainderVisible(), edgeDisplay.getRemainderLabel(),
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
    JoglPane result = JoglPane.createJoglPane(cameraInfo, dialogRunner);
    result.addMouseActionListener(new ViewMouseActionListener());
    return result;
  }

  private void populateJoglPane() {

    viewNodes.forEach(this::installShape);

    edgeDisplay = EdgeDisplayController.of(joglPane, viewData);

    linkDisplayRsrc = viewData.getLinkDisplayDocRsrc();
    linkDisplayDirty = false;

    getViewEdges().forEach(edgeDisplay::installEdge);
  }

  private void updateNodeLocation(
      GraphNode node, DepanFxNodeLocationData location) {
    if (viewNodes.contains(node)) {
      JoglShapes.updateLocation(joglPane, node, location);
      nodeLocations.put(node, location);
    }
  }

  private void installShape(GraphNode node) {

    DepanFxNodeLocationData location = nodeLocations.get(node);
    if (location == null) {
      return;
    }
    DepanFxNodeDisplayData displayInfo = nodeDisplay.get(node);
    if (displayInfo == null) {
      return;
    }
    JoglShapes.installShape(joglPane, node, location, displayInfo);
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
