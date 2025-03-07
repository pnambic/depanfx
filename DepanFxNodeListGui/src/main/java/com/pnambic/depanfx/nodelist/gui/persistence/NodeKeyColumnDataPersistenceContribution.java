/*
 * Copyright 2025 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pnambic.depanfx.nodelist.gui.persistence;

import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeKeyColumnData;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
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
  public void prepareTransport(PersistDocumentTransportBuilder builder) {
    builder.addAlias(NODE_KEY_COLUMN_INFO_TAG, DepanFxNodeKeyColumnData.class);
    builder.addAlias(
        KEY_CHOICE_TAG, DepanFxNodeKeyColumnData.KeyChoice.class);

    builder.addAllowedType(ALLOW_TYPES);
  }
}
