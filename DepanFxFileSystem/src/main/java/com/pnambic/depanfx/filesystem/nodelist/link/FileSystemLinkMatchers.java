package com.pnambic.depanfx.filesystem.nodelist.link;

import com.pnambic.depanfx.filesystem.graph.FileSystemRelation;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcher;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatchers.Composite;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatchers.ForwardRelation;

import java.util.Arrays;

public class FileSystemLinkMatchers {

  public FileSystemLinkMatchers() {
    // Prevent instantiation.
  }

  public static ForwardRelation DIRECTORY_FORWARD =
      new ForwardRelation(FileSystemRelation.CONTAINS_DIR);

  public static ForwardRelation FILE_FORWARD =
      new ForwardRelation(FileSystemRelation.CONTAINS_FILE);

  public static ForwardRelation LINK_FORWARD =
      new ForwardRelation(FileSystemRelation.SYMBOLIC_LINK);

  private static DepanFxLinkMatcher[] FILE_SYSTEM_MEMBERS =
      new DepanFxLinkMatcher [] {
          DIRECTORY_FORWARD, FILE_FORWARD, LINK_FORWARD
      };

  public static Composite MEMBER =
      new Composite(Arrays.asList(FILE_SYSTEM_MEMBERS));
}
