package com.pnambic.depanfx.workspace;

import java.util.stream.Stream;

public interface DepanFxProjectContainer extends DepanFxProjectMember {

  Stream<DepanFxProjectMember> getMembers();
}
