package com.pnambic.depanfx.nodelist.link;

import com.pnambic.depanfx.graph.context.BaseContextDefinition;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.Collections;

@Configuration
public class DepanFxLinkMatcherBuiltIns {

  public static final String MATCH_ALL_MATCHER_LABEL = "All Edges";

  public static final String MATCH_ALL_MATCHER_DESCR = "All Edges";

  public static final String MATCH_ALL_DOC_NAME = "All Edges";

  public static final Path MATCH_ALL_DOC_PATH =
      DepanFxLinkMatcherDocument.LINK_MATCHER_TOOL_PATH
          .resolve(MATCH_ALL_DOC_NAME);

  private final DepanFxLinkMatcherDocument allEdgeMatcherDoc =
      buildAllEdgeMatcher();

  public DepanFxLinkMatcherBuiltIns() {
  }

  @Bean
  public DepanFxBuiltInContribution allEdgeMatcher() {
    return new DepanFxBuiltInContribution.Simple(
        MATCH_ALL_DOC_PATH, allEdgeMatcherDoc);
  }

  private DepanFxLinkMatcherDocument buildAllEdgeMatcher() {
    return new DepanFxLinkMatcherDocument(
          MATCH_ALL_MATCHER_LABEL,
          MATCH_ALL_MATCHER_DESCR,
          BaseContextDefinition.MODEL_ID,
          Collections.emptyList(),
          DepanFxLinkMatchers.ALL_EDGES_FORWARD);
  }
}
