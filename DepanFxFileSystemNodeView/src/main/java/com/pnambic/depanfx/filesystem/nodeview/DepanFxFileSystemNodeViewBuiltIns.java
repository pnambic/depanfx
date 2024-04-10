package com.pnambic.depanfx.filesystem.nodeview;

import com.pnambic.depanfx.filesystem.nodelist.link.FileSystemLinkMatcherBuiltIns;
import com.pnambic.depanfx.graph.context.BaseContextDefinition;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxJoglColor;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineArrow;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineDirection;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineForm;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineLabel;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineStyle;
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
import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;

@Configuration
public class DepanFxFileSystemNodeViewBuiltIns {

  private static final String MEMBERS_LABEL = "File System Member";

  private static final String MEMBERS_DESCR =
      "Files system edges, separated by relation type";

  private static final String MEMBERS_NAME = "File System Members";

  public static final Path NODE_VIEW_FILE_SYSTEM_MEMBERS_DOC_PATH =
      DepanFxNodeViewData.NODE_VIEW_TOOL_PATH
          .resolve(MEMBERS_NAME);

  @Autowired
  public DepanFxFileSystemNodeViewBuiltIns() {
  }

  @Bean
  public DepanFxBuiltInContribution fileSystemMembersEdgeLinkDisplayDoc() {
    return new DepanFxBuiltInContribution.Dependent(
        NODE_VIEW_FILE_SYSTEM_MEMBERS_DOC_PATH) {

      @Override
      protected Object buildDocument(DepanFxBuiltInProject project) {
        return buildFileSystemLinkDisplayData(project);
      }
    };
  }

  /**
   * Now that we have an established project for built ins,
   * build the all edges display built in using the all edges matcher built in.
   */
  private static DepanFxNodeViewLinkDisplayData buildFileSystemLinkDisplayData(
      DepanFxBuiltInProject project) {

    DepanFxLineDisplayData directoryLine = new DepanFxLineDisplayData(
        DepanFxLineForm.STRAIGHT,
        DepanFxLineStyle.SOLID,
        DepanFxJoglColor.of(Color.GREEN),
        1.5d,  // width
        DepanFxLineLabel.DEFAULT,
        DepanFxLineArrow.NONE,
        DepanFxLineArrow.OPEN,
        DepanFxLineDirection.FORWARD);
    LinkDisplayEntry directoryDisplayEntry =
        buildDisplayEntry(project,
            NODE_VIEW_FILE_SYSTEM_MEMBERS_DOC_PATH,
            FileSystemLinkMatcherBuiltIns.DIRECTORY_NAME,
            FileSystemLinkMatcherBuiltIns.FILE_SYSTEM_DIRECTORY_MATCHER_PATH,
            directoryLine);

    DepanFxLineDisplayData fileLine = new DepanFxLineDisplayData(
        DepanFxLineForm.STRAIGHT,
        DepanFxLineStyle.SOLID,
        DepanFxJoglColor.of(Color.YELLOW),
        1.0d,  // width
        DepanFxLineLabel.DEFAULT,
        DepanFxLineArrow.NONE,
        DepanFxLineArrow.OPEN,
        DepanFxLineDirection.FORWARD);
    LinkDisplayEntry fileDisplayEntry =
        buildDisplayEntry(project,
            NODE_VIEW_FILE_SYSTEM_MEMBERS_DOC_PATH,
            FileSystemLinkMatcherBuiltIns.FILE_NAME,
            FileSystemLinkMatcherBuiltIns.FILE_SYSTEM_FILE_MATCHER_PATH,
            fileLine);

    List<LinkDisplayEntry> displayInfo = new ArrayList<>();
    displayInfo.add(fileDisplayEntry);
    displayInfo.add(directoryDisplayEntry);

    DepanFxNodeViewLinkDisplayData result =
        new DepanFxNodeViewLinkDisplayData(
            MEMBERS_LABEL, MEMBERS_DESCR,
            BaseContextDefinition.MODEL_ID, displayInfo);
    return result;
  };

  private static LinkDisplayEntry buildDisplayEntry(
      DepanFxBuiltInProject project, Path displayDataPath,
      String entryName, Path matcherPath, DepanFxLineDisplayData lineDisplay) {
    DepanFxWorkspaceResource directoryMatcher =
        project.getProjectTree()
            .asProjectDocument(matcherPath)
            .flatMap(project::getResource)
        .orElseThrow(() ->
            new DepanFxBuiltInContribution.MissingDependencyException(
                displayDataPath, matcherPath));

    return new LinkDisplayEntry(
        entryName, directoryMatcher, lineDisplay);
  }
}
