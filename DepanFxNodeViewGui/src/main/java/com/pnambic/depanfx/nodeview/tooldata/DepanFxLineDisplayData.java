package com.pnambic.depanfx.nodeview.tooldata;

import com.pnambic.depanfx.graph.info.GraphEdgeInfo;

import javafx.scene.paint.Color;

public class DepanFxLineDisplayData implements GraphEdgeInfo {

  public DepanFxLineForm lineForm;

  public DepanFxLineStyle lineStyle;

  public DepanFxJoglColor lineColor;

  public double lineWidth;

  public DepanFxLineLabel lineLabel;
  /**
   * Typically empty, maybe an "push" arrow for some edges.
   */
  public DepanFxLineArrow sourceArrow;

  /**
   * Typically an receiving arrow.
   */
  public DepanFxLineArrow targetArrow;

  public DepanFxLineDirection lineDir;

  public DepanFxLineDisplayData(
      DepanFxLineForm lineForm, DepanFxLineStyle lineStyle,
      DepanFxJoglColor lineColor, double lineWidth, DepanFxLineLabel lineLabel,
      DepanFxLineArrow sourceArrow, DepanFxLineArrow targetArrow,
      DepanFxLineDirection lineDir) {
    this.lineForm = lineForm;
    this.lineStyle = lineStyle;
    this.lineColor = lineColor;
    this.lineWidth = lineWidth;
    this.lineLabel = lineLabel;
    this.sourceArrow = sourceArrow;
    this.targetArrow = targetArrow;
    this.lineDir = lineDir;
  }

  public static DepanFxLineDisplayData buildSimpleLineDisplayData() {
    return new DepanFxLineDisplayData(
        DepanFxLineForm.DEFAULT,
        DepanFxLineStyle.DEFAULT,
        DepanFxJoglColor.of(Color.BLACK),
        1.0d /* width*/,
        DepanFxLineLabel.DEFAULT,
        DepanFxLineArrow.SOURCE_DEFAULT,
        DepanFxLineArrow.TARGET_DEFAULT,
        DepanFxLineDirection.DEFAULT);
  }
}
