package com.pnambic.depanfx.filesystem.graph;

import com.pnambic.depanfx.filesystem.context.FileSystemContextDefinition;
import com.pnambic.depanfx.filesystem.context.FileSystemContextModelId;
import com.pnambic.depanfx.graph.model.BasicGraphContextModel;
import com.pnambic.depanfx.graph.model.GraphContextModel;
import com.pnambic.depanfx.graph_doc.model.GraphContextDocument;
import com.pnambic.depanfx.graph_doc.model.GraphContextlModelDefinition;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.Arrays;

@Configuration
public class FileSystemModelDefinition {

  public static final String FILE_SYSTEM_LABEL = "File System";

  public static final BasicGraphContextModel MODEL =
      new BasicGraphContextModel(
          FileSystemContextDefinition.MODEL_ID,
          Arrays.asList(GraphContextlModelDefinition.BASE_DEPENDENCY),
          Arrays.asList(FileSystemContextDefinition.NODE_KIND_IDS),
          Arrays.asList(FileSystemRelation.RELATIONS));

  public static final GraphContextDocument DOCUMENT =
      new GraphContextDocument(
          FILE_SYSTEM_LABEL,
          "Identifies the graph elements and components for the"
              + " File System graph context.",
          MODEL);

  /**
   * Convenience for extensions built on File System models.
   */
  public static final GraphContextModel[] FILE_SYSTEM_DEPENDENCY =
      { GraphContextlModelDefinition.MODEL, MODEL };

  /** Goes into BuiltIn project under key, not label. */
  public static final Path BUILTIN_PATH =
      GraphContextDocument.CONTEXT_MODEL_PATH.resolve(
          FileSystemContextModelId.FILE_SYSTEM_KEY);

  public static final DepanFxBuiltInContribution.Simple CONTRIBUTION =
      new DepanFxBuiltInContribution.Simple(BUILTIN_PATH, DOCUMENT);

  @Bean
  public DepanFxBuiltInContribution fileSystemContextModel() {
    return CONTRIBUTION;
  }
}
