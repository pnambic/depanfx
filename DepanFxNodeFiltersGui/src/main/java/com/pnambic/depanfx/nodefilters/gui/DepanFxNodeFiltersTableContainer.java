package com.pnambic.depanfx.nodefilters.gui;

import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;

public interface DepanFxNodeFiltersTableContainer {

  void addFilter(DepanFxBaseFilterData filterData);

  void deleteFilter(DepanFxBaseFilterData filterData);
}
