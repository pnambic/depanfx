package com.pnambic.depanfx.nodelist.link;

import com.pnambic.depanfx.graph.context.BaseContextDefinition;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;

import org.springframework.context.annotation.Bean;

import java.nio.file.Path;
import java.util.Collections;

public class DepanFxLinkMatcherBuiltIns {

  private static final String MATCH_ALL_DOC_NAME = "All Edges";

  private DepanFxLinkMatcherBuiltIns() {
    // Prevent instantiation
  }

  @Bean
  public DepanFxBuiltInContribution allEdgeMatcher() {
    DepanFxLinkMatcherDocument allEdgeMatcherDoc =
        new DepanFxLinkMatcherDocument(
            BaseContextDefinition.MODEL_ID,
            Collections.emptyList(),
            DepanFxLinkMatchers.ALL_EDGES_FORWARD);
    return createBuiltIn(MATCH_ALL_DOC_NAME, allEdgeMatcherDoc);
  }

  private DepanFxBuiltInContribution createBuiltIn(
      String docName, DepanFxLinkMatcherDocument doc) {
    Path docPath = DepanFxLinkMatcherDocument.LINK_MATCHER_TOOL_PATH
        .resolve(docName);
    return new DepanFxBuiltInContribution.Simple(docPath, doc);
  }
}
