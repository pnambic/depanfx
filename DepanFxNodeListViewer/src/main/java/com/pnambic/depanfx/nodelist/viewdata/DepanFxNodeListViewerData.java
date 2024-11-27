/*
 * Copyright 2024 The Depan Project Authors
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
package com.pnambic.depanfx.nodelist.viewdata;

import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.session.viewdata.DepanFxBaseViewerData;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

/**
 * The table view viewer includes the node list and the table view resources.
 * The active selection in the viewer is not persisted.
 */
public class DepanFxNodeListViewerData implements DepanFxBaseViewerData {

  private final String viewTitle;

  private final DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc;

  private final DepanFxWorkspaceResource<DepanFxNodeListTableViewData> tableViewRsrc;

  public DepanFxNodeListViewerData(
      String viewTitle,
      DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc,
      DepanFxWorkspaceResource<DepanFxNodeListTableViewData> tableViewRsrc) {
    this.viewTitle = viewTitle;
    this.nodeListRsrc = nodeListRsrc;
    this.tableViewRsrc = tableViewRsrc;
  }

  public String getViewerTitle() {
    return viewTitle;
  }

  public DepanFxWorkspaceResource<DepanFxNodeList> getNodeListRsrc() {
    return nodeListRsrc;
  }

  public DepanFxWorkspaceResource<DepanFxNodeListTableViewData> getTableViewRsrc() {
    return tableViewRsrc;
  }
}
