package com.pnambic.depanfx.nodeview.persistence;

import com.pnambic.depanfx.nodeview.tooldata.DepanFxShiftLayoutData;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.persistence.plugins.DocumentPersistenceContribution;
import com.pnambic.depanfx.persistence.plugins.GraphNodePersistencePluginRegistry;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShiftLayoutDataPersistenceContribution
    implements DocumentPersistenceContribution {

  public static final String EXTENSION =
      DepanFxShiftLayoutData.SHIFT_LAYOUT_TOOL_EXT;

  public static final String SHIFT_LAYOUT_INFO_TAG = "shift-layout-info";

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
      DepanFxShiftLayoutData.class
  };

  private final GraphNodePersistencePluginRegistry graphNodeRegistry;

  @Autowired
  public ShiftLayoutDataPersistenceContribution(
      GraphNodePersistencePluginRegistry graphNodeRegistry) {
    this.graphNodeRegistry = graphNodeRegistry;
  }

  @Override
  public boolean acceptsDocument(Object document) {
    return DepanFxShiftLayoutData.class.isAssignableFrom(document.getClass());
  }

  @Override
  public boolean acceptsExt(String extText) {
    return EXTENSION.equalsIgnoreCase(extText);
  }

  @Override
  public void prepareTransport(PersistDocumentTransportBuilder builder) {
    builder.addAlias(SHIFT_LAYOUT_INFO_TAG, DepanFxShiftLayoutData.class);

    builder.addAllowedType(ALLOW_TYPES);
    graphNodeRegistry.applyExtensions(builder, DepanFxWorkspaceResource.class);
  }
}
