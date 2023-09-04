package com.pnambic.depanfx.graph.context;

public class ContextModelTools {

  private ContextModelTools() {
    // Prevent instantiation
  }

  public static String buildNodeKindTag(ContextNodeKindId nodeKindId) {
    StringBuilder tag = new StringBuilder();
    tag.append(nodeKindId.getContextModelId().getContextModelKey());
    tag.append(":");
    tag.append(nodeKindId.getNodeKindKey());
    return tag.toString();
  }
}
