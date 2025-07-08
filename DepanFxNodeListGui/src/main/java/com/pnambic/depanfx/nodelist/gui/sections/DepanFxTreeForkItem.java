package com.pnambic.depanfx.nodelist.gui.sections;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListGraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.gui.sections.folds.DepanFxFoldSection;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxMenuItemFactory;

import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class DepanFxTreeForkItem extends DepanFxNodeListForkItem {

  public static final String FOLD_TREE_INTO = "Fold Tree Into";

  public DepanFxTreeForkItem(DepanFxTreeFork fork) {
    super(fork);
  }

  @Override
  public ContextMenu getNodeContextMenu(Scene scene,
      DepanFxNodeListTableAdapter tableAdapter, GraphNode node) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();

    appendRecursiveActionItems(builder, tableAdapter);

    builder.appendSeparator();
    appendCopyActionItems(builder);

    builder.appendSeparator();
    appendExpandTreeActionItems(builder);

    // Conditional, with separator if needed.
    appendFoldIntoMenu(builder, tableAdapter);

    builder.appendSeparator();
    appendExpandTreeActionItems(builder);

    return builder.build();
  }

  private void appendFoldIntoMenu(
      DepanFxContextMenuBuilder builder,
      DepanFxNodeListTableAdapter tableAdapter) {

    DepanFxMenuBuilder menuBuilder = new DepanFxMenuBuilder(FOLD_TREE_INTO);
    tableAdapter.streamSections()
        .filter(DepanFxFoldSection.class::isInstance)
        .map(DepanFxFoldSection.class::cast)
        .forEach(f -> menuBuilder.appendMenuItem(
            buildFoldTreeIntoItem(f, getFork())));

    if (menuBuilder.isEmpty()) {
      return;
    }

    // Only append the fold into menu if there are fold sections.
    builder.appendSeparator();
    builder.appendSubMenu(menuBuilder.build());
  }

  private MenuItem buildFoldTreeIntoItem(
      DepanFxFoldSection foldSection, DepanFxNodeListGraphNode node) {
    String label = foldSection.getDisplayName();
    return DepanFxMenuItemFactory.createActionItem(
        label, e -> runFoldTreeInto(foldSection, node));
  }

  private void runFoldTreeInto(
      DepanFxFoldSection foldSection,
      DepanFxNodeListGraphNode node) {
    DepanFxTreeSection srcSection = (DepanFxTreeSection) node.getSection();
    DepanFxTreeModel srcTree = srcSection.getTreeModel();
    DepanFxTreeModel subModel = srcTree.subTreeModel(node.getGraphNode());

    foldSection.addTreeModel(subModel);
  }
}
