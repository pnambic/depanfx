package com.pnambic.depanfx.nodelist.gui.tooldata;

import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListData;
import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

import java.nio.file.Path;

public class DepanFxNodeListTableViewData extends DepanFxBaseToolData {

  public static final String TABLE_VIEWS_TOOL_DIR = "Table Views";

  public static final String TABLE_VIEW_TOOL_EXT = "dtvti";

  public static final Path TABLE_VIEW_TOOL_PATH =
      DepanFxNodeListData.NODE_LIST_TOOL_PATH.resolve(TABLE_VIEWS_TOOL_DIR);

  public DepanFxNodeListTableViewData(
      String toolName, String toolDescription) {

    super(toolName, toolDescription);
  }
}
