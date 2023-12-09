package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherGroup;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxFlatSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderBy;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderDirection;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxTreeSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxTreeSectionData.ContainerOrder;
import com.pnambic.depanfx.workspace.DepanFxProjectResource;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class DepanFxNodeListSectionConfiguration {

  private final DepanFxBuiltInContribution memberFinderlinkMatcherContrib =
      buildMemberFinderLinkMatcher();

  @Autowired
  public DepanFxNodeListSectionConfiguration() {
  }

  @Bean
  public DepanFxBuiltInContribution memberTreeSection() {
    DepanFxTreeSectionData toolData = buildInitialTreeSection(
        "Member Tree Section",
        "Tree section based on a link matcher for membership");
    return createBuiltIn("Member Tree", toolData);
  }

  @Bean
  public DepanFxBuiltInContribution flatSection() {
    DepanFxFlatSectionData toolData =
        new DepanFxFlatSectionData(
            "Built-in Flat Section", "Built-in flat section.",
            DepanFxFlatSectionData.BASE_SECTION_LABEL, true,
            OrderBy.NODE_KEY, OrderDirection.FORWARD);
    return new DepanFxBuiltInContribution.Simple(
        DepanFxNodeListSectionData.SIMPLE_SECTION_TOOL_PATH, toolData);
  }

  @Bean
  public DepanFxBuiltInContribution memberFinderLinkMatcher() {
    return memberFinderlinkMatcherContrib;
  }

  public DepanFxTreeSectionData buildInitialTreeSection(
      String toolName, String toolDescription) {
    return new DepanFxTreeSectionData(
        toolName, toolDescription,
        "Tree", true,
        new DepanFxProjectResource.BuiltIn(
            memberFinderlinkMatcherContrib.getPath()), true,
        OrderBy.NODE_LEAF, ContainerOrder.LAST, OrderDirection.FORWARD);

  }

  private DepanFxBuiltInContribution buildMemberFinderLinkMatcher() {
    Path docPath = DepanFxLinkMatcherDocument.LINK_MATCHER_TOOL_PATH
        .resolve("Tree Member");
    DepanFxLinkMatcherDocument finderMatcher =
        new DepanFxLinkMatcherDocument(
            null, DepanFxLinkMatcherGroup.MEMBER_MATCHER_GROUP, null);
    return new DepanFxBuiltInContribution.Simple(docPath, finderMatcher);
  }

  private DepanFxBuiltInContribution createBuiltIn(
      String docName, Object doc) {
    Path docPath =
        DepanFxNodeListSectionData.SECTIONS_TOOL_PATH.resolve(docName);
    return new DepanFxBuiltInContribution.Simple(docPath, doc);
  }
}
