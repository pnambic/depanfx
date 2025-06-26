package com.pnambic.depanfx.nodelist.gui.sections;

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.gui.sections.folds.DepanFxFoldSection;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxBaseSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxFlatSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxFoldSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxTreeSectionData;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

/**
 * Eventually, this should be a registry of section type contributions.
 */
public class DepanFxSectionRegistry {

  /**
   * Updates the supplied section with the supplied data.
   *
   * Typically, the table containing the updated section
   * will need to have its table root reset after the update.
   *
   * @return {@code} if an update was perform,
   *    or {@code false} if the data was unrecognized.
   */
  @SuppressWarnings("unchecked")
  public static boolean updateSection(DepanFxNodeListSection section,
      DepanFxWorkspaceResource<? extends DepanFxBaseSectionData> dataRsrc) {
    switch (section) {
    case DepanFxTreeSection tree:
      tree.setSectionDataRsrc(
          (DepanFxWorkspaceResource<DepanFxTreeSectionData>) dataRsrc);
      return true;
    case DepanFxFlatSection flat:
      flat.setSectionDataRsrc(
          (DepanFxWorkspaceResource<DepanFxFlatSectionData>) dataRsrc);
      return true;
    default:
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  public static DepanFxNodeListSection createSection(
      DepanFxNodeListTableAdapter tableAdapter,
      DepanFxWorkspaceResource<? extends DepanFxBaseSectionData> dataRsrc) {
    switch (dataRsrc.getResource()) {
    case DepanFxTreeSectionData tree:
      return new DepanFxTreeSection(tableAdapter,
          (DepanFxWorkspaceResource<DepanFxTreeSectionData>) dataRsrc);
    case DepanFxFoldSectionData fold:
      return new DepanFxFoldSection(tableAdapter,
          (DepanFxWorkspaceResource<DepanFxFoldSectionData>) dataRsrc);
    case DepanFxFlatSectionData flat:
      return new DepanFxFlatSection(
          (DepanFxWorkspaceResource<DepanFxFlatSectionData>) dataRsrc);
    default:
    }
    return null;
  }
}
