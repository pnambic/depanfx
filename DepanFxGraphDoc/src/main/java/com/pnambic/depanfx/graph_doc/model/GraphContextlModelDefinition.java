package com.pnambic.depanfx.graph_doc.model;

import com.pnambic.depanfx.graph.context.BaseContextDefinition;
import com.pnambic.depanfx.graph.context.BaseContextModelId;
import com.pnambic.depanfx.graph.model.BasicGraphContextModel;
import com.pnambic.depanfx.graph.model.GraphContextModel;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.Collections;

@Configuration
public class GraphContextlModelDefinition {

  public static final String BASE_CONTEXT_LABEL = "Base Context";

  public static final BasicGraphContextModel MODEL =
      new BasicGraphContextModel(
          BaseContextDefinition.MODEL_ID,
          Collections.emptyList(),
          Collections.emptyList(),
          Collections.emptyList());

  public static final GraphContextDocument DOCUMENT =
      new GraphContextDocument(
          BASE_CONTEXT_LABEL,
          "Base context for graphs before other elements.",
          MODEL);

  /**
   * Convenience for extensions built on the base.
   */
  public static final GraphContextModel[] BASE_DEPENDENCY =
      { MODEL };

  /** Goes into BuiltIn project under key, not label. */
  public static final Path BUILTIN_PATH =
      GraphContextDocument.CONTEXT_MODEL_PATH.resolve(
          BaseContextModelId.BASE_CONTEXT_KEY);

  public static final DepanFxBuiltInContribution.Simple CONTRIBUTION =
      new DepanFxBuiltInContribution.Simple(BUILTIN_PATH, DOCUMENT);

  @Bean
  public DepanFxBuiltInContribution baseContextModel() {
    return CONTRIBUTION;
  }
}
