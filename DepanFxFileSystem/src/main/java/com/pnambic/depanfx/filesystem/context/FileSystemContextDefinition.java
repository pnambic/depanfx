package com.pnambic.depanfx.filesystem.context;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileSystemContextDefinition {

  public static final FileSystemContextModelId MODEL_ID =
      new FileSystemContextModelId();

  /* Nodes */
  public static final FileSystemNodeKindId DIRECTORY_NKID =
      new FileSystemNodeKindId(MODEL_ID, "Directory");

  public static final FileSystemNodeKindId DOCUMENT_NKID =
      new FileSystemNodeKindId(MODEL_ID, "Document");

  public static final FileSystemNodeKindId[] NODE_KIND_IDS =
      { DIRECTORY_NKID, DOCUMENT_NKID };

  /* Relations */
  public static final FileSystemRelationId DIRECTORY_RELID =
      new FileSystemRelationId(MODEL_ID, "directory");

  public static final FileSystemRelationId DOCUMENT_RELID =
      new FileSystemRelationId(MODEL_ID, "document");

  public static final FileSystemRelationId LINK_RELID =
      new FileSystemRelationId(MODEL_ID, "link");

  public static final FileSystemRelationId[] RELATION_IDS =
      { DIRECTORY_RELID, DOCUMENT_RELID, LINK_RELID };

  public static final FileSystemContextModel MODEL=
      new FileSystemContextModel(MODEL_ID,
          Arrays.asList(NODE_KIND_IDS),
          Arrays.asList(RELATION_IDS));

  @Bean
  public FileSystemContextModel fileSystemContextModel() {
    return MODEL;
  }
}
