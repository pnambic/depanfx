package com.pnambic.depanfx.nodelist.gui.sections;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListGraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.gui.sections.folds.DepanFxFoldSection;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxMenuItemFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

public class DepanFxTreeForkItem extends DepanFxNodeListForkItem {

  public static final String FOLD_TREE_INTO = "Fold Tree Into";

  public static final String FOLD_TREE_INTO_MULTI = "Fold Trees Into";

  public static final String FOLD_SELECTION_INTO = "Fold Selection Into...";

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxTreeForkItem.class);

  public DepanFxTreeForkItem(DepanFxTreeFork fork) {
    super(fork);
  }

  @Override
  public void fillNodeContextMenu(
      ContextMenu contextMenu,
      Scene scene,
      DepanFxNodeListTableAdapter tableAdapter,
      GraphNode node) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder(contextMenu);

    appendRecursiveActionItems(builder, tableAdapter);

    builder.appendSeparator();
    appendCopyActionItems(builder);

    // Conditional, with separator if needed.
    appendFoldIntoMenu(builder, tableAdapter);

    builder.appendSeparator();
    appendExpandTreeActionItems(builder);
  }

  @Override
  public void fillMultiContextMenu(
      ContextMenu contextMenu, Scene scene,
      DepanFxNodeListTableAdapter tableAdapter,
      ObservableList<TreeItem<DepanFxNodeListMember>> choices) {
    DepanFxContextMenuBuilder builder =
        new DepanFxContextMenuBuilder(contextMenu);

    appendRecursiveMultiActionItems(builder, tableAdapter, choices);

    // Conditional, with separator if needed.
    appendFoldIntoMultiMenu(builder, tableAdapter, choices);
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
    DepanFxNodeList selectNodes = tableAdapter.getSelection();

    // Only append the fold into menu if there are fold sections.
    builder.appendSeparator();
    builder.appendSubMenu(menuBuilder.build());
    if (selectNodes.getNodes().size() >= 2) {
      builder.appendActionItem(FOLD_SELECTION_INTO,
          e -> runFoldSelectionInto());
    };
  }

  private void runFoldSelectionInto() {
    LOG.warn("Fold selection into not implemented yet");
  }

  private void appendFoldIntoMultiMenu(
      DepanFxContextMenuBuilder builder,
      DepanFxNodeListTableAdapter tableAdapter,
      ObservableList<TreeItem<DepanFxNodeListMember>> choices) {

    DepanFxMenuBuilder menuBuilder =
        new DepanFxMenuBuilder(FOLD_TREE_INTO_MULTI);
    tableAdapter.streamSections()
        .filter(DepanFxFoldSection.class::isInstance)
        .map(DepanFxFoldSection.class::cast)
        .forEach(f -> menuBuilder.appendMenuItem(
            buildFoldTreeIntoMultiItem(f, choices)));

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

  private MenuItem buildFoldTreeIntoMultiItem(
      DepanFxFoldSection foldSection,
      ObservableList<TreeItem<DepanFxNodeListMember>> choices) {
    String label = foldSection.getDisplayName();
    return DepanFxMenuItemFactory.createActionItem(
        label, e -> runFoldTreeIntoMulti(foldSection, choices));
  }

  private void runFoldTreeInto(
      DepanFxFoldSection foldSection,
      DepanFxNodeListGraphNode node) {
    DepanFxTreeSection srcSection = (DepanFxTreeSection) node.getSection();
    DepanFxTreeModel srcTree = srcSection.getTreeModel();
    DepanFxTreeModel subModel = srcTree.subTreeModel(node.getGraphNode());

    foldSection.addTreeModel(subModel);
  }

  private void runFoldTreeIntoMulti(
      DepanFxFoldSection foldSection,
      ObservableList<TreeItem<DepanFxNodeListMember>> choices) {

    Stream<DepanFxTreeModel> foldTrees = choices.stream()
        .map(n -> getTreeModel(foldSection, n));

    foldSection.addTreeModels(foldTrees);
  }

  private DepanFxTreeModel getTreeModel(DepanFxFoldSection foldSection,
      TreeItem<DepanFxNodeListMember> member) {

    if (member.getValue() instanceof DepanFxNodeListGraphNode node) {
      DepanFxTreeSection srcSection = (DepanFxTreeSection) node.getSection();
      DepanFxTreeModel srcTree = srcSection.getTreeModel();
      return srcTree.subTreeModel(node.getGraphNode());
    }

    return null;
  }
}
