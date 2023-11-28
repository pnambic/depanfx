package com.pnambic.depanfx.nodelist.xstream;

import com.pnambic.depanfx.nodelist.tooldata.DepanFxTreeSectionData;
import com.pnambic.depanfx.persistence.DocumentXmlPersist;
import com.pnambic.depanfx.persistence.builder.DocumentXmlPersistBuilder;
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

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
      DepanFxTreeSectionData.class,
      DepanFxTreeSectionData.OrderBy.class,
      DepanFxTreeSectionData.ContainerOrder.class
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
  public DocumentXmlPersist getDocumentPersist() {
    DocumentXmlPersistBuilder builder = new DocumentXmlPersistBuilder();

    builder.setXStream();
    builder.setNoReferences();

    builder.addAlias(TREE_SECTION_INFO_TAG, DepanFxTreeSectionData.class);
    builder.addAlias(ORDER_BY_TAG, DepanFxTreeSectionData.OrderBy.class);
    builder.addAlias(
        CONTAINER_ORDER_TAG, DepanFxTreeSectionData.ContainerOrder.class);

    builder.addAllowedType(ALLOW_TYPES);

    // Apply plugins for document elements.
    graphNodeRegistry.applyExtensions(builder, DepanFxProjectResource.class);
    graphNodeRegistry.applyExtensions(builder, DepanFxWorkspaceResource.class);
    return builder.buildDocumentXmlPersist();
  }
}
