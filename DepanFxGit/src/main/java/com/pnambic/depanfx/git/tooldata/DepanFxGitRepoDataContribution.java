package com.pnambic.depanfx.git.tooldata;

import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.persistence.plugins.DocumentPersistenceContribution;

import org.springframework.stereotype.Component;

@Component
public class DepanFxGitRepoDataContribution
    implements DocumentPersistenceContribution {

  /**
   * Standard extension to use when loading or saving {@code NodeList}s.
   * The characters represent "DepAn Graph Info".
   */
  public static final String EXTENSION = DepanFxGitRepoData.GIT_REPO_TOOL_EXT;

  public static final String GIT_REPO_TAG = "git-repo-info";

  private static final Class<?>[] ALLOWED_TYPES = new Class[] {
      DepanFxGitRepoData.class
  };

  public DepanFxGitRepoDataContribution() {
  }

  @Override
  public boolean acceptsDocument(Object document) {
    return DepanFxGitRepoData.class.isAssignableFrom(document.getClass());
  }

  @Override
  public boolean acceptsExt(String extText) {
    return EXTENSION.equalsIgnoreCase(extText);
  }

  @Override
  public void prepareTransport(PersistDocumentTransportBuilder builder) {
    builder.addAlias(GIT_REPO_TAG, DepanFxGitRepoData.class);
    builder.addAllowedType(ALLOWED_TYPES);
  }
}
