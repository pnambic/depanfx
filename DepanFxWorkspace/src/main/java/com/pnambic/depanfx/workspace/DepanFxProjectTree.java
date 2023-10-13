package com.pnambic.depanfx.workspace;

import java.nio.file.Path;
import java.util.Optional;

public interface DepanFxProjectTree extends DepanFxProjectMember {

  Optional<DepanFxProjectDocument> asProjectDocument(Path path);
}
