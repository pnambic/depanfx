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
package com.pnambic.depanfx.nodelist.gui.columns.infos;

import com.pnambic.depanfx.scene.plugins.DepanFxOrderableContribution;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Stream;

@Component
public class DepanFxInfoRegistry {

  public interface Contribution extends DepanFxOrderableContribution {

    /**
     * Recognize a content by type.
     */
    boolean acceptsResource(DepanFxWorkspaceResource<?> resource);

    String getInfosLabel();
  }

  public static abstract class Basic implements Contribution {

    private final String label;

    private final Class<?> acceptType;

    private final String orderKey;

    public Basic(
        String label,
        Class<?> acceptType,
        String orderKey) {
      this.label = label;
      this.acceptType = acceptType;
      this.orderKey = orderKey;
    }

    @Override
    public String getInfosLabel() {
      return label;
    }

    @Override
    public String getOrderKey() {
      return orderKey;
    }

    @Override
    public boolean acceptsResource(DepanFxWorkspaceResource<?> resource) {
      return acceptType.isAssignableFrom(resource.getResource().getClass());
    }
  }

  @SuppressWarnings("unused")
  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxInfoRegistry.class);

  private final Collection<Contribution> contribs;

  @Autowired
  public DepanFxInfoRegistry(Collection<Contribution> contribs) {
    this.contribs = contribs;
  }

  public Stream<Contribution> streamContributions() {
    return contribs.stream()
        .sorted(DepanFxOrderableContribution.CONTRIB_COMPARE);
  }

  /**
   * Provide those contributions from the stream that claim to be able
   * to open the supplied document.
   */
  private Stream<Contribution> selectContributions(
      DepanFxWorkspaceResource<?> columnRsrc) {

    return contribs.stream()
        .filter(c -> c.acceptsResource(columnRsrc));
  }
}
