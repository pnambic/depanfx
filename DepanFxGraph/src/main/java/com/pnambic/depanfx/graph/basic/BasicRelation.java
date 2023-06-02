/*
 * Copyright 2006 The Depan Project Authors
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
package com.pnambic.depanfx.graph.basic;

import com.pnambic.depanfx.graph.api.Relation;

public class BasicRelation<R> implements Relation<R> {

  private final String forwardName;

  private final String reverseName;

  private final R id;

  public BasicRelation(R id, String forwardName, String reverseName) {
    this.id = id;
    this.forwardName = forwardName;
    this.reverseName = reverseName;
  }

  @Override
  public R getId() {
    return id;
  }

  @Override
  public String getForwardName() {
    return forwardName;
  }

  @Override
  public String getReverseName() {
    return reverseName;
  }
}
