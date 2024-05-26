package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeKeyColumnConfiguration;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSectionConfiguration;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxBaseColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxBaseSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInProject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DepanFxNodeListConfiguration {

  public static final Path FLAT_TABLE_VIEW_PATH =
      DepanFxNodeListTableViewData.TABLE_VIEW_TOOL_PATH
          .resolve("Flat Table View");

  public static final Path MEMBER_TABLE_VIEW_PATH =
      DepanFxNodeListTableViewData.TABLE_VIEW_TOOL_PATH
          .resolve("Member Table View");

  @Autowired
  public DepanFxNodeListConfiguration() {
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeListTableViewData>
      flatTableView() {

    return new DepanFxBuiltInContribution.Dependent<DepanFxNodeListTableViewData>(
        FLAT_TABLE_VIEW_PATH) {

      @Override
      protected DepanFxNodeListTableViewData buildDocument(
          DepanFxBuiltInProject project) {

        // Flat Section
        List<DepanFxWorkspaceResource<? extends DepanFxBaseSectionData>>
            sectionRsrcs = new ArrayList<>();
        sectionRsrcs.add(getResource(project,
            DepanFxNodeListSectionData.SIMPLE_SECTION_TOOL_PATH));

        // Node Kind Column
        List<DepanFxWorkspaceResource<? extends DepanFxBaseColumnData>>
            columnRsrcs = new ArrayList<>();
        columnRsrcs.add(getResource(project,
            DepanFxNodeKeyColumnConfiguration.KIND_KEY_COLUMN_TOOL_PATH));

        return new DepanFxNodeListTableViewData(
            "Flat Table View", "Flat node list table view",
            sectionRsrcs, columnRsrcs);
      }
    };
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeListTableViewData>
      memberTableView() {

    return new DepanFxBuiltInContribution.Dependent<DepanFxNodeListTableViewData>(
        MEMBER_TABLE_VIEW_PATH) {

      @Override
      protected DepanFxNodeListTableViewData buildDocument(
          DepanFxBuiltInProject project) {

        // Flat and Members Sections
        List<DepanFxWorkspaceResource<? extends DepanFxBaseSectionData>>
            sectionRsrcs = new ArrayList<>();
        sectionRsrcs.add(getResource(project,
            DepanFxNodeListSectionConfiguration.MEMBER_TREE_SECTION_PATH));
        sectionRsrcs.add(getResource(project,
            DepanFxNodeListSectionData.SIMPLE_SECTION_TOOL_PATH));

        // Node Kind Column
        List<DepanFxWorkspaceResource<? extends DepanFxBaseColumnData>>
            columnRsrcs = new ArrayList<>();
        columnRsrcs.add(getResource(project,
            DepanFxNodeKeyColumnConfiguration.KIND_KEY_COLUMN_TOOL_PATH));

        return new DepanFxNodeListTableViewData(
            "Member Table View", "Table view membership",
            sectionRsrcs, columnRsrcs);
      }
    };
  }
}
