package com.pnambic.depanfx.nodelist.gui.persistence;

import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.persistence.plugins.DocumentPersistenceContribution;
import com.pnambic.depanfx.persistence.plugins.GraphNodePersistencePluginRegistry;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TableViewDataPersistenceContribution
    implements DocumentPersistenceContribution {

  public static final String EXTENSION =
      DepanFxNodeListTableViewData.TABLE_VIEW_TOOL_EXT;

  public static final String FLAT_SECTION_INFO_TAG = "table-view-info";

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
      DepanFxNodeListTableViewData.class
  };

  private final GraphNodePersistencePluginRegistry graphNodeRegistry;

  @Autowired
  public TableViewDataPersistenceContribution(
      GraphNodePersistencePluginRegistry graphNodeRegistry) {
    this.graphNodeRegistry = graphNodeRegistry;
  }

  @Override
  public boolean acceptsDocument(Object document) {
    return DepanFxNodeListTableViewData.class.isAssignableFrom(document.getClass());
  }

  @Override
  public boolean acceptsExt(String extText) {
    return EXTENSION.equalsIgnoreCase(extText);
  }

  @Override
  public void prepareTransport(PersistDocumentTransportBuilder builder) {
    builder.addAllowedType(ALLOW_TYPES);

    graphNodeRegistry.applyExtensions(builder, DepanFxWorkspaceResource.class);
  }
}
