package com.pnambic.depanfx.edgematchers.tooldata;

import com.pnambic.depanfx.graph.model.GraphEdge;

import java.util.Optional;

public interface DepanFxLinkMatcher {
  Optional<DepanFxLink> match(GraphEdge edge);
}
