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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DepanFxInfoRegistry {

  public interface Contribution extends DepanFxOrderableContribution {

    /**
     * Used for serialization.  Should be universally unique.
     * A class name is good.
     *
     * Info id's starting "depan" are reserved by DepanFX.
     */
    String getInfoId();

    /**
     * Recognize a content by type.
     */
    boolean acceptsResource(DepanFxWorkspaceResource<?> resource);

    /**
     * Label to show to humans.
     */
    String getInfoLabel();

    /**
     * Description to show to humans.
     */
    String getInfoDescription();

    /**
     * Supply the properties supported by this info.
     */
    Stream<DepanFxNodeInfoProperty> streamProperties();

    Optional<DepanFxNodeInfoProperty> getProperty(String label);
  }

  public static abstract class Basic implements Contribution {

    private final String id;

    private final String label;

    private final String descr;

    private final Class<?> acceptType;

    private final String orderKey;

    private final List<DepanFxNodeInfoProperty> properties;

    public Basic(
        String id,
        String label,
        String descr,
        Class<?> acceptType,
        String orderKey,
        List<DepanFxNodeInfoProperty> properties) {
      this.id = id;
      this.label = label;
      this.descr = descr;
      this.acceptType = acceptType;
      this.orderKey = orderKey;
      this.properties = properties;
    }

    @Override
    public String getInfoId() {
      return id;
    }

    @Override
    public String getInfoLabel() {
      return label;
    }

    @Override
    public String getInfoDescription() {
      return descr;
    }

    @Override
    public String getOrderKey() {
      return orderKey;
    }

    @Override
    public Stream<DepanFxNodeInfoProperty> streamProperties() {
      return properties.stream();
    }

    @Override
    public Optional<DepanFxNodeInfoProperty> getProperty(String label) {
      return properties.stream()
          .filter(p -> label.equals(p.getToolName()))
          .findFirst();
    }

    @Override
    public boolean acceptsResource(DepanFxWorkspaceResource<?> resource) {
      return acceptType.isAssignableFrom(resource.getResource().getClass());
    }
  }

  // @SuppressWarnings("unused")
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

  public Optional<Contribution> getById(String id) {

    List<Contribution> idContribs = contribs.stream()
        .filter(c -> id.equals(c.getInfoId()))
        .collect(Collectors.toList());
    if (idContribs.size() > 1) {
      LOG.error("Multiple ({}) contributions for id {}",
          idContribs.size(), id);
    }
    if (!idContribs.isEmpty()) {
      return Optional.of(idContribs.get(0));
    }
    return Optional.empty();
  }

  public Stream<Contribution> streamByLabel(String label) {

    return contribs.stream()
        .filter(c -> label.equals(c.getInfoLabel()))
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
