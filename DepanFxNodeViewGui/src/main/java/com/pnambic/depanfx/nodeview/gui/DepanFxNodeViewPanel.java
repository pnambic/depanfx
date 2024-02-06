package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

  private static final String SAVE_NODE_LIST_ITEM = "Save as node list...";

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeViewPanel.class);

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private DepanFxNodeList nodeList;

  private Map<GraphNode, BooleanProperty> nodesCheckBoxStates;

  public DepanFxNodeViewPanel(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxNodeList nodeList) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
    this.nodeList = nodeList;

    nodesCheckBoxStates = buildNodesCheckBoxStates(nodeList.getNodes());
  }

  public Tab createWorkspaceTab(String tabTitle) {
    DepanFxJoglView joglView = DepanFxJoglView.createJoglView();
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
    return (GraphDocument) nodeList.getGraphDocResource().getResource();
  }

  public ContextModelId getContextModelId() {
    return getGraphDoc()
        .getContextModelId();
  }

  public <T> Dialog<T> buildDialog(Class<T> controllerType) {
    return dialogRunner.createDialogAndParent(controllerType);
  }

  public DepanFxDialogRunner getDialogRunner() {
    return dialogRunner;
  }

  public void doSelectAllAction() {
    doSelectGraphNodesAction(nodeList.getNodes(), true);
  }

  public void doClearSelectionAction() {
    doSelectGraphNodesAction(nodeList.getNodes(), false);
  }

  public void doInvertSelectionAction() {
    nodeList.getNodes().stream()
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

  private void runSaveNodeListDialog() {
    // STUB
  }

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
        SAVE_NODE_LIST_ITEM, e -> runSaveNodeListDialog());
    return builder.build();
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
