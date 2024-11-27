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
package com.pnambic.depanfx.nodeview.viewdata;

import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
import com.pnambic.depanfx.session.viewdata.DepanFxBaseViewerData;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

/**
 * The node view panel renders the node view data in a scene viewer tab.
 * There is no need for additional data.
 */
public class DepanFxNodeViewPanelData implements DepanFxBaseViewerData {

  private final DepanFxWorkspaceResource<DepanFxNodeViewData> nodeViewRsrc;

  public DepanFxNodeViewPanelData(
      DepanFxWorkspaceResource<DepanFxNodeViewData> nodeViewRsrc) {
    this.nodeViewRsrc = nodeViewRsrc;
  }

  public String getViewerTitle() {
    return nodeViewRsrc.getResource().getToolName();
  }

  public DepanFxWorkspaceResource<DepanFxNodeViewData> getNodeViewRsrc() {
    return nodeViewRsrc;
  }
}
