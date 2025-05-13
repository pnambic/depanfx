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
 package com.pnambic.depanfx.graph.nodeanno;

import com.pnambic.depanfx.base.tooldata.DepanFxBaseToolData;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DepanFxAnnotationIndexData extends DepanFxBaseToolData {

  public static final String ANNOTATION_INDEX_TOOL_EXT = "daiti";

  public static class AnnotationSpecification {

    /**
     * Value of the label used to identify the annotation specification
     * for people.
     * */
    private final String annoLabel;

    /**
     * Value of the key used to retrieve an annotation value
     * from the annotation store.  The annotation key should be unique
     * over the annotation index.
     * */
    private final String annoKey;

    /**
     * Field and type information about an annotation's value.
     */
    private final DepanFxInfoRegistry.Contribution annoInfo;

    public AnnotationSpecification(
        String annoLabel, String annoKey, DepanFxInfoRegistry.Contribution annoInfo) {
      this.annoLabel = annoLabel;
      this.annoKey = annoKey;
      this.annoInfo = annoInfo;
    }

    public String getAnnotationLabel() {
      return annoLabel;
    }

    public String getAnnotationKey() {
      return annoKey;
    }

    public DepanFxInfoRegistry.Contribution getAnnotationInfo() {
      return annoInfo;
    }
  }

  private final List<AnnotationSpecification> annos;

  public DepanFxAnnotationIndexData(
      String toolName, String toolDescription,
      Collection<AnnotationSpecification> annos) {
    super(toolName, toolDescription);
    this.annos = annos.stream().collect(Collectors.toList());
  }

  public Stream<AnnotationSpecification> streamAnnotations() {
    return annos.stream();
  }

  public Optional<AnnotationSpecification> getByAnnotationKey(String annoKey) {
    return streamAnnotations()
        .filter(a -> annoKey.equals(a.getAnnotationKey()))
        .findFirst();
  }
}
