package com.pnambic.depanfx.perspective.plugins;

import java.util.Comparator;

public interface DepanFxOrderableContribution {

  String getOrderKey();

  static class ContribComparator
      implements Comparator<DepanFxOrderableContribution> {

    @Override
    public int compare(
        DepanFxOrderableContribution contribOne,
        DepanFxOrderableContribution contribTwo) {
      String orderOne = contribOne.getOrderKey();
      String orderTwo = contribTwo.getOrderKey();
      return orderOne.compareTo(orderTwo);
    }
  }

  static final ContribComparator CONTRIB_COMPARE =
      new ContribComparator();
}
