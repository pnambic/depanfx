package com.pnambic.depanfx.nodelist.link;

import com.pnambic.depanfx.graph.model.GraphEdge;

import java.util.Optional;

public interface DepanFxLinkMatcher {
  Optional<DepanFxLink> match(GraphEdge edge);
}
