package com.pnambic.depanfx.filesystem.graph;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pnambic.depanfx.filesystem.context.FileSystemContextDefinition;
import com.pnambic.depanfx.filesystem.context.FileSystemContextModelId;
import com.pnambic.depanfx.filesystem.context.FileSystemNodeKindId;
import com.pnambic.depanfx.graph.model.BasicGraphContextModel;
import com.pnambic.depanfx.graph.model.GraphRelation;
import com.pnambic.depanfx.graph_doc.model.GraphContextDocument;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;

@Configuration
public class FileSystemModelDefinition {

  public static final String FILE_SYSTEM_LABEL = "File System";

  public static final FileSystemNodeKindId[] NODE_KIND_IDS ={
    FileSystemContextDefinition.DIRECTORY_NKID,
    FileSystemContextDefinition.DOCUMENT_NKID
  };

  /* Relations */
  public static final GraphRelation[] RELATIONS = {
    FileSystemRelation.CONTAINS_DIR,
    FileSystemRelation.CONTAINS_FILE,
    FileSystemRelation.SYMBOLIC_LINK
  };

  public static final BasicGraphContextModel MODEL =
      new BasicGraphContextModel(
          FileSystemContextDefinition.MODEL_ID,
          Collections.emptyList(),
          Arrays.asList(NODE_KIND_IDS),
          Arrays.asList(RELATIONS));

  public static final GraphContextDocument DOCUMENT =
      new GraphContextDocument(
          FILE_SYSTEM_LABEL,
          "Identifies the graph elements and components for the"
              + " File System graph context.",
          MODEL);

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
