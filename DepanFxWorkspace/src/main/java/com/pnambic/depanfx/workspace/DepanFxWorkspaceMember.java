package com.pnambic.depanfx.workspace;

import java.util.Comparator;

/*
 * Root type for all things that are members of a workspace tree view.
 * These include workspaces, projects, containers, documents, and bad members.
 */
public interface DepanFxWorkspaceMember {

  String getMemberName();

  public static class Compare
      implements Comparator<DepanFxWorkspaceMember> {

    @Override
    public int compare(DepanFxWorkspaceMember one, DepanFxWorkspaceMember two) {
      return one.getMemberName().compareTo(two.getMemberName());
    }
  }

  /**
   * Be explicit about this, so it is always the same.
   */
  public static Compare COMPARE = new Compare();
}
