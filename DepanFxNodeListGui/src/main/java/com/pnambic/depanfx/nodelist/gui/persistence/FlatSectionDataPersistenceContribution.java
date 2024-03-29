package com.pnambic.depanfx.nodelist.gui.persistence;

import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxFlatSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListSectionData;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.persistence.plugins.DocumentPersistenceContribution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FlatSectionDataPersistenceContribution
    implements DocumentPersistenceContribution {

  public static final String EXTENSION =
      DepanFxFlatSectionData.FLAT_SECTION_TOOL_EXT;

  public static final String FLAT_SECTION_INFO_TAG = "flat-section-info";

  public static final String ORDER_BY_TAG = "order-by-info";

  public static final String ORDER_DIRECTION_TAG = "order-direction-info";

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
      DepanFxFlatSectionData.class,
      DepanFxNodeListSectionData.class,
      DepanFxNodeListSectionData.OrderBy.class,
      DepanFxNodeListSectionData.OrderDirection.class
  };

  @Autowired
  public FlatSectionDataPersistenceContribution() {
  }

  @Override
  public boolean acceptsDocument(Object document) {
    return DepanFxFlatSectionData.class.isAssignableFrom(document.getClass());
  }

  @Override
  public boolean acceptsExt(String extText) {
    return EXTENSION.equalsIgnoreCase(extText);
  }

  @Override
  public void prepareTransport(PersistDocumentTransportBuilder builder) {
    builder.addAllowedType(ALLOW_TYPES);

    builder.addAlias(FLAT_SECTION_INFO_TAG, DepanFxFlatSectionData.class);
    builder.addAlias(ORDER_BY_TAG, DepanFxNodeListSectionData.OrderBy.class);
    builder.addAlias(
        ORDER_DIRECTION_TAG, DepanFxNodeListSectionData.OrderDirection.class);
  }
}
