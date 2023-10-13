package com.pnambic.depanfx.nodelist.gui;

import java.util.ArrayList;
import java.util.List;

public class DepanFxNodeListSections {

  public static List<DepanFxNodeListSection> getFinalSection() {
    DepanFxNodeListSection section = new DepanFxNodeListSection();
    List<DepanFxNodeListSection> result = new ArrayList<>();
    result.add(section);
    return result;
  }
}
