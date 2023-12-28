package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class DepanFxTreeForkItem extends DepanFxNodeListItem {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxTreeForkItem.class);

  private boolean freshTree = true;

  public DepanFxTreeForkItem(DepanFxTreeFork fork) {
    super(fork);
  }

  @Override
  public boolean isLeaf() {
    return false;
  }

  @Override
  public ObservableList<TreeItem<DepanFxNodeListMember>> getChildren() {
    if (freshTree) {
      freshTree = false;
      super.getChildren().setAll(buildChildren());
    }

    return super.getChildren();
  }

  private ObservableList<TreeItem<DepanFxNodeListMember>> buildChildren() {
    DepanFxTreeFork folder = (DepanFxTreeFork) getValue();
    LOG.info("building children for {}", folder.getDisplayName());

    Collection<GraphNode> nodes = folder.getMembers();

    List<TreeItem<DepanFxNodeListMember>> result =
        new ArrayList<>(nodes.size());
    nodes.stream()
      .map(folder::buildTreeMember)
      .forEach(result::add);
    folder.sortTreeItems(result);

    return FXCollections.observableList(result);
  }
}
