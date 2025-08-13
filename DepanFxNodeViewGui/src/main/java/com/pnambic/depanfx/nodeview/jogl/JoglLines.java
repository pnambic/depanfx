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
package com.pnambic.depanfx.nodeview.jogl;

import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLink;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.jogl.JoglColor;
import com.pnambic.depanfx.jogl.shapes.LineShape;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxJoglColor;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineArrow;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineForm;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineLabel;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineStyle;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData.LinkDisplayEntry;

/**
 * Hold references to line entities from the {@code Automatic-Module-Name}
 * module{@code depanfx.jogl}.  This avoids chatty but unhelpful warning
 * messages in most of the IDE.
 */
public class JoglLines {

  /**
   * For edges matched to a link, use the link to define source and target.
   */
  public static void installLine(
      JoglPane joglView, GraphEdge edge,
      DepanFxLink link, LinkDisplayEntry display, boolean isVisible) {
    LineShape shape = buildLineShape(
        link.getSource(), link.getTarget(),
        toLabel(display), display.getLineDisplay(), isVisible);
    joglView.updateShape(edge, shape);
  }

  public static void installEdge(
      JoglPane joglView, GraphEdge edge,
      String lineLabel, DepanFxLineDisplayData lineInfo, boolean isVisible) {
    LineShape shape = buildLineShape(
        edge.getHead(), edge.getTail(), lineLabel, lineInfo, isVisible);
    joglView.updateShape(edge, shape);
  }

  public static void updateLine(
      JoglPane joglView, GraphEdge edge,
      DepanFxLink link, LinkDisplayEntry display) {
    LineShape priorShape = (LineShape) joglView.getShape(edge);
    LineShape updateShape = buildLineShape(
        link.getSource(), link.getTarget(),
        toLabel(display), display.getLineDisplay(),
        priorShape.isVisible);
    joglView.updateShape(edge, updateShape);
  }

  public static void setEdgeVisible(
      JoglPane joglView, GraphEdge edge, boolean isVisible) {
    LineShape shape = (LineShape) joglView.getShape(edge);
    shape.isVisible = isVisible;
    joglView.updateShape(edge, shape);
  }

  private static LineShape buildLineShape(
      GraphNode lineSource, GraphNode lineTarget,
      String lineLabel,  DepanFxLineDisplayData lineInfo,
      boolean isVisible) {
    LineShape result = new LineShape();

    // Use visibility from node view panel
    result.isVisible = isVisible;

    switch (lineInfo.lineDir) {
    case FORWARD:
      result.lineSource = lineSource;
      result.lineTarget = lineTarget;
      break;
    case REVERSE:
      result.lineSource = lineTarget;
      result.lineTarget = lineSource;
    }

    result.lineForm = toForm(lineInfo.lineForm);
    result.lineStyle = toStyle(lineInfo.lineStyle);
    result.lineColor = toColor(lineInfo.lineColor);
    result.lineWidth = lineInfo.lineWidth;

    result.lineLabel = lineLabel;

    result.sourceArrow = toArrow(lineInfo.sourceArrow);
    result.targetArrow = toArrow(lineInfo.targetArrow);
    return result;
  }

  private static JoglColor toColor(DepanFxJoglColor lineColor) {
    return new JoglColor(
        lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue());
  }

  private static String toLabel(LinkDisplayEntry display) {
    if (DepanFxLineLabel.LABEL.equals(display.getLineDisplay().lineLabel)) {
      return display.getLinkLabel();
    }
    return "";
  }

  private static LineShape.Arrow toArrow(DepanFxLineArrow arrow) {
    switch (arrow) {
    case ARTISTIC:
      return LineShape.Arrow.ARTISTIC;
    case CHEVRON:
      return LineShape.Arrow.CHEVRON;
    case FILLED:
      return LineShape.Arrow.FILLED;
    case NONE:
      return LineShape.Arrow.NONE;
    case OPEN:
      return LineShape.Arrow.OPEN;
    case TRIANGLE:
      return LineShape.Arrow.TRIANGLE;
    }
    return LineShape.Arrow.NONE;
  }

  private static LineShape.Form toForm(DepanFxLineForm form) {
    switch (form) {
    case ARCED:
      return LineShape.Form.ARCED;
    case STRAIGHT:
      return LineShape.Form.STRAIGHT;
    }
    return LineShape.Form.DEFAULT;
  }

  private static LineShape.Style toStyle(DepanFxLineStyle style) {
    switch (style) {
    case DASHED:
      return LineShape.Style.DASHED;
    case DOUBLE_DASH:
      return LineShape.Style.DOUBLE_DASH;
    case SOLID:
      return LineShape.Style.SOLID;
    }
    return LineShape.Style.DEFAULT;
  }
}
