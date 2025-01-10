package com.pnambic.depanfx.nodeview.tooldata;

import java.nio.file.Path;

/**
 * Base definitions for layout data.
 */
public class DepanFxNodeViewLayoutData {

  public static final String MEMBER_LAYOUT_RESOURCE_NAME = "Member Layout";

  public static final String LAYOUT_TOOL_DIR = "Layouts";

  public static final Path LAYOUT_TOOL_PATH =
      DepanFxNodeViewData.NODE_VIEW_TOOL_PATH.resolve(LAYOUT_TOOL_DIR);

  private DepanFxNodeViewLayoutData() {
    // Prevent instantiation.
  }
}
