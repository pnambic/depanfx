package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.graph.context.BaseContextDefinition;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherBuiltIns;
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

@Configuration
public class DepanFxNodeViewLinkDisplayDataBuiltIns {

  private static final String ALL_EDGES_LABEL = "All Edges";

  private static final String ALL_EDGES_DESCR = "All Edges";

  private static final String ALL_EDGES_DOC_NAME = "All Edges";

  public static final Path ALL_EDGES_DOC_PATH =
      DepanFxNodeViewData.NODE_VIEW_TOOL_PATH
          .resolve(ALL_EDGES_DOC_NAME);

  @Autowired
  public DepanFxNodeViewLinkDisplayDataBuiltIns() {
  }

  @Bean
  public DepanFxBuiltInContribution allEdgeLinkDisplayDoc() {
    return new DepanFxBuiltInContribution.Dependent(ALL_EDGES_DOC_PATH) {

      @Override
      protected Object buildDocument(DepanFxBuiltInProject project) {
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

    DepanFxWorkspaceResource allEdgeMatcher =
        project.getProjectTree()
            .asProjectDocument(DepanFxLinkMatcherBuiltIns.MATCH_ALL_DOC_PATH)
            .flatMap(project::getResource)
        .orElseThrow(() ->
            new DepanFxBuiltInContribution.MissingDependencyException(
                ALL_EDGES_DOC_PATH,
                DepanFxLinkMatcherBuiltIns.MATCH_ALL_DOC_PATH));

    LinkDisplayEntry linkDisplayEntry =
        new LinkDisplayEntry(ALL_EDGES_LABEL, allEdgeMatcher, lineDisplayData);

    DepanFxNodeViewLinkDisplayData result =
        new DepanFxNodeViewLinkDisplayData(
            ALL_EDGES_LABEL, ALL_EDGES_DESCR,
            BaseContextDefinition.MODEL_ID,
            Collections.singletonList(linkDisplayEntry));
    return result;
  };
}
