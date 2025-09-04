/*
 * Copyright 2023 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pnambic.depanfx.nodelist.gui.sections;

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListItem;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxBaseSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceChooser;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilterModel;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.TreeItem;

/**
 * Common behavior for all node list section items.
 */
public abstract class DepanFxNodeListSectionItem extends DepanFxNodeListItem {

  public static final String EXPORT_TO_CSV = "Export to CSV";

  private boolean sectionLoaded = false;

  public DepanFxNodeListSectionItem(DepanFxNodeListSection section) {
    super(section);
  }

  @Override
  public boolean isLeaf() {
    return false;
  }

  @Override
  public ObservableList<TreeItem<DepanFxNodeListMember>> getChildren() {
    if (!sectionLoaded) {
      sectionLoaded = true;
      super.getChildren().setAll(buildChildren());
    }

    return super.getChildren();
  }

  abstract protected ObservableList<TreeItem<DepanFxNodeListMember>>
  buildChildren();

  protected DepanFxNodeListSection getSection() {
    return (DepanFxNodeListSection) getValue();
  }

  protected DepanFxResourceChooser prepareSectionChooser(
      DepanFxNodeListTableAdapter tableAdapter,
      DepanFxResourceFilterModel rsrcFilter) {
    DepanFxResourceChooser result =
        new DepanFxResourceChooser(
            tableAdapter.getWorkspace(), tableAdapter.getDialogRunner());
    DepanFxResourcePerspectives.prepareResourceFinder(
        result, DepanFxNodeListSectionData.SECTIONS_TOOL_PATH);
    result.getExtensionFilters().add(rsrcFilter);
    result.setSelectedExtensionFilter(rsrcFilter);
    return result;
  }

  protected void updateSectionDataResource(
      DepanFxNodeListTableAdapter tableAdapter,
      DepanFxWorkspaceResource<? extends DepanFxBaseSectionData>  dataRsrc) {
    tableAdapter.updateSection(getSection(), dataRsrc);
  }

  protected Menu buildNewSectionMenu(
      Scene scene,
      DepanFxNodeListTableAdapter tableAdapter,
      DepanFxNodeListSection before) {
    return DepanFxNodeListSections.newSectionMenu(scene, tableAdapter, before);
  }
}
