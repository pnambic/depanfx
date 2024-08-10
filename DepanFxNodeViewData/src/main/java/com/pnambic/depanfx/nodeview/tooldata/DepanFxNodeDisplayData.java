package com.pnambic.depanfx.nodeview.tooldata;

import com.pnambic.depanfx.graph.info.GraphNodeInfo;

import javafx.scene.paint.Color;


public class DepanFxNodeDisplayData implements GraphNodeInfo {

  public boolean isVisible;

  public DepanFxJoglShape nodeShape;

  public DepanFxSizerModel nodeSizer;

  public DepanFxJoglColor fillColor;

  public DepanFxJoglColor borderColor;

  public DepanFxJoglColor highlightColor;

  public DepanFxNodeDisplayData(
      boolean isVisible,
      DepanFxJoglShape nodeShape, DepanFxSizerModel nodeSizer,
      DepanFxJoglColor fillColor, DepanFxJoglColor borderColor,
      DepanFxJoglColor highlightColor) {
    this.isVisible = isVisible;
    this.nodeShape = nodeShape;
    this.nodeSizer = nodeSizer;
    this.fillColor = fillColor;
    this.borderColor = borderColor;
    this.highlightColor = highlightColor;
  }

  public static DepanFxNodeDisplayData buildSimpleNodeDisplayData() {
    return buildSimpleNodeDisplayData(
        DepanFxJoglShape.SQUARE, DepanFxJoglColor.of(Color.BLUE));
  }

  public static DepanFxNodeDisplayData buildSimpleNodeDisplayData(
      DepanFxJoglShape nodeShape, DepanFxJoglColor nodeColor) {
    return new DepanFxNodeDisplayData(
        true, nodeShape, DepanFxSizerModel.FIXED,
        nodeColor, nodeColor.shift(0.7d), nodeColor.complement());
  }
}
