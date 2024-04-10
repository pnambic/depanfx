package com.pnambic.depanfx.nodeview.persistence;

import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.persistence.plugins.DocumentPersistenceContribution;
import com.pnambic.depanfx.persistence.plugins.GraphNodePersistencePluginRegistry;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DepanFxNodeViewLinkDisplayDataPersistenceContribution
    implements DocumentPersistenceContribution {

  public static final String EXTENSION =
      DepanFxNodeViewLinkDisplayData.NODE_VIEW_LINK_DISPLAY_EXT;

  private static final String LINK_DISPLAY_INFO_TAG = "link-display-info";

  private static final String LINK_DISPLAY_ENTRY_TAG = "link-display";

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
      DepanFxNodeViewLinkDisplayData.class,
      DepanFxNodeViewLinkDisplayData.LinkDisplayEntry.class,
      DepanFxLineDisplayData.class
  };

  private final GraphNodePersistencePluginRegistry graphNodeRegistry;

  @Autowired
  public DepanFxNodeViewLinkDisplayDataPersistenceContribution(
      GraphNodePersistencePluginRegistry graphNodeRegistry) {
    this.graphNodeRegistry = graphNodeRegistry;
  }

  @Override
  public boolean acceptsDocument(Object document) {
    return DepanFxNodeViewLinkDisplayData.class.isAssignableFrom(document.getClass());
  }

  @Override
  public boolean acceptsExt(String extText) {
    return EXTENSION.equalsIgnoreCase(extText);
  }

  @Override
  public void prepareTransport(PersistDocumentTransportBuilder builder) {
    builder.addAlias(
        LINK_DISPLAY_INFO_TAG, DepanFxNodeViewLinkDisplayData.class);
    builder.addImplicitCollection(
        DepanFxNodeViewLinkDisplayData.class, "linkDisplayEntries");

    builder.addAlias(
        LINK_DISPLAY_ENTRY_TAG,
        DepanFxNodeViewLinkDisplayData.LinkDisplayEntry.class);

    builder.addAllowedType(ALLOW_TYPES);

    // Apply plugins for document elements.
    graphNodeRegistry.applyExtensions(builder, DepanFxWorkspaceResource.class);
  }
}
