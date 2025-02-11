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
package com.pnambic.depanfx.session.core;

import com.pnambic.depanfx.persistence.PersistDocumentTransportBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneViewer;
import com.pnambic.depanfx.scene.DepanFxWelcomeViewer;
import com.pnambic.depanfx.session.plugins.DepanFxSceneViewerRegistry;
import com.pnambic.depanfx.session.tooldata.DepanFxWelcomeViewerData;
import com.pnambic.depanfx.session.viewdata.DepanFxBaseViewerData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Define how the welcome view is serialized.
 */
@Component
public class DepanFxWelcomeViewerContribution
    implements DepanFxSceneViewerRegistry.Contribution {

  private static final Class<?>[] ALLOWED_TYPES = new Class<?>[] {
    DepanFxWelcomeViewerData.class
  };

  private DepanFxDialogRunner dialogRunner;

  @Autowired
  public DepanFxWelcomeViewerContribution(DepanFxDialogRunner dialogRunner) {
    this.dialogRunner = dialogRunner;
  }

  @Override
  public boolean accepts(DepanFxBaseViewerData viewerData) {
    return DepanFxWelcomeViewerData.class.isAssignableFrom(
        viewerData.getClass());
  }

  @Override
  public boolean accepts(DepanFxSceneViewer viewer) {
    return DepanFxWelcomeViewer.class.isAssignableFrom(
        viewer.getClass());
  }

  @Override
  public Optional<DepanFxSceneViewer> buildViewer(
      DepanFxBaseViewerData viewData) {
    return Optional.of(new DepanFxWelcomeViewer(dialogRunner));
  }

  @Override
  public Optional<DepanFxBaseViewerData> getViewerData(
      DepanFxSceneViewer viewer) {
    return Optional.of(DepanFxWelcomeViewerData.MARKER);
  }

  @Override
  public void prepareTransport(PersistDocumentTransportBuilder builder) {
    builder.addAllowedType(ALLOWED_TYPES);
  }
}
