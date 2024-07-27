package com.pnambic.depanfx.nodeview.tooldata;

import com.pnambic.depanfx.graph.info.GraphNodeInfo;

import javafx.scene.paint.Color;


public class DepanFxNodeDisplayData implements GraphNodeInfo {

  public boolean isVisible;

  public DepanFxSizerModel nodeSizer;

  public DepanFxJoglColor color;

  public DepanFxNodeDisplayData(
      boolean isVisible, DepanFxSizerModel nodeSizer, DepanFxJoglColor color) {
    this.isVisible = isVisible;
    this.nodeSizer = nodeSizer;
    this.color = color;
  }

  public static DepanFxNodeDisplayData buildSimpleNodeDisplayData() {
    DepanFxJoglColor nodeColor = DepanFxJoglColor.of(Color.BLUE);
    return new DepanFxNodeDisplayData(true, DepanFxSizerModel.FIXED, nodeColor);
  }
}
