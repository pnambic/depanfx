package com.pnambic.depanfx.nodelist.gui.persistence;

import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxFocusColumnData;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.persistence.plugins.DocumentPersistenceContribution;
import com.pnambic.depanfx.persistence.plugins.GraphNodePersistencePluginRegistry;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FocusColumnDataPersistenceContribution
    implements DocumentPersistenceContribution {

  public static final String EXTENSION =
      DepanFxFocusColumnData.FOCUS_COLUMN_TOOL_EXT;

  public static final String FOCUS_COLUMN_INFO_TAG = "focus-column-info";

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
      DepanFxFocusColumnData.class
  };

  private final GraphNodePersistencePluginRegistry graphNodeRegistry;

  @Autowired
  public FocusColumnDataPersistenceContribution(
      GraphNodePersistencePluginRegistry graphNodeRegistry) {
    this.graphNodeRegistry = graphNodeRegistry;
  }

  @Override
  public boolean acceptsDocument(Object document) {
    return DepanFxFocusColumnData.class.isAssignableFrom(document.getClass());
  }

  @Override
  public boolean acceptsExt(String extText) {
    return EXTENSION.equalsIgnoreCase(extText);
  }

  @Override
  public void prepareTransport(PersistDocumentTransportBuilder builder) {
    builder.addAlias(FOCUS_COLUMN_INFO_TAG, DepanFxFocusColumnData.class);

    builder.addAllowedType(ALLOW_TYPES);
    graphNodeRegistry.applyExtensions(builder, DepanFxWorkspaceResource.class);
  }
}
