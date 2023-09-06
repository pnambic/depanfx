package com.pnambic.depanfx.filesystem.graph;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pnambic.depanfx.filesystem.context.FileSystemContextDefinition;
import com.pnambic.depanfx.filesystem.context.FileSystemNodeKindId;
import com.pnambic.depanfx.graph.model.BasicGraphContextModel;
import com.pnambic.depanfx.graph.model.GraphRelation;

@Configuration
public class FileSystemModelDefinition {

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

  public static final BasicGraphContextModel MODEL=
      new BasicGraphContextModel(
          FileSystemContextDefinition.MODEL_ID,
          Collections.emptyList(),
          Arrays.asList(NODE_KIND_IDS),
          Arrays.asList(RELATIONS));

  @Bean
  public BasicGraphContextModel fileSystemContextModel() {
    return MODEL;
  }
}
