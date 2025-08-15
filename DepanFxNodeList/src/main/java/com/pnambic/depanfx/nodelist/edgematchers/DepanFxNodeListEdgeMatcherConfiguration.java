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
package com.pnambic.depanfx.nodelist.edgematchers;

import com.pnambic.depanfx.edgematchers.link.DepanFxLinkMatchersRegistry;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxBaseMatcherDocument;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcher;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListEdgeMatcherData;
import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DepanFxNodeListEdgeMatcherConfiguration {

  @Bean
  public DepanFxLinkMatchersRegistry.Contribution nodeListMatchers() {

    return new DepanFxLinkMatchersRegistry.TransportableContribution(
        DepanFxNodeListEdgeMatcherData.class) {

      @Override
      public DepanFxLinkMatcher buildMatcher(
          DepanFxBaseMatcherDocument filterInfo) {
        if (filterInfo instanceof
            DepanFxNodeListEdgeMatcherData matcherInfo) {
          return DepanFxNodeListEdgeMatchers.createMatcher(matcherInfo);
        }
        return null;
      }

      @Override
      public void prepareTransport(
          PersistDocumentTransportBuilder builder) {
        prepareTransport(
            builder, "link-matcher", DepanFxNodeListEdgeMatcherData.class);
      }
    };
  }
}
