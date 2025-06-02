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
package com.pnambic.depanfx.nodelist.persistence;

import com.pnambic.depanfx.nodelist.tooldata.DepanFxAnnotationStoreData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxKeyPropertyStoreConverter;
import com.pnambic.depanfx.persistence.BasePersistObjectConverter;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.modxstream.XstreamMarshalContext;
import com.pnambic.modxstream.XstreamUnmarshalContext;

/**
 * Handle serialization for {@link DepanFxAnnotationStoreData}.
 */
public class AnnotationStoreDataConverter
    extends BasePersistObjectConverter<DepanFxAnnotationStoreData> {

  public static final String ANNOTATION_STORE_INFO_TAG =
      "annotation-store-info";

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
      DepanFxAnnotationStoreData.class
    };

  public static void installIn(PersistDocumentTransportBuilder builder) {
    // builder.addConverter(new AnnotationStoreDataConverter());
    // builder.addAllowedType(ALLOW_TYPES);
    builder.addAllowedType(ALLOW_TYPES);
    builder.addAliasType(
        ANNOTATION_STORE_INFO_TAG, DepanFxAnnotationStoreData.class);
    DepanFxKeyPropertyStoreConverter.installIn(builder);
  }

  @Override
  public Class<?> forType() {
    return DepanFxAnnotationStoreData.class;
  }

  @Override
  public Class<?>[] getAllowTypes() {
    return ALLOW_TYPES;
  }

  @Override
  public String getTag() {
    return ANNOTATION_STORE_INFO_TAG;
  }

  @Override
  public void marshal(XstreamMarshalContext dstContext, Object source) {
    DepanFxAnnotationStoreData store = (DepanFxAnnotationStoreData) source;
    dstContext.convertAnother(store);
  }

  @Override
  public DepanFxAnnotationStoreData unmarshal(
      XstreamUnmarshalContext srcContext) {
    DepanFxAnnotationStoreData result =
        (DepanFxAnnotationStoreData) srcContext.convertAnother(null, DepanFxAnnotationStoreData.class);
    return DepanFxAnnotationStoreData.forUnmarshal(result);
  }
}
