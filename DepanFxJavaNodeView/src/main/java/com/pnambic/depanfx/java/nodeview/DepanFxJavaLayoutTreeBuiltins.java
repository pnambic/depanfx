package com.pnambic.depanfx.java.nodeview;

import com.pnambic.depanfx.java.context.JavaContextModelId;
import com.pnambic.depanfx.java.nodelist.link.JavaLinkMatcherBuiltIns;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLayoutData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxTreeLayoutData;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInProject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class DepanFxJavaLayoutTreeBuiltins {

  public static final Path JAVA_LAYOUT_PATH =
      DepanFxNodeViewLayoutData.LAYOUT_TOOL_PATH.resolve(
          JavaContextModelId.JAVA_KEY);

  public static final String TREE_MEMBERSHIP_TREE_LAYOUT_NAME =
      "Tree Membership Tree Layout";

  public static final Path TREE_MEMBERSHIP_TREE_LAYOUT_PATH =
      JAVA_LAYOUT_PATH.resolve(TREE_MEMBERSHIP_TREE_LAYOUT_NAME);

  public static final String PACKAGE_MEMBERSHIP_TREE_LAYOUT_NAME =
      "Package Membership Tree Layout";

  public static final Path PACKAGE_MEMBERSHIP_TREE_LAYOUT_PATH =
      JAVA_LAYOUT_PATH.resolve(PACKAGE_MEMBERSHIP_TREE_LAYOUT_NAME);

  public static final String CLASS_MEMBERSHIP_TREE_LAYOUT_NAME =
      "Class Membership Tree Layout";

  public static final Path CLASS_MEMBERSHIP_TREE_LAYOUT_PATH =
      JAVA_LAYOUT_PATH.resolve(CLASS_MEMBERSHIP_TREE_LAYOUT_NAME);

  public static final String DERIVED_CLASS_TREE_LAYOUT_NAME =
      "Derived Class Tree Layout";

  public static final Path DERIVED_CLASS_TREE_LAYOUT_PATH =
      JAVA_LAYOUT_PATH.resolve(DERIVED_CLASS_TREE_LAYOUT_NAME);

  public static final String MODULE_USES_TREE_LAYOUT_NAME =
      "Module Uses Tree Layout";

  public static final Path MODULE_USES_TREE_LAYOUT_PATH =
      JAVA_LAYOUT_PATH.resolve(MODULE_USES_TREE_LAYOUT_NAME);

  @Bean
  public DepanFxBuiltInContribution<DepanFxTreeLayoutData>
  buildTreeMembershipTreeLayout() {
    return buildLayoutContrib(
        TREE_MEMBERSHIP_TREE_LAYOUT_PATH,
        "Java Tree Membership Tree Layout",
        "Layout selected nodes based on their Java tree membership relations",
        JavaLinkMatcherBuiltIns.JAVA_TREE_MEMBER_MATCHER_PATH);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxTreeLayoutData>
  buildPackageMembershipTreeLayout() {
    return buildLayoutContrib(
        PACKAGE_MEMBERSHIP_TREE_LAYOUT_PATH,
        "Java Package Membership Tree Layout",
        "Layout selected nodes based on their Java package membership relations",
        JavaLinkMatcherBuiltIns.JAVA_PACKAGE_MEMBER_PATH);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxTreeLayoutData>
  buildClassMembershipTreeLayout() {
    return buildLayoutContrib(
        CLASS_MEMBERSHIP_TREE_LAYOUT_PATH,
        "Java Class Membership Tree Layout",
        "Layout selected nodes based on their Java class membership relations",
        JavaLinkMatcherBuiltIns.JAVA_CLASS_MEMBER_MATCHER_PATH);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxTreeLayoutData>
  buildDerivedClassTreeLayout() {
    return buildLayoutContrib(
        DERIVED_CLASS_TREE_LAYOUT_PATH,
        "Java Derived Class Tree Layout",
        "Layout selected nodes based on their Java class derivation relations",
        JavaLinkMatcherBuiltIns.JAVA_CLASS_DERIVED_MATCHER_PATH);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxTreeLayoutData>
  buildModuleUsesTreeLayout() {
    return buildLayoutContrib(
        MODULE_USES_TREE_LAYOUT_PATH,
        "Java Module Uses Tree Layout",
        "Layout selected nodes based on their Java module use relations",
        JavaLinkMatcherBuiltIns.MODULE_USES_MATCHER_PATH);
  }

  private static DepanFxBuiltInContribution.Dependent<DepanFxTreeLayoutData>
  buildLayoutContrib(
      Path layoutInfoPath,
      String layoutName,
      String layoutDescr,
      Path layoutMatcherPath) {

    return new DepanFxBuiltInContribution.Dependent<DepanFxTreeLayoutData>(
        layoutInfoPath) {

      @Override
      protected DepanFxTreeLayoutData buildDocument(
          DepanFxBuiltInProject project) {

        DepanFxTreeLayoutData result = new DepanFxTreeLayoutData(
            layoutName, layoutDescr, getResource(project, layoutMatcherPath));
        return result ;
      }
    };
  }
}
