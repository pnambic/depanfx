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
package com.pnambic.depanfx.java.context;

import java.util.Objects;

/**
 * Graph node ide for a Java package.
 *
 * The JVM and language spec don't really recognized packages as a first
 * class entity.  However, this is a common mechansims for defining
 * structure even when it is not enforced.
 */
public class PackageNodeId extends JavaNodeId {

  private final String packagePath;

  public PackageNodeId(String packagePath) {
    super(JavaContextDefinition.PACKAGE_NKID);
    this.packagePath = packagePath;
  }

  @Override
  public String getNodeKey() {
    return packagePath;
  }

  public String getPackagePath() {
    return packagePath;
  }

  @Override
  public int hashCode() {
    return hashCodeHelper(packagePath.hashCode());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof PackageNodeId)) {
      return false;
    }
    if (!super.equals(obj)) {
      return false;
    }
    PackageNodeId other = (PackageNodeId) obj;
    return Objects.equals(packagePath, other.packagePath);
  }
}
