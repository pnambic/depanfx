package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxCategoryColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxFocusColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeKeyColumnData;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Eventually, this should be a registry of column type contributions.
 */
public class DepanFxColumnRegistry {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxColumnRegistry.class);

  @SuppressWarnings("unchecked")
  public static DepanFxNodeListColumn toColumn(
      DepanFxNodeListTableAdapter tableAdapter,
      DepanFxWorkspaceResource<?> columnRsrc) {
    switch (columnRsrc.getResource()) {
    case DepanFxCategoryColumnData val:
      return new DepanFxCategoryColumn(tableAdapter,
          (DepanFxWorkspaceResource<DepanFxCategoryColumnData>) columnRsrc);
    case DepanFxFocusColumnData val:
      return new DepanFxFocusColumn(tableAdapter,
          (DepanFxWorkspaceResource<DepanFxFocusColumnData>) columnRsrc);
    case DepanFxNodeKeyColumnData val:
      return new DepanFxNodeKeyColumn(tableAdapter,
          (DepanFxWorkspaceResource<DepanFxNodeKeyColumnData>) columnRsrc);

    default:
      break;
    }
    LOG.warn("Unknown type {} for column construction",
        columnRsrc.getResource().getClass().getName());
    return null;
  }
}
