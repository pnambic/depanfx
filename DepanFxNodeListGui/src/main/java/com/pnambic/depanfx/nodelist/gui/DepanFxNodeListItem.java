package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxSceneControls;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.TreeItem;

public abstract class DepanFxNodeListItem
    extends CheckBoxTreeItem<DepanFxNodeListMember> {

  public static final String COPY_ITEM = "Copy";

  public static final String COPY_AS_ITEM = "Copy as";

  private static final String COPY_DISPLAY_ITEM = "Display";

  private static final String COPY_NODE_KEY = "Node Key";

  private static final String COPY_SIMPLE_NAME = "Short Name";

  public DepanFxNodeListItem(DepanFxNodeListMember member) {
    super(member);
  }

  /**
   * Provide the context menu for a single selected graph node.
   *
   * Note that node may be null if the item is not a GraphNode or no items are
   * selected.
   */
  abstract public void fillNodeContextMenu(
      ContextMenu contextMenu,
      Scene scene,
      DepanFxNodeListTableAdapter tableAdapter,
      GraphNode node);

  /**
   * Provide the context menu when multiple graph nodes are selected.
   */
  public void fillMultiContextMenu(
      ContextMenu contextMenu,
      Scene scene,
      DepanFxNodeListTableAdapter tableAdapter,
      ObservableList<TreeItem<DepanFxNodeListMember>> choices) {

    // Pick one.
     GraphNode node = DepanFxNodeListGraphNode.streamMultiNodes(choices)
        .findFirst()
        .orElse(null);
    fillNodeContextMenu(
        contextMenu, scene, tableAdapter, node);
  };

  protected void appendCopyActionItems(DepanFxContextMenuBuilder builder) {
    DepanFxNodeListMember member = getValue();
    builder.appendActionItem(
        COPY_ITEM, e -> runCopyFrom(member.getDisplayName()));

    if (member instanceof DepanFxNodeListGraphNode graphNode) {
      builder.appendSubMenu(buildCopyMenu(graphNode));
    }
  }

  protected void runCopyFrom(String src) {
    DepanFxSceneControls.setSystemClipboard(src);
  }

  protected Menu buildCopyMenu(DepanFxNodeListGraphNode node) {
    DepanFxMenuBuilder result = new DepanFxMenuBuilder(COPY_AS_ITEM);
    result.appendActionItem(
        COPY_DISPLAY_ITEM,
        e -> runCopyFrom(node.getDisplayName()));
    result.appendActionItem(
        COPY_NODE_KEY,
        e -> runCopyFrom(node.getGraphNode().getId().getNodeKey()));
    result.appendActionItem(
        COPY_SIMPLE_NAME,
        e -> runCopyFrom(node.getGraphNode().getId().getSimpleName()));
    return result.build();
  }
}
