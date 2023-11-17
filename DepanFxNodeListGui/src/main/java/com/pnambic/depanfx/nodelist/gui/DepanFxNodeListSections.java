package com.pnambic.depanfx.nodelist.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javafx.scene.control.TreeItem;

public class DepanFxNodeListSections {

  /**
   * Only use with graph node members.  Should not be used with sections, etc.
   */
  public static Comparator<TreeItem<DepanFxNodeListMember>> COMPARE_BY_SORT_KEY =
      new CompareBySortKey();

  private DepanFxNodeListSections() {
    // Prevent instantiation.
  }

  public static List<DepanFxNodeListSection> getFinalSection() {
    List<DepanFxNodeListSection> result = new ArrayList<>();
    result.add(new DepanFxNodeListFlatSection());
    return result;
  }

  private static class CompareBySortKey
      implements Comparator<TreeItem<DepanFxNodeListMember>> {

    @Override
    public int compare(
        TreeItem<DepanFxNodeListMember> one,
        TreeItem<DepanFxNodeListMember> two) {

      String oneKey = ((DepanFxNodeListGraphNode) one.getValue()).getSortKey();
      String twoKey = ((DepanFxNodeListGraphNode) two.getValue()).getSortKey();
      return oneKey.compareTo(twoKey);
    }
  }
}
