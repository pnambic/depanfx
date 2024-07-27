package com.pnambic.depanfx.nodeview.builtins;

import com.pnambic.depanfx.graph.context.BaseContextDefinition;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherBuiltIns;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData.LinkDisplayEntry;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInProject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;

@Configuration
public class DepanFxGraphLinkViewBuiltIns {

  private static final String ALL_EDGES_DOC_NAME = "All Edges Display";

  private static final String ALL_EDGES_NAME= "All Edges";

  private static final String ALL_EDGES_DESCR = "All edges.";

  public static final Path ALL_EDGES_DISPLAY_DOC_PATH =
      DepanFxNodeViewData.NODE_VIEW_TOOL_PATH
          .resolve(ALL_EDGES_DOC_NAME);

  @Autowired
  public DepanFxGraphLinkViewBuiltIns() {
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeViewLinkDisplayData> allEdgeLinkDisplayDoc() {
    return new DepanFxBuiltInContribution.Dependent<>(ALL_EDGES_DISPLAY_DOC_PATH) {

      @Override
      protected DepanFxNodeViewLinkDisplayData buildDocument(
          DepanFxBuiltInProject project) {
        return buildAllEdgesLinkDisplayData(project);
      }
    };
  }

  /**
   * Now that we have an established project for built ins,
   * build the all edges display built in using the all edges matcher built in.
   */
  private static DepanFxNodeViewLinkDisplayData buildAllEdgesLinkDisplayData(
      DepanFxBuiltInProject project) {

    DepanFxLineDisplayData lineDisplayData =
        DepanFxLineDisplayData.buildSimpleLineDisplayData();

    Optional<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
        optMatcherRsrc =
            project.getResource(DepanFxLinkMatcherBuiltIns.MATCH_ALL_DOC_PATH);

    DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> allEdgeMatcher =
        optMatcherRsrc.orElseThrow(() ->
            new DepanFxBuiltInContribution.MissingDependencyException(
                ALL_EDGES_DISPLAY_DOC_PATH,
                DepanFxLinkMatcherBuiltIns.MATCH_ALL_DOC_PATH));

    LinkDisplayEntry linkDisplayEntry =
        new LinkDisplayEntry(ALL_EDGES_NAME, allEdgeMatcher, lineDisplayData);

    DepanFxNodeViewLinkDisplayData result =
        new DepanFxNodeViewLinkDisplayData(
            ALL_EDGES_NAME, ALL_EDGES_DESCR,
            BaseContextDefinition.MODEL_ID,
            Collections.singletonList(linkDisplayEntry));
    return result;
  };
}
