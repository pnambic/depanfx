package com.pnambic.depanfx.workspace.projects;

import com.pnambic.depanfx.workspace.DepanFxProjectDocument;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class DepanFxBuiltInRegistry {

  private final List<DepanFxBuiltInContribution> builtInContribs;

  @Autowired
  public DepanFxBuiltInRegistry(
      List<DepanFxBuiltInContribution> builtInContribs) {
    this.builtInContribs = builtInContribs;
  }

  public Optional<?> getBuiltInDocument(DepanFxProjectDocument projDoc) {
    return builtInContribs.stream()
        .filter(c -> c.matches(projDoc))
        .map(c -> c.getDocument())
        .findFirst();
  }

  public Stream<DepanFxBuiltInContribution> getContribs() {
    return builtInContribs.stream();
  }
}
