package com.pnambic.depanfx.nodeview.jogl;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.jogl.JoglColor;
import com.pnambic.depanfx.jogl.JoglShape;
import com.pnambic.depanfx.jogl.shapes.AwtShape;
import com.pnambic.depanfx.jogl.shapes.NodeShape;
import com.pnambic.depanfx.jogl.overlays.NodeOverlay;
import com.pnambic.depanfx.jogl.overlays.NestFoldOverlay;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxJoglShape;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Hold references to shape entities from the {@code Automatic-Module-Name}
 * module{@code depanfx.jogl}.  This avoids chatty but unhelpful warning
 * messages in most of the IDE.
 */
public class JoglShapes {

  public static void installShape(
      JoglPane joglPane, GraphNode node,
      DepanFxNodeLocationData location,
      DepanFxNodeDisplayData display,
      boolean isVisible) {
    createShape(node, location, display, isVisible)
        .ifPresent(s -> joglPane.updateShape(node, s));
  }

  public static void updateLocation(
      JoglPane joglPane, GraphNode node,
      DepanFxNodeLocationData location) {
    JoglShape joglShape = joglPane.getShape(node);
    if (joglShape instanceof NodeShape nodeShape) {
      nodeShape.targetX = location.xPos;
      nodeShape.targetY = location.yPos;
      nodeShape.targetZ = location.zPos;
      joglPane.updateShape(node, nodeShape);
    }
  }

  public static void updateSelection(
      JoglPane joglPane, GraphNode node, boolean isSelected) {
    JoglShape joglShape = joglPane.getShape(node);
    if (joglShape instanceof NodeShape nodeShape) {
      updateNodeSelection(nodeShape, isSelected);
      joglPane.updateShape(node, nodeShape);
    }
  }

  public static void updateDisplay(
      JoglPane joglPane, GraphNode node, DepanFxNodeDisplayData display) {
    JoglShape joglShape = joglPane.getShape(node);
    if (joglShape instanceof NodeShape nodeShape) {
      // test before any changes
      boolean isHighlit = nodeShape.edgeColor == nodeShape.highlightColor;

      nodeShape.shapeColor = JoglColors.toJogl(display.fillColor);
      nodeShape.borderColor = JoglColors.toJogl(display.borderColor);
      nodeShape.highlightColor = JoglColors.toJogl(display.highlightColor);

      // Mimic the current selection rendering for the borders
      if (isHighlit) {
        nodeShape.edgeColor = nodeShape.highlightColor;
      } else {
        nodeShape.edgeColor = nodeShape.borderColor;;
      }

      // Only AWT shapes have a shape
      if (nodeShape instanceof AwtShape awtShape) {
        awtShape.shapeAwt = getShape(display.nodeShape);
      }

      // Do the update
      joglPane.updateShape(node, nodeShape);
    }
  }

  public static void updateVisibility(
      JoglPane joglPane, GraphNode node, boolean isVisible) {
    JoglShape joglShape = joglPane.getShape(node);
    if (joglShape instanceof NodeShape nodeShape) {
      nodeShape.isVisible = isVisible;
      joglPane.updateShape(node, nodeShape);
    }
  }

  public static void updateNodeFolding(
      JoglPane joglPane, GraphNode memberNode, GraphNode nestNode) {
    if (joglPane.getShape(memberNode) instanceof NodeShape memberShape) {
      if (joglPane.getShape(nestNode) instanceof NodeShape nestShape) {
        memberShape.setNodeNestKey(nestNode, nestShape);
        joglPane.updateShape(memberNode, memberShape);
      }
    }
  }

  public static void clearNodeFolding(
      JoglPane joglPane, GraphNode memberNode) {
    if (joglPane.getShape(memberNode) instanceof NodeShape memberShape) {
      memberShape.clearApparentShape();
      joglPane.updateShape(memberNode, memberShape);
    }
  }

  public static void updateNestFoldingState(
      JoglPane joglPane, GraphNode nestNode, boolean isOpen) {
    if (joglPane.getShape(nestNode) instanceof NodeShape nestShape) {
      nestShape.overlays = updateFoldOverlays(nestShape.overlays, isOpen);
      joglPane.updateShape(nestNode, nestShape);
    }
  }

  public static void clearNestFoldingState(
      JoglPane joglPane, GraphNode nestNode, boolean isOpen) {
    if (joglPane.getShape(nestNode) instanceof NodeShape nestShape) {
      nestShape.overlays = clearFoldOverlays(nestShape.overlays);
      joglPane.updateShape(nestNode, nestShape);
    }
  }

  private static void updateNodeSelection(
      NodeShape nodeShape, boolean isSelected) {
    if (isSelected) {
      nodeShape.edgeColor = nodeShape.highlightColor;
      nodeShape.borderWidth = 5.0f;
      return;
    }
    nodeShape.edgeColor = nodeShape.borderColor;
    nodeShape.borderWidth = 1.0f;
  }

  private static Optional<JoglShape> createShape(
      GraphNode node, DepanFxNodeLocationData location,
      DepanFxNodeDisplayData display, boolean isVisible) {

    JoglColor fillColor = JoglColors.toJogl(display.fillColor);
    JoglColor borderColor = JoglColors.toJogl(display.borderColor);
    JoglColor highlightColor = JoglColors.toJogl(display.highlightColor);
    String nodeName = guessName(node);

    return Optional.of(new AwtShape(
        getShape(display.nodeShape), isVisible,
        fillColor, borderColor, highlightColor, 1.0f,
        location.xPos, location.yPos, location.zPos,
        true, nodeName, node));
  }

  private static String guessName(GraphNode node) {
    return node.getId().getSimpleName();
  }

  private static Shape getShape(DepanFxJoglShape joglShape) {
    switch (joglShape) {
    case CIRCLE:
      return JoglShapeKinds.CIRCLE.buildAwtShape();
    case ELLIPSE:
      return JoglShapeKinds.ELLIPSE.buildAwtShape();
    case HEXAGON:
      return JoglShapeKinds.HEXAGON.buildAwtShape();
    case RECTANGLE:
      return JoglShapeKinds.RECTANGLE.buildAwtShape();
    case ROUNDED_RECTANGLE:
      return JoglShapeKinds.ROUNDED_RECTANGLE.buildAwtShape();
    case SQUARE:
      return JoglShapeKinds.SQUARE.buildAwtShape();
    default:
    }
    return JoglShapeKinds.SQUARE.buildAwtShape();
  }


  private static List<NodeOverlay> clearFoldOverlays(
      List<NodeOverlay> overlays) {
    List<NodeOverlay> result = removeFoldOverlays(overlays)
        .collect(Collectors.toList());

    // Minimize the space for empty overlays.
    if (result.isEmpty()) {
      return NodeOverlay.EMPTY_OVERLAYS;
    }
    return result;
  }

  private static List<NodeOverlay> updateFoldOverlays(
      List<NodeOverlay> overlays, boolean isOpen) {
    // Help with type inference.
    NodeOverlay nestOverlay = isOpen
        ? NestFoldOverlay.OPEN_NEST_OVERLAY
        : NestFoldOverlay.SHUT_NEST_OVERLAY;

    return Stream.concat(
        removeFoldOverlays(overlays),
        Stream.of(nestOverlay))
        .collect(Collectors.toList());
  }

  private static Stream<NodeOverlay> removeFoldOverlays(
      List<NodeOverlay> overlays) {
    return overlays.stream()
        .filter(o -> o != NestFoldOverlay.OPEN_NEST_OVERLAY)
        .filter(o -> o != NestFoldOverlay.SHUT_NEST_OVERLAY);
  }
}
