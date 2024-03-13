package com.pnambic.depanfx.nodeview.jogl;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.jogl.JoglShape;
import com.pnambic.depanfx.jogl.shapes.NodeShape;
import com.pnambic.depanfx.nodeview.gui.DepanFxJoglView;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxJoglColor;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;

import java.util.Optional;

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

  private static Optional<JoglShape> createShape(
      GraphNode node, DepanFxNodeLocationData location, DepanFxNodeDisplayData display) {

    DepanFxJoglColor color = display.color;
    String nodeName = guessName(node);
    return Optional.of(new NodeShape(
        color.getRed(), color.getGreen(), color.getBlue(),
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
}
