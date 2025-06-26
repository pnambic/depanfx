package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxExportFlatSectionDialog;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxExportTreeSectionDialog;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxFlatLeaf;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxFlatSection;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxFlatSectionToolDialog;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSection;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxTreeFork;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxTreeLeaf;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxTreeSection;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxTreeSectionToolDialog;
import com.pnambic.depanfx.nodelist.gui.sections.folds.DepanFxFoldSection;
import com.pnambic.depanfx.nodelist.gui.sections.folds.DepanFxFoldSectionToolDialog;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherGroup;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxBaseSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxFlatSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxFoldSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxTreeSectionData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceChooser;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.util.StringConverter;

public class DepanFxNodeListCell
    extends CheckBoxTreeTableCell<DepanFxNodeListMember, DepanFxNodeListMember> {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeListCell.class);

  private static final String SELECT_FLAT_SECTION = "Select Flat Section...";

  private static final String EDIT_FLAT_SECTION = "Edit Flat Section...";

  // Tree section actions
  private static final String SELECT_TREE_SECTION = "Select Tree Section...";

  private static final String EDIT_TREE_SECTION = "Edit Tree Section...";

  private static final String EXPORT_TO_CSV = "Export to CSV...";

  private static final String INSERT_ABOVE_FOLD_SECTION =
      "Insert Fold Section";

  private static final String INSERT_ABOVE_MEMBER_TREE_SECTION =
      "Insert Member Tree Section";

  // Copy behaviors
  private static final String COPY_ITEM = "Copy";

  private static final String COPY_AS_ITEM = "Copy as";

  private static final String COPY_DISPLAY_ITEM = "Display";

  private static final String COPY_NODE_KEY = "Node Key";

  private static final String COPY_SIMPLE_NAME = "Short Name";

  // Fork/Directory actions
  private static final String SELECT_RECURSIVE = "Select Recursive";

  private static final String CLEAR_RECURSIVE = "Clear Recursive";

  private static final String EXPAND_TREE_5 = "Expand Tree (5)";

  private static final String EXPAND_TREE_20 = "Expand Tree (20)";

  private static final String EXPAND_TREE_100 = "Expand Tree (100)";

  // Allow cells to act on viewer (e.g. change sections, etc.)
  private final DepanFxNodeListTableAdapter tableAdapter;

  public DepanFxNodeListCell(DepanFxNodeListTableAdapter tableAdapter) {
    this.tableAdapter = tableAdapter;
    setConverter(new NameConverter());
    setSelectedStateCallback(
        p -> tableAdapter.getCheckBoxObservable(p.intValue()));
  }

  @Override
  public void updateItem(DepanFxNodeListMember member, boolean empty) {
    super.updateItem(member, empty);

    // Visual space reserved for future use.
    if (empty) {
      return;
    }
    // The normal case.
    if (member != null) {
      stylizeCell(member);
      return;
    }
    // Something unexpected.
    setText("<null>");
    setGraphic(null);
  }

  private void stylizeCell(DepanFxNodeListMember member) {
    switch (member) {
    case DepanFxFlatLeaf item:
      // Items in the flat section don't have menus (yet),
      // and they are not an unexpected type.
      setContextMenu(null);
      return;
    case DepanFxFlatSection flat:
      setContextMenu(flatSectionMenu(flat));
      return;
    case DepanFxTreeLeaf leaf:
      setContextMenu(treeLeafMenu(leaf));
      return;
    case DepanFxTreeFork fork:
      setContextMenu(treeForkMenu(fork));
      return;
    case DepanFxTreeSection tree:
      setContextMenu(treeSectionMenu(tree));
      return;
    case DepanFxFoldSection.FoldLeaf leaf:
      setContextMenu(foldLeafMenu(leaf));
      return;
    case DepanFxFoldSection.FoldFork fork:
      setContextMenu(foldForkMenu(fork));
      return;
    case DepanFxFoldSection fold:
      setContextMenu(foldSectionMenu(fold));
      return;
    default:
      LOG.warn("Unrecognized node list element of class {}",
          member.getClass().getName());
    }

    // Otherwise clear the context menu
    setContextMenu(null);
  }

  private ContextMenu flatSectionMenu(DepanFxFlatSection member) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem(SELECT_FLAT_SECTION,
        e -> openFlatSectionFinder(member));
    builder.appendActionItem(EDIT_FLAT_SECTION,
        e -> openFlatSectionEditor(member));

    builder.appendSeparator();
    builder.appendActionItem(
        EXPORT_TO_CSV,
        e -> runExportToCsvAction(member));

    builder.appendSeparator();
    builder.appendActionItem(
        INSERT_ABOVE_MEMBER_TREE_SECTION,
        e -> runInsertMemberTreeSectionAction(member));
    builder.appendActionItem(
        INSERT_ABOVE_FOLD_SECTION,
        e -> runInsertFoldSectionAction(member));
    return builder.build();
  }

  private ContextMenu treeSectionMenu(DepanFxTreeSection member) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem(SELECT_TREE_SECTION,
        e -> openTreeSectionFinder(member));
    builder.appendActionItem(EDIT_TREE_SECTION,
        e -> openTreeSectionEditor(member));

    builder.appendSeparator();
    builder.appendActionItem(
        EXPORT_TO_CSV,
        e -> runExportToCsvAction(member));
    return builder.build();
  }

  private ContextMenu treeForkMenu(DepanFxTreeFork fork) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem(
        SELECT_RECURSIVE,
        e -> runSelectRecursiveAction(fork, true));
    builder.appendActionItem(
        CLEAR_RECURSIVE,
        e -> runSelectRecursiveAction(fork, false));
    builder.appendSeparator();
    builder.appendActionItem(
        COPY_ITEM,
        e -> runCopyFrom(fork.getDisplayName()));
    builder.appendSubMenu(buildCopyMenu(fork));
    builder.appendSeparator();
    builder.appendActionItem(
        EXPAND_TREE_5, e -> runExpandTreeAction(5));
    builder.appendActionItem(
        EXPAND_TREE_20, e -> runExpandTreeAction(20));
    builder.appendActionItem(
        EXPAND_TREE_100, e -> runExpandTreeAction(100));
    return builder.build();
  }

  private ContextMenu treeLeafMenu(DepanFxTreeLeaf leaf) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem(
        COPY_ITEM,
        e -> runCopyFrom(leaf.getDisplayName()));
    builder.appendSubMenu(buildCopyMenu(leaf));
    return builder.build();
  }

  private ContextMenu foldSectionMenu(DepanFxFoldSection section) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem(SELECT_TREE_SECTION,
        e -> openFoldSectionFinder(section));
    builder.appendActionItem(EDIT_TREE_SECTION,
        e -> openFoldSectionEditor(section));

    builder.appendSeparator();
    builder.appendActionItem(
        EXPORT_TO_CSV,
        e -> runExportToCsvAction(section));
    return builder.build();
  }

  private ContextMenu foldForkMenu(DepanFxFoldSection.FoldFork fork) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem(
        SELECT_RECURSIVE,
        e -> runSelectRecursiveAction(fork, true));
    builder.appendActionItem(
        CLEAR_RECURSIVE,
        e -> runSelectRecursiveAction(fork, false));
    builder.appendSeparator();
    builder.appendActionItem(
        COPY_ITEM,
        e -> runCopyFrom(fork.getDisplayName()));
    builder.appendSubMenu(buildCopyMenu(fork));
    builder.appendSeparator();
    builder.appendActionItem(
        EXPAND_TREE_5, e -> runExpandTreeAction(5));
    builder.appendActionItem(
        EXPAND_TREE_20, e -> runExpandTreeAction(20));
    builder.appendActionItem(
        EXPAND_TREE_100, e -> runExpandTreeAction(100));
    return builder.build();
  }

  private ContextMenu foldLeafMenu(DepanFxFoldSection.FoldLeaf leaf) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem(
        COPY_ITEM,
        e -> runCopyFrom(leaf.getDisplayName()));
    builder.appendSubMenu(buildCopyMenu(leaf));
    return builder.build();
  }

  private Menu buildCopyMenu(DepanFxNodeListGraphNode node) {
    Menu result = new Menu(COPY_AS_ITEM);
    ObservableList<MenuItem> items = result.getItems();
    items.add(DepanFxContextMenuBuilder.createActionItem(
        COPY_DISPLAY_ITEM,
        e -> runCopyFrom(node.getDisplayName())));
    items.add(DepanFxContextMenuBuilder.createActionItem(
        COPY_NODE_KEY,
        e -> runCopyFrom(node.getGraphNode().getId().getNodeKey())));
    items.add(DepanFxContextMenuBuilder.createActionItem(
        COPY_SIMPLE_NAME,
        e -> runCopyFrom(node.getGraphNode().getId().getSimpleName())));
    return result;
  }

  private void runCopyFrom(String src) {
    Clipboard clipboard = Clipboard.getSystemClipboard();
    ClipboardContent content = new ClipboardContent();
    content.putString(src);
    clipboard.setContent(content);
  }

  private void runExpandTreeAction(int expandLimit) {
    TreeItem<DepanFxNodeListMember> tree = getTableRow().getTreeItem();
    BreadthExpander expander = new BreadthExpander(expandLimit);
    expander.addBreadthItems(tree.getChildren());
    expander.expandTree();
    tree.setExpanded(true);
  }

  private void runExportToCsvAction(DepanFxFoldSection foldSection) {
    LOG.info("Fold section to CVS export not yet implemented");
  }

  private void runExportToCsvAction(DepanFxFlatSection flatSection) {
    DepanFxExportFlatSectionDialog.runExportDialog(flatSection, tableAdapter);
  }

  private void runExportToCsvAction(DepanFxTreeSection treeSection) {
    DepanFxExportTreeSectionDialog.runExportDialog(
        treeSection, tableAdapter.getDialogRunner());
  }

  private void openTreeSectionEditor(DepanFxTreeSection member) {
    Dialog<DepanFxTreeSectionToolDialog> treeSectionEditor =
        DepanFxTreeSectionToolDialog.runEditDialog(
            member.getSectionResource(), tableAdapter.getDialogRunner());
    treeSectionEditor.getController().getToolResource()
        .ifPresent(d -> updateSectionDataRsrc(member, d));
  }

  private void openTreeSectionFinder(DepanFxTreeSection member) {
    DepanFxWorkspace workspace = tableAdapter.getWorkspace();

    prepareTreeSectionChooser(workspace).showOpenDialog(getScene())
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(p -> workspace.getWorkspaceResource(
            p, DepanFxTreeSectionData.class))
        .ifPresent(d -> updateSectionDataRsrc(member, d));
  }

  private void openFoldSectionEditor(DepanFxFoldSection member) {
    LOG.info("Fold section editor not yet implemented");
/*
    Dialog<DepanFxTreeSectionToolDialog> treeSectionEditor =
        DepanFxFoldSectionToolDialog.runEditDialog(
            member.getSectionResource(), tableAdapter.getDialogRunner());
    treeSectionEditor.getController().getToolResource()
        .ifPresent(d -> updateSectionDataRsrc(member, d));
*/
  }

  private void openFoldSectionFinder(DepanFxFoldSection member) {
    DepanFxWorkspace workspace = tableAdapter.getWorkspace();

    prepareFoldSectionChooser(workspace).showOpenDialog(getScene())
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(p -> workspace.getWorkspaceResource(
            p, DepanFxFoldSectionData.class))
        .ifPresent(d -> updateSectionDataRsrc(member, d));
  }

  private void openFlatSectionEditor(DepanFxFlatSection member) {
    Dialog<DepanFxFlatSectionToolDialog> flatSectionEditor =
        DepanFxFlatSectionToolDialog.runEditDialog(
            member.getSectionResource(),
            tableAdapter.getDialogRunner());

    flatSectionEditor.getController().getToolResource()
        .ifPresent(d -> updateSectionDataRsrc(member, d));
  }

  private void openFlatSectionFinder(DepanFxFlatSection member) {
    DepanFxWorkspace workspace = tableAdapter.getWorkspace();

    prepareFlatSectionChooser(workspace).showOpenDialog(getScene())
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(p -> workspace.getWorkspaceResource(
            p, DepanFxFlatSectionData.class))
        .ifPresent(d -> updateSectionDataRsrc(member, d));
  }

  private void updateSectionDataRsrc(
      DepanFxNodeListSection member,
      DepanFxWorkspaceResource<? extends DepanFxBaseSectionData>  dataRsrc) {
    tableAdapter.updateSection(member, dataRsrc);
  }

  private void runInsertMemberTreeSectionAction(DepanFxNodeListSection before) {
    getInitialTreeSectionResource()
        .ifPresent(r -> tableAdapter.insertSection(before, r));
  }

  private void runInsertFoldSectionAction(DepanFxNodeListSection before) {
    getInitialFoldSectionResource()
        .ifPresent(r -> tableAdapter.insertSection(before, r));
  }

  private Optional<DepanFxWorkspaceResource<DepanFxTreeSectionData>>
      getInitialTreeSectionResource() {
    ContextModelId modelId = tableAdapter.getGraphDoc().getContextModelId();

    return DepanFxProjects.getBuiltIn(
        tableAdapter.getWorkspace(), DepanFxTreeSectionData.class,
        c -> isContextModelMatcherResource(c, modelId));
  }

  private Optional<DepanFxWorkspaceResource<DepanFxFoldSectionData>>
      getInitialFoldSectionResource() {
    DepanFxFoldSectionData result =
        DepanFxFoldSectionData.emptyFoldSectionData(
            tableAdapter.getGraphDocResource());
    return Optional.of(tableAdapter.getWorkspace().addScratchResource(result));
  }

  private boolean isContextModelMatcherResource(
      DepanFxBuiltInContribution<DepanFxTreeSectionData> contrib,
      ContextModelId modelId) {
    return DepanFxLinkMatcherGroup.isContextModelMatcherResource(
        modelId, contrib.getDocument().getLinkMatcherRsrc());
  }

  private void runSelectRecursiveAction(
      DepanFxFoldSection.FoldFork fork, boolean value) {;
    // Do the root
    GraphNode selectNode = fork.getGraphNode();
    tableAdapter.doSelectGraphNodeAction(selectNode, value);

    // Then all the reachable nodes.
    Collection<GraphNode> nodes = fork.getDecendants();
    tableAdapter.doSelectGraphNodesAction(nodes.stream(), value);
  }

  private void runSelectRecursiveAction(DepanFxTreeFork fork, boolean value) {
    // Do the root
    GraphNode selectNode = fork.getGraphNode();
    tableAdapter.doSelectGraphNodeAction(selectNode, value);

    // Then all the reachable nodes.
    Collection<GraphNode> nodes = fork.getDecendants();
    tableAdapter.doSelectGraphNodesAction(nodes.stream(), value);
  }

  private DepanFxResourceChooser prepareFoldSectionChooser(
      DepanFxWorkspace workspace) {
    return prepareSectionChooser(
        workspace, DepanFxFoldSectionToolDialog.FOLD_SECTION_RSRC_FILTER);
  }

  private DepanFxResourceChooser prepareFlatSectionChooser(
      DepanFxWorkspace workspace) {
    return prepareSectionChooser(
        workspace, DepanFxFlatSectionToolDialog.FLAT_SECTION_RSRC_FILTER);
  }

  private DepanFxResourceChooser prepareTreeSectionChooser(
      DepanFxWorkspace workspace) {
    return prepareSectionChooser(
        workspace, DepanFxTreeSectionToolDialog.TREE_SECTION_RSRC_FILTER);
  }

  private DepanFxResourceChooser prepareSectionChooser(
      DepanFxWorkspace workspace, DepanFxResourceFilter rsrcFilter) {
    DepanFxResourceChooser result =
        new DepanFxResourceChooser(workspace, tableAdapter.getDialogRunner());
    DepanFxResourcePerspectives.prepareResourceFinder(
        result, DepanFxNodeListSectionData.SECTIONS_TOOL_PATH);
    result.getExtensionFilters().add(rsrcFilter);
    result.setSelectedExtensionFilter(rsrcFilter);
    return result;
  }

  private static class BreadthExpander {

    private int expandLimit;

    private List<TreeItem<DepanFxNodeListMember>> breadthItems =
        new ArrayList<>();

    public BreadthExpander(int expandLimit) {
      this.expandLimit = expandLimit;
    }

    public void addBreadthItems(
        List<TreeItem<DepanFxNodeListMember>> moreItems) {
      breadthItems.addAll(moreItems);
    }

    public void expandTree() {
      for (int next = 0; next < breadthItems.size(); next++) {
        if (expandLimit <= 0) {
          return;
        }
        TreeItem<DepanFxNodeListMember> currItem = breadthItems.get(next);
        ObservableList<TreeItem<DepanFxNodeListMember>> children =
            currItem.getChildren();
        addBreadthItems(children);
        if (!currItem.isExpanded()) {
          expandLimit -= children.size();
          currItem.setExpanded(true);
        }
      }
    }
  }

  private static class NameConverter
      extends StringConverter<DepanFxNodeListMember> {

    @Override
    public String toString(DepanFxNodeListMember member) {
      if (member != null) {
        return member.getDisplayName();
      }
      return "<empty>";
    }

    @Override
    public DepanFxNodeListMember fromString(String string) {
      throw new UnsupportedOperationException();
    }
  }
}
