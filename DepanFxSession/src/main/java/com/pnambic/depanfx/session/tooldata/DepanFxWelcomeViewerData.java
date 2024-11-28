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
package com.pnambic.depanfx.session.tooldata;

import com.pnambic.depanfx.session.viewdata.DepanFxBaseViewerData;

/**
 * A simple marker class for the welcome viewer.  It is always constructed the
 * same way, from static content, so there are no data elements to persist.
 *
 * The session modules defines this on behalf of the scene module.  The scene
 * module does not know about persistence, and the session module maintains
 * persistence across all screens.
 */
public class DepanFxWelcomeViewerData implements DepanFxBaseViewerData {

  /** Only need one instance. */
  public static final DepanFxWelcomeViewerData MARKER =
      new DepanFxWelcomeViewerData();
}
