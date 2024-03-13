package com.pnambic.depanfx.workspace.projects;

import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution.Dependent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DepanFxBuiltInRegistry {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxBuiltInRegistry.class);

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

  public void installBuiltIns(DepanFxBuiltInProject project) {
    Map<Boolean, List<DepanFxBuiltInContribution>> contribsByDependent =
        builtInContribs.stream()
            .collect(Collectors.partitioningBy(e -> e instanceof Dependent));

    // Independent ones first
    contribsByDependent.get(false).stream()
        .forEach(c -> addContribution(project, c));

    installDependentContributions(project, contribsByDependent.get(true));
  }

  public void installDependentContributions(
      DepanFxBuiltInProject project,
      List<DepanFxBuiltInContribution> dependContribs) {

    List<DepanFxBuiltInContribution> uninstalledContribs =
        closeDependentContributions(project, dependContribs);
    uninstalledContribs.stream()
        .forEach(c -> LOG.warn("Unable to install {}", c.getPath().toString()));
  }

  /**
   * Install contributions from the list of dependent contributions
   * as long as the list keeps getting shorter.
   *
   * @return the {@link List} of uninstallable contributions,
   *   or empty if all contributes where installed.
   */
  private List<DepanFxBuiltInContribution> closeDependentContributions(
      DepanFxBuiltInProject project,
      List<DepanFxBuiltInContribution> dependContribs) {

    int prevSize = dependContribs.size();
    while (dependContribs.size() > 0) {
      List<DepanFxBuiltInContribution> nextContribs = dependContribs.stream()
          // Only keep the ones that failed to build a document on this pass.
          .map(Dependent.class::cast)
          .filter(c -> !installDependent(project, c))
          .collect(Collectors.toList());
      if (nextContribs.size() >= prevSize) {
        // The ones that did not load.
        return nextContribs;
      }
      dependContribs = nextContribs;
    }
    // Loaded all dependent contributions.
    return Collections.emptyList();
  }

  /**
   * Indicates if the dependent contribution was successfully installed.
   */
  private boolean installDependent(
      DepanFxBuiltInProject project, Dependent contrib) {
    Object contribDoc = contrib.installDocument(project);
    if (contribDoc != null ) {
      addContribution(project, contrib);
      return true;
    }
    return false;
  }

  private void addContribution(
      DepanFxBuiltInProject project, DepanFxBuiltInContribution contrib) {
    Path contribPath = contrib.getPath();
    DepanFxProjectContainer parentDir =
        project.installProjectContainer(contribPath.getParent());
    DepanFxProjectDocument contribDoc =
        parentDir.getProject().asProjectDocument(contribPath).get();

    project.installContribDoc(parentDir, contribDoc, contrib);
  }
}
