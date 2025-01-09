package com.pnambic.depanfx.java.nodelist.link;

import com.pnambic.depanfx.graph.context.ContextNodeKindId;
import com.pnambic.depanfx.java.context.JavaContextDefinition;
import com.pnambic.depanfx.java.context.JavaContextModelId;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxNodeKindFilterData;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class JavaNodeKindFilterBuiltIns {

  public static final Path JAVA_NODE_FILTERS_PATH =
      DepanFxNodeKindFilterData.NODE_FILTERS_TOOL_PATH
          .resolve(JavaContextModelId.JAVA_KEY);

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeKindFilterData>
      classNodeKindFilter() {

    return createBuiltIn(JavaContextDefinition.CLASS_NKID);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeKindFilterData>
      fieldNodeKindFilter() {

    return createBuiltIn(JavaContextDefinition.FIELD_NKID);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeKindFilterData>
      methodNodeKindFilter() {

    return createBuiltIn(JavaContextDefinition.METHOD_NKID);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeKindFilterData>
      moduleNodeKindFilter() {

    return createBuiltIn(JavaContextDefinition.MODULE_NKID);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeKindFilterData>
      packageNodeKindFilter() {

    return createBuiltIn(JavaContextDefinition.PACKAGE_NKID);
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxNodeKindFilterData>
      parameterNodeKindFilter() {

    return createBuiltIn(JavaContextDefinition.PARAMETER_NKID);
  }

  private DepanFxBuiltInContribution<DepanFxNodeKindFilterData> createBuiltIn(
      ContextNodeKindId kindId) {

    Path docPath =
        JAVA_NODE_FILTERS_PATH.resolve(kindId.getNodeKindKey());
    DepanFxNodeKindFilterData kindFilter =
        DepanFxNodeKindFilterData.createNodeKindFilterData(kindId);
    return new DepanFxBuiltInContribution.Simple<>(docPath, kindFilter);
  }
}
