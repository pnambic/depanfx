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

import com.pnambic.depanfx.graph.nodeanno.DepanFxAnnotationIndexData;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry.Contribution;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.persistence.PersistObjectConverter;
import com.pnambic.depanfx.persistence.plugins.DocumentPersistenceContribution;
import com.pnambic.modxstream.XstreamMarshalContext;
import com.pnambic.modxstream.XstreamUnmarshalContext;

import org.springframework.stereotype.Component;

@Component
public class AnnotationIndexDataPersistenceContribution
    implements DocumentPersistenceContribution {

  public static final String EXTENSION =
      DepanFxAnnotationIndexData.ANNOTATION_INDEX_TOOL_EXT;

  public static final String ANNOTATION_INDEX_INFO_TAG =
      "annotation-index-info";

  public static final String ANNOTATION_SPEC_TAG =
      "annotation-spec";

  private static final String INFO_PROP_TAG = "info-prop";

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
      DepanFxAnnotationIndexData.class,
      DepanFxAnnotationIndexData.AnnotationSpecification.class
  };

  private final DepanFxInfoRegistry infoRegistry;

  public AnnotationIndexDataPersistenceContribution(
      DepanFxInfoRegistry infoRegistry) {
    this.infoRegistry = infoRegistry;
  }

  @Override
  public boolean acceptsDocument(Object document) {
    return DepanFxAnnotationIndexData.class.isAssignableFrom(
        document.getClass());
  }

  @Override
  public boolean acceptsExt(String extText) {
    return EXTENSION.equalsIgnoreCase(extText);
  }

  @Override
  public void prepareTransport(PersistDocumentTransportBuilder builder) {
    builder.addAllowedType(ALLOW_TYPES);
    builder.addAlias(ANNOTATION_INDEX_INFO_TAG,
        DepanFxAnnotationIndexData.class);
    builder.addAlias(ANNOTATION_SPEC_TAG,
        DepanFxAnnotationIndexData.AnnotationSpecification.class);
    builder.addImplicitCollection(DepanFxAnnotationIndexData.class, "annos");
    builder.addConverter(new InfoContributionConverter(infoRegistry));
    builder.addAliasType(INFO_PROP_TAG, DepanFxInfoRegistry.Contribution.class);
  }

  private class InfoContributionConverter
      implements PersistObjectConverter<DepanFxInfoRegistry.Contribution> {

    public static final String ANNO_INFO_TAG = "anno-info";

    private static final Class<?>[] ALLOW_INFO_TYPES = new Class[] {
        DepanFxInfoRegistry.Contribution.class
    };

    private final DepanFxInfoRegistry infoRegistry;

    public InfoContributionConverter(DepanFxInfoRegistry infoRegistry) {
      this.infoRegistry = infoRegistry;
    }

    @Override
    public Class<?> forType() {
      return DepanFxInfoRegistry.Contribution.class;
    }

    @Override
    public Class<?>[] getAllowTypes() {
      return ALLOW_INFO_TYPES;
    }

    @Override
    public String getTag() {
      return ANNO_INFO_TAG;
    }

    @Override
    public void marshal(XstreamMarshalContext dstContext, Object source) {
      Contribution out = (DepanFxInfoRegistry.Contribution) source;
      dstContext.convertAnother(out.getInfoId());
    }

    @Override
    public Contribution unmarshal(XstreamUnmarshalContext srcContext) {
      String infoId = srcContext.getValue();
      return infoRegistry.getById(infoId)
          .orElse(null);
    }
  }
}
