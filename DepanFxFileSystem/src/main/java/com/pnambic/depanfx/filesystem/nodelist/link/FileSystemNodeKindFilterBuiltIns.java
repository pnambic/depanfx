package com.pnambic.depanfx.filesystem.nodelist.link;

import com.pnambic.depanfx.filesystem.context.FileSystemContextDefinition;
import com.pnambic.depanfx.graph.context.ContextNodeKindId;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxNodeKindFilterData;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class FileSystemNodeKindFilterBuiltIns {

  public static final String FILE_SYSTEM_DIR = "File System";

  public static final String DIRECTORY_NAME = "Directory";

  public static final String DOCUMENT_NAME = "Document";

  public static final Path FILE_SYSTEM_NODE_FILTERS_PATH =
      DepanFxNodeKindFilterData.NODE_FILTERS_TOOL_PATH
          .resolve(FILE_SYSTEM_DIR);

  public static final Path FILE_SYSTEM_DIRECTORY_FILTER_PATH =
      FILE_SYSTEM_NODE_FILTERS_PATH.resolve(DIRECTORY_NAME);

  public static final Path FILE_SYSTEM_DOCUMENT_FILTER_PATH =
      FILE_SYSTEM_NODE_FILTERS_PATH.resolve(DOCUMENT_NAME);

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeKindFilterData>
      directoryNodeKindFilter() {

    return createBuiltIn(
        DIRECTORY_NAME,
        FileSystemContextDefinition.DIRECTORY_NKID,
        FILE_SYSTEM_DIRECTORY_FILTER_PATH);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeKindFilterData>
      documentNodeKindFilter() {

    return createBuiltIn(
        DOCUMENT_NAME, FileSystemContextDefinition.DOCUMENT_NKID,
        FILE_SYSTEM_DOCUMENT_FILTER_PATH);
  }

  private DepanFxBuiltInContribution<DepanFxNodeKindFilterData> createBuiltIn(
      String docName, ContextNodeKindId kindId, Path docPath) {
    DepanFxNodeKindFilterData kindFilter =
        DepanFxNodeKindFilterData.createMatcherFilterData(kindId);
    return new DepanFxBuiltInContribution.Simple<>(docPath, kindFilter);
  }
}
