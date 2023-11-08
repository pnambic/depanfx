package com.pnambic.depanfx.workspace;

import java.nio.file.Path;
import java.util.Optional;

public interface DepanFxProjectMember extends DepanFxWorkspaceMember {

  Path getMemberPath();

  DepanFxProjectTree getProject();

  /**
   * Provide the member's parent container, assuming there is one.
   * If there is no parent contain, the member is likely to be a root
   * member.
   */
  Optional<DepanFxProjectContainer> getParent();
}
