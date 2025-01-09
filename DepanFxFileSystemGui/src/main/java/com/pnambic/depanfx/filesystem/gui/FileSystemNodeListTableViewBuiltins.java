package com.pnambic.depanfx.filesystem.gui;

import com.pnambic.depanfx.filesystem.context.FileSystemContextModelId;
import com.pnambic.depanfx.nodelist.builtins.DepanFxNodeKeyColumnBuiltIns;
import com.pnambic.depanfx.nodelist.builtins.DepanFxNodeListSectionBuiltIns;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxBaseColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxBaseSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInProject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class FileSystemNodeListTableViewBuiltins {

  public static final Path FILE_SYSTEM_TABLE_VIEW_PATH =
      DepanFxNodeListTableViewData.TABLE_VIEW_TOOL_PATH
          .resolve(FileSystemContextModelId.FILE_SYSTEM_KEY);

  public static final String FILE_SYSTEM_HIERARY_TABLE_VIEW_NAME =
      "File System Hierarchy";

  public static final Path FILE_SYSTEM_HIERARY_TABLE_VIEW_PATH =
      FILE_SYSTEM_TABLE_VIEW_PATH.resolve(FILE_SYSTEM_HIERARY_TABLE_VIEW_NAME);

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeListTableViewData>
      fileSystemHierarchyTableView() {

    return new DepanFxBuiltInContribution.Dependent<DepanFxNodeListTableViewData>(
        FILE_SYSTEM_HIERARY_TABLE_VIEW_PATH) {

      @Override
      protected DepanFxNodeListTableViewData buildDocument(
          DepanFxBuiltInProject project) {

        // Flat and Members Sections
        List<DepanFxWorkspaceResource<? extends DepanFxBaseSectionData>>
            sectionRsrcs = new ArrayList<>();
        sectionRsrcs.add(getResource(project,
            FileSystemNodeListSectionBuiltIns.FILE_SYSTEM_HIERARCHY_SECTION_PATH));
        sectionRsrcs.add(getResource(project,
            DepanFxNodeListSectionBuiltIns.SIMPLE_SECTION_TOOL_PATH));

        // Node Kind Column
        List<DepanFxWorkspaceResource<? extends DepanFxBaseColumnData>>
            columnRsrcs = new ArrayList<>();
        columnRsrcs.add(getResource(project,
            DepanFxNodeKeyColumnBuiltIns.KIND_KEY_COLUMN_TOOL_PATH));

        return new DepanFxNodeListTableViewData(
            "Member Table View", "Table view membership",
            sectionRsrcs, columnRsrcs);
      }
    };
  }
}
