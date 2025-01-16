package com.pnambic.depanfx.workspace.tooldata;

import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.Comparator;

/**
 * The minimal expectations for any tool data.
 */
public class DepanFxBaseToolData {

  /**
   * Provides an alphabetically ordered sequence of filter resources,
   * based on the tool name of each resource.
   * This helps ensure that consumers always see the same order,
   * regardless of set construction.
   *
   * Only one required.
   * Suitable for {@code .sorted(DepanFxBaseToolData.BY_RESOURCE_NAME)}
   */
  public static final ToolNameComparator BY_RESOURCE_NAME =
      new ToolNameComparator();

  private final String toolName;

  private final String toolDescription;

  public DepanFxBaseToolData(String toolName, String toolDescription) {
    this.toolName = toolName;
    this.toolDescription = toolDescription;
  }

  public String getToolName() {
    return toolName;
  }

  public String getToolDescription() {
    return toolDescription;
  }

  /**
   * Provides an alphabetically ordered sequence of filter resources,
   * based on the tool name of each resource.
   *
   * This ensure that consumers always see the same order,
   * regardless of set construction.
   */
  private static class ToolNameComparator
    implements Comparator<DepanFxWorkspaceResource<? extends DepanFxBaseToolData>> {

    @Override
    public int compare(
        DepanFxWorkspaceResource<? extends DepanFxBaseToolData> left,
        DepanFxWorkspaceResource<? extends DepanFxBaseToolData> right) {

      return
          left.getResource().getToolName()
          .compareTo(right.getResource().getToolName());
    }
  }
}
