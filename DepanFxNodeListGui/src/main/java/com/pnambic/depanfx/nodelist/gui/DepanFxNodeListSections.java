package com.pnambic.depanfx.nodelist.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javafx.scene.control.TreeItem;

public class DepanFxNodeListSections {

  public static Comparator<TreeItem<DepanFxNodeListMember>> COMPARE_BY_SORT_KEY =
      new CompareBySortKey();

  private DepanFxNodeListSections() {
    // Prevent instatiation.
  }

  public static List<DepanFxNodeListSection> getFinalSection() {
    DepanFxNodeListSection section = new DepanFxNodeListSection();
    List<DepanFxNodeListSection> result = new ArrayList<>();
    result.add(section);
    return result;
  }

  private static class CompareBySortKey
      implements Comparator<TreeItem<DepanFxNodeListMember>> {

    @Override
    public int compare(
        TreeItem<DepanFxNodeListMember> one,
        TreeItem<DepanFxNodeListMember> two) {

      // Better to sort by TreeItem type, then name, but this works ok.
      String oneKey = ((DepanFxNodeListMember) one.getValue()).getSortKey();
      String twoKey = ((DepanFxNodeListMember) two.getValue()).getSortKey();
      return oneKey.compareTo(twoKey);
    }
  }
}
