package com.pnambic.depanfx.nodeview.jogl;

import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.jogl.JoglColor;
import com.pnambic.depanfx.jogl.JoglShape;
import com.pnambic.depanfx.jogl.shapes.LineShape;
import com.pnambic.depanfx.jogl.shapes.LineShape.Arrow;
import com.pnambic.depanfx.jogl.shapes.LineShape.Form;
import com.pnambic.depanfx.jogl.shapes.LineShape.Style;
import com.pnambic.depanfx.nodelist.link.DepanFxLink;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.nodeview.gui.DepanFxJoglView;
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

  public static void installLine(
      DepanFxJoglView result, GraphEdge edge, LinkDisplayEntry display) {

    // Use info from link matcher
    DepanFxLinkMatcherDocument matcher =
        (DepanFxLinkMatcherDocument) display.getLinkRsrc().getResource();

    matcher.getMatcher().match(edge)
        .map(l -> buildLineShape(l, display))
        .ifPresent(s -> result.updateShape(edge, s));
  }

  public static LineShape buildLineShape(
      DepanFxLink link, LinkDisplayEntry display) {
    LineShape result = new LineShape();

    // Use visibility from node view panel
    result.isVisible = true;

    DepanFxLineDisplayData lineInfo = display.getLineDisplay();
    switch (lineInfo.lineDir) {
    case FORWARD:
      result.lineSource = link.getSource();
      result.lineTarget = link.getTarget();
    case REVERSE:
      result.lineSource = link.getTarget();
      result.lineTarget = link.getSource();
    }

    result.lineForm = toForm(lineInfo.lineForm);
    result.lineStyle = toStyle(lineInfo.lineStyle);
    result.lineColor = toColor(lineInfo.lineColor);
    result.lineWidth = lineInfo.lineWidth;

    result.lineLabel = toLabel(display);

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
