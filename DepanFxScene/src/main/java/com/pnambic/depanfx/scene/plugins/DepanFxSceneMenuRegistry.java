/*
 * Copyright 2023 The Depan Project Authors
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
package com.pnambic.depanfx.scene.plugins;

import com.pnambic.depanfx.scene.DepanFxSceneService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;

@Component
public class DepanFxSceneMenuRegistry {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxSceneMenuRegistry.class);

  private final HashMap<String, Collection<DepanFxSceneMenuContribution>>
  menuContribs = new HashMap<>();

  @Autowired
  public DepanFxSceneMenuRegistry(
      Collection<DepanFxSceneMenuContribution> contributions) {
    installContributions(contributions);
  }

  public void dispatch(DepanFxSceneService sceneSrvc, ActionEvent event) {
    StringProperty menuItemId = ((MenuItem) event.getSource()).idProperty();
    getEventContribution(sceneSrvc, menuItemId.getValue())
        .ifPresentOrElse(
            c -> c.handleEvent(sceneSrvc, event),
            () -> LOG.info("Unable to dispatch scene menu event {}",
                      menuItemId.getValue()));
  }

  public boolean isMenuItemActive(
      DepanFxSceneService sceneSrvc, String menuItemKey) {
    return getEventContribution(sceneSrvc, menuItemKey).isPresent();
  }

  private Optional<DepanFxSceneMenuContribution> getEventContribution(
      DepanFxSceneService sceneSrvc, String menuItemKey) {
    return
        menuContribs.getOrDefault(menuItemKey, Collections.emptyList()).stream()
            .filter(c -> c.acceptsMenuItemKey(sceneSrvc, menuItemKey))
            .findFirst();
  }

  /**
   * @param contribs allows future enhancements to add more contributions.
   */
  private void installContributions(
      Collection<DepanFxSceneMenuContribution> contribs) {
    contribs.stream()
        .forEach(c -> menuContribs.computeIfAbsent(
            c.forMenuKey(),
            k -> new ArrayList<DepanFxSceneMenuContribution>())
            .add(c));
  }
}
