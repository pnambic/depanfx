package com.pnambic.depanfx.nodelist.xstream;

import com.pnambic.depanfx.nodelist.tooldata.DepanFxCategoryColumnData;
import com.pnambic.depanfx.persistence.DocumentXmlPersist;
import com.pnambic.depanfx.persistence.builder.DocumentXmlPersistBuilder;
import com.pnambic.depanfx.persistence.plugins.DocumentPersistenceContribution;
import com.pnambic.depanfx.persistence.plugins.GraphNodePersistencePluginRegistry;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryColumnDataPersistenceContribution
    implements DocumentPersistenceContribution {

  public static final String EXTENSION =
      DepanFxCategoryColumnData.CATEGORY_COLUMN_TOOL_EXT;

  public static final String CATEGORY_COLUMN_INFO_TAG = "category-column-info";

  public static final String CATEGORY_ENTRY_TAG = "category-entry";

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
      DepanFxCategoryColumnData.class,
      DepanFxCategoryColumnData.CategoryEntry.class
  };

  private final GraphNodePersistencePluginRegistry graphNodeRegistry;

  @Autowired
  public CategoryColumnDataPersistenceContribution(
      GraphNodePersistencePluginRegistry graphNodeRegistry) {
    this.graphNodeRegistry = graphNodeRegistry;
  }

  @Override
  public boolean acceptsDocument(Object document) {
    return DepanFxCategoryColumnData.class.isAssignableFrom(document.getClass());
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

    builder.addAlias(CATEGORY_COLUMN_INFO_TAG, DepanFxCategoryColumnData.class);
    builder.addAlias(
        CATEGORY_ENTRY_TAG, DepanFxCategoryColumnData.CategoryEntry.class);

    builder.addAllowedType(ALLOW_TYPES);
    graphNodeRegistry.applyExtensions(builder, DepanFxWorkspaceResource.class);
    return builder.buildDocumentXmlPersist();
  }
}
