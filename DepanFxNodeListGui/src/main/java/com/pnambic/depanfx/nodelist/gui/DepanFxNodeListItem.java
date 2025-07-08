package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxSceneControls;

import javafx.scene.Scene;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;

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
  abstract public ContextMenu getNodeContextMenu(
      Scene scene,
      DepanFxNodeListTableAdapter tableAdapter,
      GraphNode node);

  /**
   * Provide the context menu when multiple graph nodes are selected.
   *
   * Default behavior is to use the single item context menu.
   * @return
   */
  public ContextMenu getMultiContextMenu(
    Scene scene,
    DepanFxNodeListTableAdapter tableAdapter,
    DepanFxNodeList itemList) {
    // Pick one.
    GraphNode node = itemList.getNodes().stream().findFirst().orElse(null);
    return getNodeContextMenu(
        scene, tableAdapter, node);
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
