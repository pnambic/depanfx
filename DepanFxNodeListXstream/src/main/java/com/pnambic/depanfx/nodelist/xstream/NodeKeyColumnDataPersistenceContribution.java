package com.pnambic.depanfx.nodelist.xstream;

import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeKeyColumnData;
import com.pnambic.depanfx.persistence.DocumentXmlPersist;
import com.pnambic.depanfx.persistence.builder.DocumentXmlPersistBuilder;
import com.pnambic.depanfx.persistence.plugins.DocumentPersistenceContribution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NodeKeyColumnDataPersistenceContribution
    implements DocumentPersistenceContribution {

  public static final String EXTENSION =
      DepanFxNodeKeyColumnData.NODE_KEY_COLUMN_TOOL_EXT;

  public static final String NODE_KEY_COLUMN_INFO_TAG = "node-key-column-info";

  public static final String KEY_CHOICE_TAG = "key-choice-info";

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
      DepanFxNodeKeyColumnData.class,
      DepanFxNodeKeyColumnData.KeyChoice.class
  };

  @Autowired
  public NodeKeyColumnDataPersistenceContribution() {
  }

  @Override
  public boolean acceptsDocument(Object document) {
    return DepanFxNodeKeyColumnData.class.isAssignableFrom(document.getClass());
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

    builder.addAlias(NODE_KEY_COLUMN_INFO_TAG, DepanFxNodeKeyColumnData.class);
    builder.addAlias(
        KEY_CHOICE_TAG, DepanFxNodeKeyColumnData.KeyChoice.class);

    builder.addAllowedType(ALLOW_TYPES);
    return builder.buildDocumentXmlPersist();
  }
}
