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

package com.pnambic.depanfx.scene;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
/**
 * A standard set of annotations for DepanFX dialog controllers.
 * You still need to use {@code @FxmlView()} to separately define the
 * {@literal .fxml} file that should be loaded when an instance of the class
 * is requested.
 *
 * This bundles a nice set of component behaviors, especially the use
 * of scope proptotype, for use in dialogs.
 */
public @interface DepanFxFxmlDialog {
}
