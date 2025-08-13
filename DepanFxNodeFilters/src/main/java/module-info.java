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
module depanfx.nodefilters {
  requires com.google.common;
  requires org.slf4j;
  requires spring.context;
  requires spring.beans;

  requires pnambic.modxstream;

  requires depanfx.graph;
  requires depanfx.graph_doc;
  requires depanfx.nodelist;
  requires depanfx.edgematchers;
  requires depanfx.persistence;
  requires depanfx.workspace;
  requires depanfx.base;

  opens com.pnambic.depanfx.nodefilters.model to spring.core;
  opens com.pnambic.depanfx.nodefilters.persistence to spring.beans;
  opens com.pnambic.depanfx.nodefilters.tooldata;

  exports com.pnambic.depanfx.nodefilters.model;
  exports com.pnambic.depanfx.nodefilters.persistence;
  exports com.pnambic.depanfx.nodefilters.tooldata;
}
