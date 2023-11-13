package com.pnambic.depanfx.filesystem.nodelist.link;

import com.pnambic.depanfx.filesystem.context.FileSystemContextDefinition;
import com.pnambic.depanfx.filesystem.graph.FileSystemRelation;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcher;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherGroup;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatchers.Composite;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatchers.ForwardRelation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileSystemLinkMatchers {

  private static final List<DepanFxLinkMatcher> MEMBER_MATCHER_GROUP =
      Arrays.asList(new DepanFxLinkMatcher[] { DepanFxLinkMatcherGroup.MEMBER });

  public FileSystemLinkMatchers() {
    // Prevent instantiation.
  }

  public static final ForwardRelation DIRECTORY_FORWARD =
      new ForwardRelation(FileSystemRelation.CONTAINS_DIR);

  public static final ForwardRelation FILE_FORWARD =
      new ForwardRelation(FileSystemRelation.CONTAINS_FILE);

  public static final ForwardRelation LINK_FORWARD =
      new ForwardRelation(FileSystemRelation.SYMBOLIC_LINK);

  private static final List<DepanFxLinkMatcher> FILE_SYSTEM_MEMBERS =
      Arrays.asList(new DepanFxLinkMatcher [] {
          DIRECTORY_FORWARD, FILE_FORWARD, LINK_FORWARD
      });

  public static final Composite MEMBER = new Composite(FILE_SYSTEM_MEMBERS);

  public static final DepanFxLinkMatcherDocument MEMBER_DOC =
      new DepanFxLinkMatcherDocument(FileSystemContextDefinition.MODEL_ID,
          MEMBER_MATCHER_GROUP, MEMBER);

  public static final DepanFxLinkMatcherDocument DIRECTORY_DOC =
      new DepanFxLinkMatcherDocument(FileSystemContextDefinition.MODEL_ID,
          Collections.emptyList(), DIRECTORY_FORWARD);

  public static final DepanFxLinkMatcherDocument FILE_DOC =
      new DepanFxLinkMatcherDocument(FileSystemContextDefinition.MODEL_ID,
          Collections.emptyList(), FILE_FORWARD);
}
