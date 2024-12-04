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
package com.pnambic.depanfx.nodeview.layouts;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodeview.gui.DepanFxNodeViewPanel;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxShiftLayoutData;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;

@Component
public class ShiftLayoutContribution
    implements DepanFxNodeLayoutRegistry.Contribution {

  public static final String SHIFT_LAYOUT = "Shift Layout...";

  @Override
  public String getLabel() {
    return SHIFT_LAYOUT;
  }

  @Override
  public DepanFxResourceFilter getResourceFilter() {
    return DepanFxShiftLayoutToolDialog.SHIFT_LAYOUT_RSRC_FILTER;
  }

  @Override
  public Map<GraphNode, DepanFxNodeLocationData> layoutNodes(
      DepanFxNodeViewPanel view,
      DepanFxWorkspaceResource<?> layoutRsrc,
      List<GraphNode> updateNodes) {
    DepanFxShiftLayoutData shiftData =
        (DepanFxShiftLayoutData) layoutRsrc.getResource();

    Map<GraphNode, DepanFxNodeLocationData> startLocations =
        view.getNodeLocations(updateNodes.stream());

    return buildNodeLocations(startLocations, shiftData);
  }

  @Override
  public void handleLayout(ActionEvent e, DepanFxNodeViewPanel view) {
    DepanFxShiftLayoutData initialData =
        new DepanFxShiftLayoutData(
            "Shift layout", "Shift nodes by indicated amounts.",
            0.0d, 0.0d, 0.0d);

    Dialog<DepanFxShiftLayoutToolDialog> layoutDlg =
        DepanFxShiftLayoutToolDialog.runCreateDialog(
            initialData, view.getDialogRunner());

    List<GraphNode> updateNodes =
        view.streamChosenNodes().collect(Collectors.toList());

    layoutDlg.getController().getWorkspaceResource()
        .ifPresent(r -> view.updateNodeLocations(
            layoutNodes(view, r, updateNodes)));
  }

  private Map<GraphNode, DepanFxNodeLocationData> buildNodeLocations(
      Map<GraphNode, DepanFxNodeLocationData> nodeLocations,
      DepanFxShiftLayoutData shiftData) {

    Map<GraphNode, DepanFxNodeLocationData> result =
        new HashMap<>(nodeLocations.size());

    nodeLocations.forEach(
        (k, v) -> result.put(k, shiftNodeLocation(v, shiftData)));

    return result;
  }

  private DepanFxNodeLocationData shiftNodeLocation(
      DepanFxNodeLocationData currLocation, DepanFxShiftLayoutData shiftData) {
    return new DepanFxNodeLocationData(
        currLocation.xPos + shiftData.getShiftX(),
        currLocation.yPos + shiftData.getShiftY(),
        currLocation.zPos + shiftData.getShiftZ());
  }
}
