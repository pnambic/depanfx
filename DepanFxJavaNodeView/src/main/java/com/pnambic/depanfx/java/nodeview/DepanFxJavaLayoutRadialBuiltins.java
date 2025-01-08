package com.pnambic.depanfx.java.nodeview;

import com.pnambic.depanfx.java.context.JavaContextModelId;
import com.pnambic.depanfx.java.nodelist.link.JavaLinkMatcherBuiltIns;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLayoutData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxRadialLayoutData;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInProject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class DepanFxJavaLayoutRadialBuiltins {

  public static final Path JAVA_LAYOUT_PATH =
      DepanFxNodeViewLayoutData.LAYOUT_TOOL_PATH.resolve(
          JavaContextModelId.JAVA_KEY);

  public static final String TREE_MEMBERSHIP_RADIAL_LAYOUT_NAME =
      "Tree Membership Radial Layout";

  public static final Path TREE_MEMBERSHIP_RADIAL_LAYOUT_PATH =
      JAVA_LAYOUT_PATH.resolve(TREE_MEMBERSHIP_RADIAL_LAYOUT_NAME);

  public static final String PACKAGE_MEMBERSHIP_RADIAL_LAYOUT_NAME =
      "Package Membership Radial Layout";

  public static final Path PACKAGE_MEMBERSHIP_RADIAL_LAYOUT_PATH =
      JAVA_LAYOUT_PATH.resolve(PACKAGE_MEMBERSHIP_RADIAL_LAYOUT_NAME);

  public static final String CLASS_MEMBERSHIP_RADIAL_LAYOUT_NAME =
      "Class Membership Radial Layout";

  public static final Path CLASS_MEMBERSHIP_RADIAL_LAYOUT_PATH =
      JAVA_LAYOUT_PATH.resolve(CLASS_MEMBERSHIP_RADIAL_LAYOUT_NAME);

  public static final String DERIVED_CLASS_RADIAL_LAYOUT_NAME =
      "Derived Class Radial Layout";

  public static final Path DERIVED_CLASS_RADIAL_LAYOUT_PATH =
      JAVA_LAYOUT_PATH.resolve(DERIVED_CLASS_RADIAL_LAYOUT_NAME);

  public static final String MODULE_USES_RADIAL_LAYOUT_NAME =
      "Module Uses Radial Layout";

  public static final Path MODULE_USES_RADIAL_LAYOUT_PATH =
      JAVA_LAYOUT_PATH.resolve(MODULE_USES_RADIAL_LAYOUT_NAME);

  @Bean
  public DepanFxBuiltInContribution<DepanFxRadialLayoutData>
  buildTreeMembershipRadialLayout() {
    return buildLayoutContrib(
        TREE_MEMBERSHIP_RADIAL_LAYOUT_PATH,
        "Java Tree Membership Radial Layout",
        "Layout selected nodes based on their Java tree membership relations",
        JavaLinkMatcherBuiltIns.JAVA_TREE_MEMBER_MATCHER_PATH);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxRadialLayoutData>
  buildPackageMembershipRadialLayout() {
    return buildLayoutContrib(
        PACKAGE_MEMBERSHIP_RADIAL_LAYOUT_PATH,
        "Java Package Membership Radial Layout",
        "Layout selected nodes based on their Java package membership relations",
        JavaLinkMatcherBuiltIns.JAVA_PACKAGE_MEMBER_PATH);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxRadialLayoutData>
  buildClassMembershipRadialLayout() {
    return buildLayoutContrib(
        CLASS_MEMBERSHIP_RADIAL_LAYOUT_PATH,
        "Java Class Membership Radial Layout",
        "Layout selected nodes based on their Java class membership relations",
        JavaLinkMatcherBuiltIns.JAVA_CLASS_MEMBER_MATCHER_PATH);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxRadialLayoutData>
  buildDerivedClassRadialLayout() {
    return buildLayoutContrib(
        DERIVED_CLASS_RADIAL_LAYOUT_PATH,
        "Java Derived Class Radial Layout",
        "Layout selected nodes based on their Java class derivation relations",
        JavaLinkMatcherBuiltIns.JAVA_CLASS_DERIVED_MATCHER_PATH);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxRadialLayoutData>
  buildModuleUsesRadialLayout() {
    return buildLayoutContrib(
        MODULE_USES_RADIAL_LAYOUT_PATH,
        "Java Module Uses Radial Layout",
        "Layout selected nodes based on their Java module use relations",
        JavaLinkMatcherBuiltIns.MODULE_USES_MATCHER_PATH);
  }

  private static DepanFxBuiltInContribution.Dependent<DepanFxRadialLayoutData>
  buildLayoutContrib(
      Path layoutInfoPath,
      String layoutName,
      String layoutDescr,
      Path layoutMatcherPath) {

    return new DepanFxBuiltInContribution.Dependent<DepanFxRadialLayoutData>(
        layoutInfoPath) {

      @Override
      protected DepanFxRadialLayoutData buildDocument(
          DepanFxBuiltInProject project) {

        DepanFxRadialLayoutData result = new DepanFxRadialLayoutData(
            layoutName, layoutDescr, getResource(project, layoutMatcherPath));
        return result ;
      }
    };
  }
}
