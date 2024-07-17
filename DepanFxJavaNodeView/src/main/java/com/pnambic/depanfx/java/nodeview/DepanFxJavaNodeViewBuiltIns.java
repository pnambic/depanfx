package com.pnambic.depanfx.java.nodeview;

import com.pnambic.depanfx.filesystem.nodelist.link.FileSystemLinkMatcherBuiltIns;
import com.pnambic.depanfx.java.context.JavaContextDefinition;
import com.pnambic.depanfx.java.nodelist.link.JavaLinkMatcherBuiltIns;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxLinkMatcherDocument;
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
import java.util.Optional;

import javafx.scene.paint.Color;

@Configuration
public class DepanFxJavaNodeViewBuiltIns {

  private static final String MEMBERS_LABEL = "Java Member";

  private static final String MEMBERS_DESCR =
      "Java edges, separated by relation type";

  private static final String MEMBERS_NAME = "Java Members";

  public static final Path NODE_VIEW_JAVA_MEMBERS_DOC_PATH =
      DepanFxNodeViewData.NODE_VIEW_TOOL_PATH
          .resolve(MEMBERS_NAME);

  @Autowired
  public DepanFxJavaNodeViewBuiltIns() {
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeViewLinkDisplayData>
      javaMembersEdgeLinkDisplayDoc() {
    return new DepanFxBuiltInContribution.Dependent<>(
        NODE_VIEW_JAVA_MEMBERS_DOC_PATH) {

      @Override
      protected DepanFxNodeViewLinkDisplayData
          buildDocument(DepanFxBuiltInProject project) {
        return buildJavaLinkDisplayData(project);
      }
    };
  }

  /**
   * Now that we have an established project for built ins,
   * build the all edges display built in using the all edges matcher built in.
   */
  private static DepanFxNodeViewLinkDisplayData buildJavaLinkDisplayData(
      DepanFxBuiltInProject project) {

    DepanFxLineDisplayData directoryLine = buildMemberLine(Color.GREEN, 1.5);
    LinkDisplayEntry directoryDisplayEntry =
        buildDisplayEntry(project,
            NODE_VIEW_JAVA_MEMBERS_DOC_PATH,
            FileSystemLinkMatcherBuiltIns.DIRECTORY_NAME,
            FileSystemLinkMatcherBuiltIns.FILE_SYSTEM_DIRECTORY_MATCHER_PATH,
            directoryLine);

    DepanFxLineDisplayData fileLine = buildMemberLine(Color.YELLOW, 1.0);
    LinkDisplayEntry fileDisplayEntry =
        buildDisplayEntry(project,
            NODE_VIEW_JAVA_MEMBERS_DOC_PATH,
            FileSystemLinkMatcherBuiltIns.FILE_NAME,
            FileSystemLinkMatcherBuiltIns.FILE_SYSTEM_FILE_MATCHER_PATH,
            fileLine);

    List<LinkDisplayEntry> displayInfo = new ArrayList<>();
    displayInfo.add(fileDisplayEntry);
    displayInfo.add(directoryDisplayEntry);

    addMembershipRelations(project, displayInfo);
    addUsesRelations(project, displayInfo);
    addDependsOnRelations(project, displayInfo);
    addMiscellaneousRelations(project, displayInfo);
    addAnnotateRelations(project, displayInfo);
    addToFileSystemRelations(project, displayInfo);
    addModuleRelations(project, displayInfo);

    DepanFxNodeViewLinkDisplayData result =
        new DepanFxNodeViewLinkDisplayData(
            MEMBERS_LABEL, MEMBERS_DESCR,
            JavaContextDefinition.MODEL_ID, displayInfo);
    return result;
  }

  private static void addMembershipRelations(
      DepanFxBuiltInProject project, List<LinkDisplayEntry> displayInfo) {
    displayInfo.add(
        buildDisplayEntry(project,
            JavaLinkMatcherBuiltIns.CLASS_MATCHER_PATH,
            buildMemberLine(Color.YELLOW, 1.5)));

    displayInfo.add(
        buildDisplayEntry(project,
            JavaLinkMatcherBuiltIns.PACKAGE_MATCHER_PATH,
            buildMemberLine(Color.GREEN, 1.0)));

    displayInfo.add(
        buildDisplayEntry(project,
            JavaLinkMatcherBuiltIns.STATIC_FIELD_MATCHER_PATH,
            buildMemberLine(Color.BLUE, 1.0)));

    displayInfo.add(
        buildDisplayEntry(project,
            JavaLinkMatcherBuiltIns.MEMBER_FIELD_MATCHER_PATH,
            buildMemberLine(Color.BLUE, 1.5)));

    displayInfo.add(
        buildDisplayEntry(project,
            JavaLinkMatcherBuiltIns.STATIC_METHOD_MATCHER_PATH,
            buildMemberLine(Color.CORAL, 1.0)));

    displayInfo.add(
        buildDisplayEntry(project,
            JavaLinkMatcherBuiltIns.MEMBER_METHOD_MATCHER_PATH,
            buildMemberLine(Color.CORAL, 1.5)));

    displayInfo.add(
        buildDisplayEntry(project,
            JavaLinkMatcherBuiltIns.INNER_TYPE_MATCHER_PATH,
            buildMemberLine(Color.GOLD, 1.0)));

    displayInfo.add(
        buildDisplayEntry(project,
            JavaLinkMatcherBuiltIns.ANONYMOUS_TYPE_MATCHER_PATH,
            buildMemberLine(Color.GOLD, 1.0)));
  }

  private static void addDependsOnRelations(
      DepanFxBuiltInProject project, List<LinkDisplayEntry> displayInfo) {

    displayInfo.add(
        buildDisplayEntry(project,
            JavaLinkMatcherBuiltIns.EXTENDS_MATCHER_PATH,
            buildMemberLine(Color.BLUE, 1.5)));

    displayInfo.add(
        buildDisplayEntry(project,
            JavaLinkMatcherBuiltIns.IMPLEMENTS_MATCHER_PATH,
            buildMemberLine(Color.DEEPSKYBLUE, 1.5)));
  }

  private static void addUsesRelations(
      DepanFxBuiltInProject project, List<LinkDisplayEntry> displayInfo) {

    displayInfo.add(
        buildDisplayEntry(project,
            JavaLinkMatcherBuiltIns.CALL_MATCHER_PATH,
            buildMemberLine(Color.RED, 1.5)));

    displayInfo.add(
        buildDisplayEntry(project,
            JavaLinkMatcherBuiltIns.READ_MATCHER_PATH,
            buildMemberLine(Color.ORANGERED, 1.0)));

    displayInfo.add(
        buildDisplayEntry(project,
            JavaLinkMatcherBuiltIns.WRITE_MATCHER_PATH,
            buildMemberLine(Color.ORANGERED, 1.5)));
  }

  private static void addAnnotateRelations(
      DepanFxBuiltInProject project, List<LinkDisplayEntry> displayInfo) {

    displayInfo.add(
        buildDisplayEntry(project,
            JavaLinkMatcherBuiltIns.RUNTIME_ANNOTATION_MATCHER_PATH,
            buildMemberLine(Color.PALETURQUOISE, 1.5)));

    displayInfo.add(
        buildDisplayEntry(project,
            JavaLinkMatcherBuiltIns.COMPILE_ANNOTATION_MATCHER_PATH,
            buildMemberLine(Color.PALETURQUOISE, 1.5)));
  }

  private static void addToFileSystemRelations(
      DepanFxBuiltInProject project, List<LinkDisplayEntry> displayInfo) {

    displayInfo.add(
        buildDisplayEntry(project,
            JavaLinkMatcherBuiltIns.CLASSFILE_MATCHER_PATH,
            buildMemberLine(Color.LIGHTPINK, 1.0)));

    displayInfo.add(
        buildDisplayEntry(project,
            JavaLinkMatcherBuiltIns.PACKAGEDIR_MATCHER_PATH,
            buildMemberLine(Color.LIGHTPINK, 1.5)));
  }

  private static void addMiscellaneousRelations(
      DepanFxBuiltInProject project, List<LinkDisplayEntry> displayInfo) {

    displayInfo.add(
        buildDisplayEntry(project,
            JavaLinkMatcherBuiltIns.TYPE_MATCHER_PATH,
            buildMemberLine(Color.BLUE, 1.5)));

    displayInfo.add(
        buildDisplayEntry(project,
            JavaLinkMatcherBuiltIns.ERROR_HANDLING_MATCHER_PATH,
            buildMemberLine(Color.RED, 2.0)));
  }

  private static void addModuleRelations(
      DepanFxBuiltInProject project, List<LinkDisplayEntry> displayInfo) {
    displayInfo.add(
        buildDisplayEntry(project,
            JavaLinkMatcherBuiltIns.MODULE_EXPORTED_TO_MATCHER_PATH,
            buildMemberLine(Color.YELLOW, 1.5)));

    displayInfo.add(
        buildDisplayEntry(project,
            JavaLinkMatcherBuiltIns.MODULE_EXPORTS_MATCHER_PATH,
            buildMemberLine(Color.GREEN, 1.0)));

    displayInfo.add(
        buildDisplayEntry(project,
            JavaLinkMatcherBuiltIns.MODULE_MAIN_MATCHER_PATH,
            buildMemberLine(Color.BLUE, 1.0)));

    displayInfo.add(
        buildDisplayEntry(project,
            JavaLinkMatcherBuiltIns.MODULE_PACKAGE_MATCHER_PATH,
            buildMemberLine(Color.BLUE, 1.5)));

    displayInfo.add(
        buildDisplayEntry(project,
            JavaLinkMatcherBuiltIns.MODULE_PROVIDES_MATCHER_PATH,
            buildMemberLine(Color.CORAL, 1.0)));

    displayInfo.add(
        buildDisplayEntry(project,
            JavaLinkMatcherBuiltIns.MODULE_OPENED_TO_MATCHER_PATH,
            buildMemberLine(Color.CORAL, 1.5)));

    displayInfo.add(
        buildDisplayEntry(project,
            JavaLinkMatcherBuiltIns.MODULE_OPENS_MATCHER_PATH,
            buildMemberLine(Color.GOLD, 1.0)));

    displayInfo.add(
        buildDisplayEntry(project,
            JavaLinkMatcherBuiltIns.MODULE_REQUIRES_MATCHER_PATH,
            buildMemberLine(Color.GOLD, 1.0)));

    displayInfo.add(
        buildDisplayEntry(project,
            JavaLinkMatcherBuiltIns.MODULE_REQUIRES_MATCHER_PATH,
            buildMemberLine(Color.GOLD, 1.0)));

    displayInfo.add(
        buildDisplayEntry(project,
            JavaLinkMatcherBuiltIns.MODULE_USES_MATCHER_PATH,
            buildMemberLine(Color.GOLD, 1.0)));
  }

  private static LinkDisplayEntry buildDisplayEntry(
      DepanFxBuiltInProject project, Path matcherPath,
      DepanFxLineDisplayData lineDisplay) {

    Optional<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
        matcherRsrc = project.getResource(matcherPath);

    DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> matcher =
        matcherRsrc.orElseThrow(() ->
        new DepanFxBuiltInContribution.MissingDependencyException(
            NODE_VIEW_JAVA_MEMBERS_DOC_PATH, matcherPath));

    return new LinkDisplayEntry(
        matcher.getResource().getToolName(), matcher, lineDisplay);
  }

  private static DepanFxLineDisplayData buildMemberLine(
      Color color, double width) {
    return new DepanFxLineDisplayData(
        DepanFxLineForm.STRAIGHT,
        DepanFxLineStyle.SOLID,
        DepanFxJoglColor.of(color),
        width,
        DepanFxLineLabel.DEFAULT,
        DepanFxLineArrow.NONE,
        DepanFxLineArrow.OPEN,
        DepanFxLineDirection.FORWARD);
  };

  private static LinkDisplayEntry buildDisplayEntry(
      DepanFxBuiltInProject project, Path displayDataPath,
      String entryName, Path matcherPath, DepanFxLineDisplayData lineDisplay) {

    Optional<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
        matcherRsrc = project.getResource(matcherPath);
    DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> directoryMatcher =
        matcherRsrc.orElseThrow(() ->
            new DepanFxBuiltInContribution.MissingDependencyException(
                displayDataPath, matcherPath));

    return new LinkDisplayEntry(
        entryName, directoryMatcher, lineDisplay);
  }
}
