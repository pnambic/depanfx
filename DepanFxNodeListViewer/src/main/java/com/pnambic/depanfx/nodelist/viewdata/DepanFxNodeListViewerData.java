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

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListSelection;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.session.viewdata.DepanFxBaseViewerData;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

/**
 * A simple marker class for the workspace viewer.  It is always constructed the
 * same way,so there are no data elements to persist.
 */
public class DepanFxNodeListViewerData extends DepanFxBaseViewerData{

  private final String viewTitle;

  private final DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc;

  private final DepanFxWorkspaceResource<DepanFxNodeListTableViewData> tableViewRsrc;

  private DepanFxNodeListViewerData(String viewTitle,
      DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc,
      DepanFxWorkspaceResource<DepanFxNodeListTableViewData> tableViewRsrc) {
    this.viewTitle = viewTitle;
    this.nodeListRsrc = nodeListRsrc;
    this.tableViewRsrc = tableViewRsrc;
  }

  public String getViewerTitle() {
    return viewTitle;
  }

  public DepanFxNodeList getNodeList() {
    return nodeListRsrc.getResource();
  }

  public DepanFxNodeListSelection getSelection() {
    return null;
  }

  public DepanFxNodeListTableViewData getTableView() {
    return tableViewRsrc.getResource();
  }
}
