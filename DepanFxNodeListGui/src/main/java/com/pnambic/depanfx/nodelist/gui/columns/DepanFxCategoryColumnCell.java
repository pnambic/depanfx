package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListGraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxTreeFork;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxCategoryColumnData.CategoryEntry;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;

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
import javafx.collections.ObservableList;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class DepanFxCategoryColumnCell extends DepanFxSimpleColumnCell {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxCategoryColumnCell.class);

  private Map<CategoryEntry, BooleanProperty> categoryChoices;

  public DepanFxCategoryColumnCell(DepanFxNodeListColumn nodeListColumn) {
    super(nodeListColumn);
  }

  @Override
  protected void stylizeCell(DepanFxNodeListMember member) {
    super.stylizeCell(member);

    if (member instanceof DepanFxNodeListGraphNode) {
      setContextMenu(buildEditMenu((DepanFxNodeListGraphNode) member));
      return;
    }
    setContextMenu(null);
  }

  private ContextMenu buildEditMenu(DepanFxNodeListGraphNode member) {
    categoryChoices = buildCategoryChoices(member);
    Collection<CategoryEntry> memberCategories = getCategoryColumn()
        .getCurrentCategories(member.getGraphNode());

    int categoryCount = memberCategories.size();
    if (categoryCount > 1) {
      return buildMultiEditMenu();
    }
    return buildSingleEditMenu();
  }

  private ContextMenu buildSingleEditMenu() {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    streamCategories()
        .forEach(c -> appendSetCategoryAction(builder, c));

    builder.appendSeparator();
    builder.appendActionItem("None", e -> setCategoryAction(null));

    builder.appendSeparator();
    Menu multiMenu = new Menu("Multiple...");
    populateMultipleMenu(multiMenu.getItems());
    builder.appendSubMenu(multiMenu);

    return builder.build();
  }

  private ContextMenu buildMultiEditMenu() {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    ContextMenu result = builder.build();
    populateMultipleMenu(result.getItems());
    return result;
  }

  private void populateMultipleMenu(ObservableList<MenuItem> multiItems) {
    streamCategories()
        .map(this::buildCheckBox)
        .forEach(multiItems::add);
    if (getItem() instanceof DepanFxTreeFork) {
      multiItems.add(new SeparatorMenuItem());
      multiItems.add(DepanFxContextMenuBuilder.createActionItem(
          "Set recursive", e -> setRecursiveAction()));
      multiItems.add(DepanFxContextMenuBuilder.createActionItem(
          "Add recursive", e -> addRecursiveAction()));
    }
  }

  private Map<CategoryEntry, BooleanProperty> buildCategoryChoices(
      DepanFxNodeListGraphNode nodeItem) {
    Map<CategoryEntry, BooleanProperty> result = new HashMap<>();
    Collection<CategoryEntry> itemCategories =
        getCategoryColumn()
            .getCurrentCategories(nodeItem.getGraphNode());

    streamCategories()
        .forEach(c -> result.put(
            c, new SimpleBooleanProperty(itemCategories.contains(c))));
    return result;
  }

  private CheckMenuItem buildCheckBox(CategoryEntry entry) {
    CheckMenuItem result = new CheckMenuItem(entry.getCategoryLabel());
    result.selectedProperty().bindBidirectional(categoryChoices.get(entry));
    result.setOnAction(e -> setCategoriesAction());
    return result;
  }

  private void appendSetCategoryAction(
      DepanFxContextMenuBuilder builder, CategoryEntry entry) {
    builder.appendActionItem(
        entry.getCategoryLabel(), e -> setCategoryAction(entry));
  }

  private void setCategoryAction(CategoryEntry entry) {
    DepanFxNodeListGraphNode nodeItem = (DepanFxNodeListGraphNode) getItem();
    getCategoryColumn()
        .setListMembership(nodeItem.getGraphNode(), entry);
    stylizeCell(nodeItem);
  }

  private void setRecursiveAction() {
    DepanFxNodeListGraphNode nodeItem = (DepanFxNodeListGraphNode) getItem();
    List<CategoryEntry> updateCategories = getUpdateCategories();
    getCategoryColumn().setDecendantsCategories(nodeItem, updateCategories);
  }

  private void addRecursiveAction() {
    DepanFxNodeListGraphNode nodeItem = (DepanFxNodeListGraphNode) getItem();
    List<CategoryEntry> updateCategories = getUpdateCategories();
    getCategoryColumn().addDecendantsCategories(nodeItem, updateCategories);
  }

  private void setCategoriesAction() {
    DepanFxNodeListGraphNode nodeItem = (DepanFxNodeListGraphNode) getItem();
    List<CategoryEntry> updateCategories = getUpdateCategories();
    getCategoryColumn()
        .setListMembership(nodeItem.getGraphNode(), updateCategories);
    stylizeCell(nodeItem);
  }

  private List<CategoryEntry> getUpdateCategories() {
    return categoryChoices.entrySet().stream()
        .filter(e -> e.getValue().getValue())
        .map(e -> e.getKey())
        .collect(Collectors.toList());
  }

  private Stream<CategoryEntry> streamCategories() {
    return getCategoryColumn()
        .getCategories().getCategoryList().stream();
  }

  private DepanFxCategoryColumn getCategoryColumn() {
    return (DepanFxCategoryColumn) getColumn();
  }
}
