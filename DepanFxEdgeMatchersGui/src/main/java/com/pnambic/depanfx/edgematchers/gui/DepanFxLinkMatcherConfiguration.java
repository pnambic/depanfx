/*
 * Copyright 2024 The Depan Project Authors
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
package com.pnambic.depanfx.edgematchers.gui;

import com.pnambic.depanfx.edgematchers.tooldata.DepanFxBaseMatcherDocument;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilterModel;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Handles UX component registration for basic link matcher documents.
 */
@Configuration
public class DepanFxLinkMatcherConfiguration {

  public static final DepanFxResourceFilter LINK_MATCHER_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Link Matcher",
          DepanFxLinkMatcherDocument.LINK_MATCHER_TOOL_EXT,
          DepanFxLinkMatcherDocument.class);

  public static final String MATCHER_ORDER_KEY = "Matcher";

  @Bean
  public DepanFxEdgeMatcherDialogRegistry.Contribution linkMatcherContribution(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner) {
    return new LinkMatcherDialogContribution();
  }

  private static class LinkMatcherDialogContribution
      implements DepanFxEdgeMatcherDialogRegistry.Contribution {

    @Override
    public boolean accepts(DepanFxBaseMatcherDocument matcherInfo) {
      return DepanFxLinkMatcherDocument.class
          .isAssignableFrom(matcherInfo.getClass());
    }

    @Override
    public String getOrderKey() {
      return MATCHER_ORDER_KEY;
    }

    @Override
    public DepanFxResourceFilterModel getResourceFilter() {
      return LINK_MATCHER_FILTER;
    }
  }
}
