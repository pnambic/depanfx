package com.pnambic.depanfx.nodelist.gui.tooldata;

import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListData;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DepanFxNodeListTableViewData extends DepanFxBaseToolData {

  public static final String TABLE_VIEWS_TOOL_DIR = "Table Views";

  public static final String TABLE_VIEW_TOOL_EXT = "dtvti";

  public static final Path TABLE_VIEW_TOOL_PATH =
      DepanFxNodeListData.NODE_LIST_TOOL_PATH.resolve(TABLE_VIEWS_TOOL_DIR);

  private final List<DepanFxWorkspaceResource<? extends DepanFxBaseSectionData>> sectionResources;

  private final List<DepanFxWorkspaceResource<? extends DepanFxBaseColumnData>> columnResources;

  public DepanFxNodeListTableViewData(
      String toolName, String toolDescription,
      List<DepanFxWorkspaceResource<? extends DepanFxBaseSectionData>> sectionResources,
      List<DepanFxWorkspaceResource<? extends DepanFxBaseColumnData>> columnResources) {

    super(toolName, toolDescription);

    // Safety, and to ensure serializablity.
    // (even in the face of a supplied Collections.emtpyList()).
    this.sectionResources = new ArrayList<>(sectionResources);
    this.columnResources = new ArrayList<>(columnResources);
  }

  /**
   * Provided in bottom-to-top order to ensure that a flat section
   * terminates the table view.
   */
  public List<DepanFxWorkspaceResource<? extends DepanFxBaseSectionData>>
      getSectionResources() {
    return sectionResources;
  }

  /**
   * Provided in bottom-to-top order to ensure that a flat section
   * terminates the table view.
   */
  public List<DepanFxWorkspaceResource<? extends DepanFxBaseColumnData>>
      getColumnResources() {
    return columnResources;
  }
}
