package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.context.ContextNodeId;
import com.pnambic.depanfx.graph.context.GraphContextKeys;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListSectionData.OrderBy;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListSectionData.OrderDirection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.MessageFormat;
import java.util.Comparator;

import javafx.scene.control.TreeItem;

public class DepanFxNodeListSections {

  static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeListSections.class);

  private DepanFxNodeListSections() {
    // Prevent instantiation.
  }

  public static String fmtDisplayName(
      String sectionLabel, int listSize, boolean displayNodeCount) {
    if (displayNodeCount) {
      if (listSize == 1) {
        return MessageFormat.format(
            "{0} ({1} node)", sectionLabel, listSize);

      }
      return MessageFormat.format(
          "{0} ({1} nodes)", sectionLabel, listSize);
    }
    return sectionLabel;
  }

  /**
   * Only use with graph node members.  Should not be used with sections, etc.
   */
  public static abstract class CompareMembers
      implements Comparator<TreeItem<DepanFxNodeListMember>> {

    private final OrderDirection direction;

    public CompareMembers(OrderDirection direction) {
      this.direction = direction;
    }

    @Override
    public int compare(
        TreeItem<DepanFxNodeListMember> itemOne,
        TreeItem<DepanFxNodeListMember> itemTwo) {
      DepanFxNodeListMember memberOne = itemOne.getValue();
      DepanFxNodeListMember memberTwo = itemTwo.getValue();
      return orderMembers(memberOne, memberTwo);
    }

    private int orderMembers(
        DepanFxNodeListMember memberOne, DepanFxNodeListMember memberTwo ) {
      switch (direction) {
      case FORWARD:
        return compareMembers(memberOne, memberTwo);
      case REVERSE:
        return - compareMembers(memberOne, memberTwo);
      }
      LOG.warn(
          "Unrecognize direction for section ordering {}", direction);
      return 0;
    }

    protected abstract int compareMembers(
        DepanFxNodeListMember memberOne, DepanFxNodeListMember memberTwo);
  }

  /**
   * A good choice to implement {@code CompareBySortKey.compareMembers()}.
   */
  public static int compareBySortKey(
      DepanFxNodeListMember memberOne, DepanFxNodeListMember memberTwo) {
    String oneKey = ((DepanFxNodeListGraphNode) memberOne).getSortKey();
    String twoKey = ((DepanFxNodeListGraphNode) memberTwo).getSortKey();
    return oneKey.compareTo(twoKey);
  }

  public static String getSortKey(GraphNode node, OrderBy orderBy) {
    ContextNodeId nodeId = node.getId();
    switch (orderBy) {
    case NODE_ID:
      return GraphContextKeys.toNodeKey(nodeId);
    case NODE_KEY:
      return nodeId.getNodeKey();
    case NODE_LEAF:
      return getLeafSortKey(nodeId.getNodeKey());
    }

    // Use the full node key the orderBy value is not known.
    LOG.warn("Unrecognized order by criteria {}", orderBy);
    return GraphContextKeys.toNodeKey(nodeId);
  }

  public static String getLeafSortKey(String nodeKey) {
    return new File(nodeKey).getName();
  }
}
