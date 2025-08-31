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
package com.pnambic.depanfx.edgematchers.gui;

import com.pnambic.depanfx.edgematchers.tooldata.DepanFxBaseMatcherDocument;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilterModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DepanFxEdgeMatcherDialogRegistry {

  public static final String EDGE_MATCHERS_RSRC_FILTER = "Edge Matchers";

  public interface Contribution {

    boolean accepts(DepanFxBaseMatcherDocument matcherInfo);

    /**
     *  A text used to order contributions in a display.
     */
    String getOrderKey();

    DepanFxResourceFilterModel getResourceFilter();
  }

  private final List<Contribution> contribs;

  @Autowired
  public DepanFxEdgeMatcherDialogRegistry(List<Contribution> contribs) {
    this.contribs = contribs;
  }

  public DepanFxResourceFilterModel getResourceFilter() {
    List<DepanFxResourceFilterModel> rsrcFilters = orderedContribs()
        .map(Contribution::getResourceFilter)
        .collect(Collectors.toList());

    return new DepanFxResourceFilterModel.Composite(
        EDGE_MATCHERS_RSRC_FILTER, rsrcFilters);
  }

  private Stream<Contribution> orderedContribs() {
    return contribs.stream()
        .sorted((a, b) -> a.getOrderKey().compareTo(b.getOrderKey()));
  }
}
