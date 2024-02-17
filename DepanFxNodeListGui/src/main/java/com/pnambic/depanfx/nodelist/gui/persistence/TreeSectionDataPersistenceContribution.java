package com.pnambic.depanfx.nodelist.gui.persistence;

import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxTreeSectionData;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.persistence.plugins.DocumentPersistenceContribution;
import com.pnambic.depanfx.persistence.plugins.GraphNodePersistencePluginRegistry;
import com.pnambic.depanfx.workspace.DepanFxProjectResource;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.stereotype.Component;

@Component
public class TreeSectionDataPersistenceContribution
    implements DocumentPersistenceContribution {

  public static final String EXTENSION =
      DepanFxTreeSectionData.TREE_SECTION_TOOL_EXT;

  public static final String TREE_SECTION_INFO_TAG = "tree-section-info";

  public static final String ORDER_BY_TAG = "order-by-info";

  public static final String CONTAINER_ORDER_TAG = "container-order-info";

  public static final String ORDER_DIRECTION_TAG = "order-direction-info";

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
      DepanFxTreeSectionData.class,
      DepanFxTreeSectionData.ContainerOrder.class,
      DepanFxNodeListSectionData.class,
      DepanFxNodeListSectionData.OrderBy.class,
      DepanFxNodeListSectionData.OrderDirection.class
  };

  private final GraphNodePersistencePluginRegistry graphNodeRegistry;

  public TreeSectionDataPersistenceContribution(
      GraphNodePersistencePluginRegistry graphNodeRegistry) {
    this.graphNodeRegistry = graphNodeRegistry;
  }

  @Override
  public boolean acceptsDocument(Object document) {
    return DepanFxTreeSectionData.class.isAssignableFrom(document.getClass());
  }

  @Override
  public boolean acceptsExt(String extText) {
    return EXTENSION.equalsIgnoreCase(extText);
  }

  @Override
  public void prepareTransport(PersistDocumentTransportBuilder builder) {
    builder.addAlias(TREE_SECTION_INFO_TAG, DepanFxTreeSectionData.class);
    builder.addAlias(
        CONTAINER_ORDER_TAG, DepanFxTreeSectionData.ContainerOrder.class);

    builder.addAlias(ORDER_BY_TAG, DepanFxNodeListSectionData.OrderBy.class);
    builder.addAlias(
        ORDER_DIRECTION_TAG, DepanFxNodeListSectionData.OrderDirection.class);

    builder.addAllowedType(ALLOW_TYPES);

    // Apply plugins for document elements.
    graphNodeRegistry.applyExtensions(builder, DepanFxProjectResource.class);
    graphNodeRegistry.applyExtensions(builder, DepanFxWorkspaceResource.class);
  }
}
