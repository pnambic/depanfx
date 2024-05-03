package com.pnambic.depanfx.nodeview.jogl;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.jogl.JoglColor;
import com.pnambic.depanfx.jogl.JoglShape;
import com.pnambic.depanfx.jogl.shapes.NodeShape;
import com.pnambic.depanfx.nodeview.gui.DepanFxJoglView;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxJoglColor;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;

import java.util.Optional;

import javafx.scene.paint.Color;

/**
 * Hold references to shape entities from the {@code Automatic-Module-Name}
 * module{@code depanfx.jogl}.  This avoids chatty but unhelpful warning
 * messages in most of the IDE.
 */
public class JoglShapes {

  public static void installShape(
      DepanFxJoglView view, GraphNode node,
      DepanFxNodeLocationData location, DepanFxNodeDisplayData display) {
    createShape(node, location, display)
        .ifPresent(s -> view.updateShape(node, s));
  }

  public static void updateLocation(
      DepanFxJoglView view, GraphNode node,
      DepanFxNodeLocationData location) {
    JoglShape joglShape = view.getShape(node);
    if (joglShape instanceof NodeShape nodeShape) {
      nodeShape.targetX = location.xPos;
      nodeShape.targetY = location.yPos;
      nodeShape.targetZ = location.zPos;
      view.updateShape(node, nodeShape);
    }
  }

  public static void updateSelection(
      DepanFxJoglView view, GraphNode node, boolean isSelected) {
    JoglShape joglShape = view.getShape(node);
    if (joglShape instanceof NodeShape nodeShape) {
      updateNodeSelection(nodeShape, isSelected);
      view.updateShape(node, nodeShape);
    }
  }

  private static void updateNodeSelection(
      NodeShape nodeShape, boolean isSelected) {
    if (isSelected) {
      nodeShape.borderColor = highlightColor(nodeShape.fillColor);
      nodeShape.borderWidth = 5.0f;
      return;
    }
    nodeShape.borderColor = nodeShape.fillColor;
    nodeShape.borderWidth = 1.0f;
  }

  private static Optional<JoglShape> createShape(
      GraphNode node, DepanFxNodeLocationData location, DepanFxNodeDisplayData display) {

    DepanFxJoglColor viewColor = display.color;
    JoglColor joglColor = JoglColors.toJogl(viewColor);

    String nodeName = guessName(node);
    return Optional.of(new NodeShape(
        joglColor, joglColor, 1.0f,
        location.xPos, location.yPos, location.zPos,
        true, nodeName));
  }

  private static String guessName(GraphNode node) {
    // This should reflect the name request to the contextModel.
    String nodeKey = node.getId().getNodeKey();
    String[] nameWords = nodeKey.split("[./\\\\]");
    int lastSplit = nameWords.length - 1;
    if (lastSplit > 0) {
      return nameWords[lastSplit - 1];
    }
    return nameWords[lastSplit];
  }

  private static JoglColor highlightColor(JoglColor joglColor) {
    DepanFxJoglColor viewColor =
        new DepanFxJoglColor(joglColor.red, joglColor.green, joglColor.blue);
    Color sysColor = JoglColors.of(viewColor).brighter().brighter();
    return JoglColors.toJogl(JoglColors.of(sysColor));
  }
}
