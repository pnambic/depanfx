package com.pnambic.depanfx.nodelist.gui.tooldata;

import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DepanFxCategoryColumnData extends DepanFxBaseColumnData {

  public static class CategoryEntry {

    private final String categoryLabel;

    private final DepanFxWorkspaceResource nodeListRsrc;

    public CategoryEntry(
        String categoryLabel, DepanFxWorkspaceResource nodeListRsrc) {
      this.categoryLabel = categoryLabel;
      this.nodeListRsrc = nodeListRsrc;
    }

    public String getCategoryLabel() {
      return categoryLabel;
    }

    public DepanFxWorkspaceResource getNodeListRsrc() {
      return nodeListRsrc;
    }
  }

  public static final String CATEGORY_COLUMN_TOOL_EXT = "dccti";

  // Don't use the basic column name.
  public static final String BASE_COLUMN_NAME = "Category";

  private static final String BASE_COLUMN_DESCR = "New category column.";

  public static final String BASE_COLUMN_LABEL = "Category";

  private static final List<CategoryEntry> BASE_CATEGORIES =
      Collections.emptyList();

  private final List<CategoryEntry> categories;

  public DepanFxCategoryColumnData(
      String toolName, String toolDescription,
      String columnLabel, int widthMs,
      List<CategoryEntry> categories) {

    super(toolName, toolDescription, columnLabel, widthMs);
    this.categories = categories;
  }

  public static DepanFxCategoryColumnData buildInitialCategoryColumnData() {
    return new DepanFxCategoryColumnData(
        BASE_COLUMN_NAME, BASE_COLUMN_DESCR,
        BASE_COLUMN_LABEL, DepanFxBaseColumnData.BASE_COLUMN_WIDTH_MS,
        BASE_CATEGORIES);
  }

  public List<CategoryEntry> getCategories() {
    // Provide defensive copy.
    return new ArrayList<>(categories);
  }
}
